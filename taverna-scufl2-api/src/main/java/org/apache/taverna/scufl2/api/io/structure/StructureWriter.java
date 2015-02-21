package org.apache.taverna.scufl2.api.io.structure;

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


import static org.apache.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WorkflowBundleWriter;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.api.port.ProcessorPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;


/**
 * A <code>WorkflowBundleWriter</code> that writes a {@link WorkflowBundle} in Scufl2 Structure
 * format.
 */
public class StructureWriter implements WorkflowBundleWriter {
	private StringBuilder sb;
	private URITools uriTools = new URITools();

	private void append(Named named) {
		append(" '");
		append(escapeName(named.getName()));
		append("'");
	}

	private void append(String string) {
		sb.append(string);
	}

	private void append(URI uri) {
		sb.append(" <");
		sb.append(uri.toASCIIString());
		sb.append(">");
	}

	private void appendPorts(int indent, Ported wf) {
		for (Port in : sorted(wf.getInputPorts())) {
			newLine(indent);
			append("In");
			append(in);
		}
		for (Port out : sorted(wf.getOutputPorts())) {
			newLine(indent);
			append("Out");
			append(out);
		}
	}

	/**
	 * Create a string representation of a workflow bundle. Note that this
	 * method is not thread-safe; call only via the public methods which have
	 * the right locking.
	 */
	@SuppressWarnings("unchecked")
	protected String bundleString(WorkflowBundle wb) {
		sb = new StringBuilder();
		append("WorkflowBundle");
		append(wb);

		if (wb.getMainWorkflow() != null) {
			newLine(1);
			append("MainWorkflow");
			append(wb.getMainWorkflow());
		}
		for (Workflow wf : sorted(wb.getWorkflows())) {
			newLine(1);
			append("Workflow");
			append(wf);
			appendPorts(2, wf);
			for (Processor p : sorted(wf.getProcessors())) {
				newLine(2);
				append("Processor");
				append(p);
				appendPorts(3, p);
			}

			// LINKS
			// We'll need to sort them afterwards
			List<String> links = new ArrayList<>();
			for (DataLink dl : wf.getDataLinks())
				links.add(datalink(dl.getReceivesFrom()) + " -> " + datalink(dl.getSendsTo()));
			Collections.sort(links);
			if (!links.isEmpty()) {
				newLine(2);
				append("Links");
			}
			for (String link : links) {
				newLine(3);
				append(link);
			}

			if (!wf.getControlLinks().isEmpty()) {
				newLine(2);
				append("Controls");
				List<ControlLink> controlLinks = new ArrayList<>(wf.getControlLinks());
				Collections.sort(controlLinks);
				for (ControlLink controlLink : controlLinks) {
					if (!(controlLink instanceof BlockingControlLink))
						// TODO
						continue;
					BlockingControlLink blockingControlLink = (BlockingControlLink) controlLink;
					newLine(3);
					append("block");
					append(blockingControlLink.getBlock());
					append(" until");
					append(blockingControlLink.getUntilFinished());
					append(" finish");
				}
			}
		}

		if (wb.getMainProfile() != null) {
			newLine(1);
			append("MainProfile");
			append(wb.getMainProfile());
		}

		for (Profile p : sorted(wb.getProfiles())) {
			newLine(1);
			append("Profile");
			append(p);
			for (Activity a : sorted(p.getActivities())) {
				newLine(2);
				append("Activity");
				append(a);
				newLine(3);
				append("Type");
				append(a.getType());
				appendPorts(3, a);
			}
			for (ProcessorBinding pb : p.getProcessorBindings()) {
				newLine(2);
				append("ProcessorBinding");
				append(pb);
				newLine(3);
				append("Activity");
				append(pb.getBoundActivity());
				newLine(3);
				append("Processor");
				String name = " '" + escapeName(pb.getBoundProcessor().getParent().getName());
				name = name + ":" + escapeName(pb.getBoundProcessor().getName()) + "'";
				append(name);

				List<String> links = new ArrayList<String>();
				for (ProcessorInputPortBinding ip : pb.getInputPortBindings())
					links.add("'" + escapeName(ip.getBoundProcessorPort().getName()) + "' -> '"
							+ escapeName(ip.getBoundActivityPort().getName()) + "'");
				Collections.sort(links);
				if (!links.isEmpty()) {
					newLine(3);
					append("InputPortBindings");
				}
				for (String link : links) {
					newLine(4);
					append(link);
				}

				links.clear();
				for (ProcessorOutputPortBinding ip : pb.getOutputPortBindings())
					// Note: opposite direction as for ProcessorInputPortBinding
					links.add("'" + escapeName(ip.getBoundActivityPort().getName()) + "' -> '"
							+ escapeName(ip.getBoundProcessorPort().getName()) + "'");
				Collections.sort(links);
				if (!links.isEmpty()) {
					newLine(3);
					append("OutputPortBindings");
				}
				for (String link : links) {
					newLine(4);
					append(link);
				}
			}

			for (Configuration config : p.getConfigurations()) {
				newLine(2);
				append("Configuration");
				append(config);

				newLine(3);
				if (config.getType() != null) {
					append("Type");
					append(config.getType());
				}
				newLine(3);
				append("Configures");
				if (config.getConfigures() instanceof Named) {				
					Named c = (Named) config.getConfigures();					
					String cName = "'" + escapeName(c.getClass().getSimpleName().toLowerCase());
					cName = cName + "/" + escapeName(c.getName()) + "'";
					append(" " + cName);
				} else {
					URI configuredURI = uriTools.relativeUriForBean(config.getConfigures(), p);
					append(" '" + configuredURI.toASCIIString() + "'");
				}

				newLine(4);
				append(config.getJson().toString());
			}

		}
		append("\n");
		//System.out.println(sb);
		return sb.toString();
	}

	private String datalink(Port port) {
		StringBuilder s = new StringBuilder();
		s.append("'");
		if (port instanceof ProcessorPort) {
			ProcessorPort processorPort = (ProcessorPort) port;
			s.append(escapeName(processorPort.getParent().getName()));
			s.append(":");
		}
		s.append(escapeName(port.getName()));
		s.append("'");
		return s.toString();
	}

	private String escapeName(String name) {
		return name.replace("\\", "\\\\").replace("'", "\\'")
				.replace(":", "\\:").replace("/", "\\/");
	}

	@Override
	public Set<String> getMediaTypes() {
		return Collections.singleton(TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
	}

	private void newLine(int indentLevel) {
		sb.append("\n");
		for (int i = 0; i < indentLevel; i++)
			sb.append("  ");
	}

	private <T extends Named> List<T> sorted(NamedSet<T> namedSet) {
		List<T> sorted = new ArrayList<>();
		sorted.addAll(namedSet);
		Collections.sort(sorted, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return sorted;
	}

	@Override
	public synchronized void writeBundle(WorkflowBundle wb, File destination,
			String mediaType) throws IOException {
		destination.createNewFile();
		try (BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(destination))) {
			writeBundle(wb, outputStream, mediaType);
		}
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output, "utf-8");
		try {
			writer.write(bundleString(wfBundle));
		} finally {
			writer.close();
		}
	}
}
