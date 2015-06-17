package org.apache.taverna.tavlang.iwir;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.api.port.AbstractGranularDepthPort;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;


import org.shiwa.fgi.iwir.AbstractTask;
import org.shiwa.fgi.iwir.BlockScope;
import org.shiwa.fgi.iwir.IWIR;
import org.shiwa.fgi.iwir.Task;



public class IwirReader implements WorkflowBundleReader {

	public Set<String> getMediaTypes() {
		return Collections.singleton(IwirWriter.APPLICATION_VND_SHIWA_IWIR_XML);
	}

	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		IWIR iwir = new IWIR(bundleFile);
		return readIwir(iwir);
	}

	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		// IWIR can't read streams, so make a temporary file
		File tmpFile = File.createTempFile("iwir", "xml");
		tmpFile.deleteOnExit();
		try {
			FileUtils.copyInputStreamToFile(inputStream, tmpFile);
			IWIR iwir = new IWIR(tmpFile);
			return readIwir(iwir);
		} finally {
			tmpFile.delete();
		}

	}

	public WorkflowBundle readIwir(IWIR iwir) throws ReaderException {
		WorkflowBundle workflowBundle = new WorkflowBundle();
		if (iwir.getWfname().startsWith("http://")
				|| iwir.getWfname().startsWith("https://")) {
			URI uri = URI.create(iwir.getWfname());
			workflowBundle.setGlobalBaseURI(uri);
		}
		workflowBundle.setName(workflowBundle.getName());

		AbstractTask task = iwir.getTask();
		if (!(task instanceof BlockScope)) {
			// TODO: Top-level workflow implied
			throw new ReaderException(
					"Not implemented: Top level task is not BlockScope, but "
							+ task.getClass());
		}
		BlockScope blockScope = (BlockScope) task;
		readBlockScope(blockScope, workflowBundle);

		return workflowBundle;
	}

	protected void readBlockScope(BlockScope blockScope,
			WorkflowBundle workflowBundle) throws ReaderException {
		Workflow wf = new Workflow();
		wf.setName(blockScope.getName());
		readPorts(blockScope, wf);
		readTasks(blockScope, wf);
		readLinks(blockScope, wf);

	}

	protected void readTasks(BlockScope blockScope, Workflow wf)
			throws ReaderException {
		for (AbstractTask abstTask : blockScope.getBodyTasks()) {
			if (!(abstTask instanceof Task)) {
				throw new ReaderException(
						"Not implemented: task is not a Task, but "
								+ abstTask.getClass());
			}
			Task task = (Task) abstTask;
			Processor proc = new Processor(wf, task.getName());
			readPorts(task, proc);

			if (task.getTasktype().startsWith("http://")
					|| task.getTasktype().startsWith("https://")) {
				URI activityType = URI.create(task.getTasktype());
				Profile prof = getProfile(wf.getParent());

				Activity act = new Activity(task.getName());
				act.setType(activityType);
//				act.setConfigurableType(activityType);
				prof.getActivities().addWithUniqueName(act);

				// Make activity ports
				// TODO: Make Scufl2Tools.cloneProcessorPortsToActivity() ? 
				readPorts(task, act);

				// Processor binding
				ProcessorBinding binding = new ProcessorBinding();
				binding.setName(task.getName());
				prof.getProcessorBindings().addWithUniqueName(binding);
				binding.setBoundProcessor(proc);
				binding.setBoundActivity(act);
				matchPortBindings(binding);

			}

		}

	}

	// TODO: Move to Scufl2Tools
	protected void matchPortBindings(ProcessorBinding binding) {
		for (InputActivityPort actPort : binding.getBoundActivity()
				.getInputPorts()) {
			InputProcessorPort procPort = binding.getBoundProcessor()
					.getInputPorts().getByName(actPort.getName());
			if (procPort == null) {
				continue;
			}
			ProcessorInputPortBinding portBinding = new ProcessorInputPortBinding();
			portBinding.setBoundActivityPort(actPort);
			portBinding.setBoundProcessorPort(procPort);
			binding.getInputPortBindings().add(portBinding);
		}

		for (OutputProcessorPort procPort : binding.getBoundProcessor()
				.getOutputPorts()) {
			OutputActivityPort actPort = binding.getBoundActivity()
					.getOutputPorts().getByName(procPort.getName());
			if (procPort == null) {
				continue;
			}
			ProcessorOutputPortBinding portBinding = new ProcessorOutputPortBinding();
			portBinding.setBoundActivityPort(actPort);
			portBinding.setBoundProcessorPort(procPort);
			binding.getOutputPortBindings().add(portBinding);
		}
	}

	protected Profile getProfile(WorkflowBundle workflowBundle) {
		if (workflowBundle.getMainProfile() == null) {
			workflowBundle.setMainProfile(new Profile("iwir"));
		}
		return workflowBundle.getMainProfile();
	}

	protected void readLinks(BlockScope blockScope, Workflow wf) {
		// TODO Auto-generated method stub

	}

	protected void readPorts(AbstractTask blockScope, Ported ported)
			throws ReaderException {
		// or getAllInputPorts() ?
		for (org.shiwa.fgi.iwir.InputPort inputPort : blockScope
				.getInputPorts()) {
			InputPort port;
			if (ported instanceof Workflow) {
				port = new InputWorkflowPort((Workflow) ported,
						inputPort.getName());
			} else if (ported instanceof Processor) {
				port = new InputProcessorPort((Processor) ported,
						inputPort.getName());
			} else if (ported instanceof Activity) {
				port = new InputActivityPort((Activity) ported,
						inputPort.getName());
			} else {
				throw new ReaderException("Unknown Ported subclass "
						+ ported.getClass());
			}
			port.setDepth(inputPort.getType().getNestingLevel());
		}

		for (org.shiwa.fgi.iwir.OutputPort outputPort : blockScope
				.getOutputPorts()) {
			OutputPort port;
			if (ported instanceof Workflow) {
				port = new OutputWorkflowPort((Workflow) ported,
						outputPort.getName());
			} else if (ported instanceof Processor) {
				port = new OutputProcessorPort((Processor) ported,
						outputPort.getName());
			} else if (ported instanceof Activity) {
				port = new OutputActivityPort((Activity) ported,
						outputPort.getName());
			} else {
				throw new ReaderException("Unknown Ported subclass "
						+ ported.getClass());
			}
			if (port instanceof AbstractGranularDepthPort) {
				AbstractGranularDepthPort depthPort = (AbstractGranularDepthPort) port;
				depthPort.setDepth(outputPort.getType().getNestingLevel());
				depthPort.setGranularDepth(outputPort.getType()
						.getNestingLevel());
			}
		}
	}

	public String guessMediaTypeForSignature(byte[] firstBytes) {
		String firstChars;
		try {
			firstChars = new String(firstBytes, "latin1");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		if (firstChars.contains("<IWIR")
				&& firstChars.contains("http://shiwa-workflow.eu/IWIR")) {
			return IwirWriter.APPLICATION_VND_SHIWA_IWIR_XML;
		}
		return null;
	}

}