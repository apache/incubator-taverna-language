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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WorkflowBundleWriter;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.api.port.InputPort;
import org.apache.taverna.scufl2.api.port.OutputPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.api.activity.Activity;


import org.shiwa.fgi.iwir.AbstractPort;
import org.shiwa.fgi.iwir.AbstractTask;
import org.shiwa.fgi.iwir.BlockScope;
import org.shiwa.fgi.iwir.CollectionType;
import org.shiwa.fgi.iwir.DataType;
import org.shiwa.fgi.iwir.IWIR;
import org.shiwa.fgi.iwir.SimpleType;
import org.shiwa.fgi.iwir.Task;

public class IwirWriter implements WorkflowBundleWriter {

	public static final String APPLICATION_VND_SHIWA_IWIR_XML = "application/vnd.shiwa.iwir+xml";

	WeakHashMap<Port, WeakReference<AbstractPort>> portMapping = new WeakHashMap<Port, WeakReference<AbstractPort>>();
	
	WeakHashMap<Processor, WeakReference<AbstractTask>> procMapping = new WeakHashMap<Processor, WeakReference<AbstractTask>>();
	
	
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	

	protected void addControlLinks(BlockScope workflowTask, Workflow wf,
			WorkflowBundle wfBundle) {
		for (ControlLink cl : wf.getControlLinks() ) {
			if (! (cl instanceof BlockingControlLink)) {
				continue;
			}
			BlockingControlLink bCL = (BlockingControlLink) cl;
			AbstractTask blockTask = procMapping.get(bCL.getBlock()).get();
			AbstractTask untilFinishedTask = procMapping.get(bCL.getUntilFinished()).get();
			workflowTask.addLink(untilFinishedTask, blockTask);
		}
	}

	protected void addLinks(BlockScope workflowTask, Workflow workflow,
			WorkflowBundle wfBundle) {
		for (DataLink dl : workflow.getDataLinks()) {
			if (dl.getMergePosition() != null) {
				System.err.println("Merge ports not yet supported");
				continue;
			}
			AbstractPort fromPort = portMapping.get(dl.getReceivesFrom()).get();
			AbstractPort toPort = portMapping.get(dl.getSendsTo()).get();
			workflowTask.addLink(fromPort, toPort);			
		}
	}


	protected void addPorts(AbstractTask task, Ported proc) {
		for (InputPort inPort : proc
				.getInputPorts()) {
			// TODO: Check for known binaries
			DataType type = SimpleType.STRING;
			if (inPort.getDepth() > 0) {
				type = new CollectionType(type);
			}
			org.shiwa.fgi.iwir.InputPort inputPort = new org.shiwa.fgi.iwir.InputPort(
					inPort.getName(), type);
			task.addInputPort(inputPort);
			portMapping.put(inPort, new WeakReference<AbstractPort>(inputPort));			
		}

		for (OutputPort outPort : proc
				.getOutputPorts()) {
			// TODO: Check for known binaries
			DataType type = SimpleType.STRING;
			if (outPort instanceof OutputProcessorPort
					&& ((OutputProcessorPort) outPort).getDepth() > 0) {
				type = new CollectionType(type);
			}
			// FIXME: Workflow output ports depth is *calculated* - but here we
			// force them as single string
			org.shiwa.fgi.iwir.OutputPort outputPort = new org.shiwa.fgi.iwir.OutputPort(
					outPort.getName(), type);
			task.addOutputPort(outputPort);
			portMapping.put(outPort, new WeakReference<AbstractPort>(outputPort));


		}
	}
	protected void addProcessors(BlockScope workflowTask, Workflow workflow,
			WorkflowBundle wfBundle) {
		for (Processor proc : workflow.getProcessors()) {
			Configuration config = scufl2Tools
					.configurationForActivityBoundToProcessor(proc,
							wfBundle.getMainProfile());
			Activity activity = (Activity) config.getConfigures();
			String tasktype = activity.getType().toASCIIString();
//			String tasktype = activity.getConfigurableType().toASCIIString();
			Task procTask = new Task(proc.getName(), tasktype);
			addPorts(procTask, proc);
			workflowTask.addTask(procTask);
			procMapping.put(proc, new WeakReference<AbstractTask>(procTask));
			
			// TODO: Check for nested workflows and make a BlockScope instead
			// TODO: Detect while loops
			// TODO: Detect iterations
		}
	}

	public Set<String> getMediaTypes() {
		return Collections.singleton(APPLICATION_VND_SHIWA_IWIR_XML);
	}

	public void writeBundle(WorkflowBundle wfBundle, File file, String mediaType) throws WriterException, IOException {
		IWIR iwir = bundleToIwir(wfBundle);
		System.out.println(iwir.asXMLString());
		iwir.asXMLFile(file);
	}

	public IWIR bundleToIwir(WorkflowBundle wfBundle) {
		IWIR iwir = new IWIR(wfBundle.getGlobalBaseURI().toASCIIString());

		Workflow wf = wfBundle.getMainWorkflow();
		BlockScope workflowTask = new BlockScope(wf.getName());
		iwir.setTask(workflowTask);

		addPorts(workflowTask, (Ported) wf);
		addProcessors(workflowTask, wf, wfBundle);
		addLinks(workflowTask, wf, wfBundle);
		addControlLinks(workflowTask, wf, wfBundle);
		return iwir;
	}

	public void writeBundle(WorkflowBundle bundle, OutputStream stream,
			String mediaType) throws WriterException, IOException {
		IWIR iwir = bundleToIwir(bundle);
		String xml = iwir.asXMLString();
		OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8");
		writer.append(xml);
		writer.flush();
		writer.close();
	}

}