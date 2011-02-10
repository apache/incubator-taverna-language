package uk.org.taverna.scufl2.api.io.structure;

import static uk.org.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;

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
import java.util.Map.Entry;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WorkflowBundleWriter;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;

public class StructureWriter implements WorkflowBundleWriter {

	private StringBuffer sb;

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

	@SuppressWarnings("unchecked")
	protected String bundleString(WorkflowBundle wb) {
		sb = new StringBuffer();
		append("WorkflowBundle");
		append(wb);

		newLine(1);
		append("MainWorkflow");
		append(wb.getMainWorkflow());
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
			List<String> links = new ArrayList<String>();
			for (DataLink dl : wf.getDataLinks()) {
				links.add(datalink(dl.getReceivesFrom()) + " -> "
						+ datalink(dl.getSendsTo()));
			}
			Collections.sort(links);
			if (!links.isEmpty()) {
				newLine(2);
				append("Links");
			}
			for (String link : links) {
				newLine(3);
				append(link);
			}

			List<ControlLink> controlLinks = new ArrayList<ControlLink>(
					wf.getControlLinks());
			if (!controlLinks.isEmpty()) {
				newLine(2);
				append("Controls");
				Collections.sort(controlLinks);
				String link = "block {1} until {2} finish";
				for (ControlLink controlLink : controlLinks) {
					if (!(controlLink instanceof BlockingControlLink)) {
						// TODO
						continue;
					}
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

		newLine(1);
		append("MainProfile");
		append(wb.getMainProfile());

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
				append(a.getConfigurableType());
				appendPorts(3, a);

				newLine(2);
			}
			for (ProcessorBinding pb : p.getProcessorBindings()) {
				// TODO: Sort processor bindings
				append("ProcessorBinding");
				newLine(3);
				append("Activity");
				append(pb.getBoundActivity());
				newLine(3);
				append("Processor");
				String name = " '"
					+ escapeName(pb.getBoundProcessor().getParent()
							.getName());
				name = name + ":"
				+ escapeName(pb.getBoundProcessor().getName()) + "'";
				append(name);

				List<String> links = new ArrayList<String>();
				for (ProcessorInputPortBinding ip : pb.getInputPortBindings()) {
					links.add("'"
							+ escapeName(ip.getBoundProcessorPort().getName())
							+ "' -> '"
							+ escapeName(ip.getBoundActivityPort().getName())
							+ "'");
				}
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
				for (ProcessorOutputPortBinding ip : pb.getOutputPortBindings()) {
					// Note: opposite direction as for ProcessorInputPortBinding
					links.add("'"
							+ escapeName(ip.getBoundActivityPort().getName())
							+ "' -> '"
							+ escapeName(ip.getBoundProcessorPort().getName())
							+ "'");
				}
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
				append("Type");
				append(config.getPropertyResource().getTypeURI());

				newLine(3);
				append("Configures");
				Named c = (Named) config.getConfigures();
				// FIXME: Handle activity/dispatchlayer etc. individually
				String cName = "'"
					+ escapeName(c.getClass().getSimpleName().toLowerCase());
				cName = cName + "/" + escapeName(c.getName()) + "'";
				append(" " + cName);

				for (Entry<URI, Set<PropertyObject>> prop : config
						.getPropertyResource().getProperties().entrySet()) {
					newLine(3);
					append("Property");
					append(prop.getKey());

					for (PropertyObject po : prop.getValue()) {
						if (po instanceof PropertyLiteral) {
							PropertyLiteral lit = (PropertyLiteral) po;
							newLine(4);
							append("'''");
							append(lit.getLiteralValue().replace("'''",
							"\\'\\'\\'"));
							append("'''");

							// TODO: Handle literal types
						} else {
							// TODO: Handle other props, recursively
						}

					}
				}
			}

		}
		append("\n");
		System.out.println(sb);
		return sb.toString();
	}

	private String datalink(Port port) {
		StringBuffer s = new StringBuffer();
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
		for (int i = 0; i < indentLevel; i++) {
			sb.append("  ");
		}
	}

	private <T extends Named> List<T> sorted(NamedSet<T> namedSet) {
		List<T> sorted = new ArrayList<T>();
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

		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(destination));
		writeBundle(wb, outputStream, mediaType);
		outputStream.close();

	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output, "utf-8");
		writer.write(bundleString(wfBundle));
		writer.close();
	}

}
