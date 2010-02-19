package net.sf.taverna.scufl2.translator.t2flow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.scufl2.api.common.ToBeDecided;
import net.sf.taverna.scufl2.api.container.TavernaResearchObject;
import net.sf.taverna.scufl2.api.core.DataLink;
import net.sf.taverna.scufl2.api.core.IterationStrategy;
import net.sf.taverna.scufl2.api.core.Processor;
import net.sf.taverna.scufl2.api.core.Workflow;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.port.InputWorkflowPort;
import net.sf.taverna.scufl2.api.port.OutputProcessorPort;
import net.sf.taverna.scufl2.api.port.OutputWorkflowPort;
import net.sf.taverna.scufl2.api.port.ReceiverPort;
import net.sf.taverna.scufl2.api.port.SenderPort;
import net.sf.taverna.scufl2.xml.t2flow.AnnotatedGranularDepthPort;
import net.sf.taverna.scufl2.xml.t2flow.AnnotatedGranularDepthPorts;
import net.sf.taverna.scufl2.xml.t2flow.Dataflow;
import net.sf.taverna.scufl2.xml.t2flow.Datalinks;
import net.sf.taverna.scufl2.xml.t2flow.DepthPort;
import net.sf.taverna.scufl2.xml.t2flow.DepthPorts;
import net.sf.taverna.scufl2.xml.t2flow.DispatchStack;
import net.sf.taverna.scufl2.xml.t2flow.GranularDepthPort;
import net.sf.taverna.scufl2.xml.t2flow.GranularDepthPorts;
import net.sf.taverna.scufl2.xml.t2flow.IterationStrategyStack;
import net.sf.taverna.scufl2.xml.t2flow.Link;
import net.sf.taverna.scufl2.xml.t2flow.LinkType;
import net.sf.taverna.scufl2.xml.t2flow.Port;
import net.sf.taverna.scufl2.xml.t2flow.Ports;
import net.sf.taverna.scufl2.xml.t2flow.Processors;
import net.sf.taverna.scufl2.xml.t2flow.Role;
import net.sf.taverna.scufl2.xml.t2flow.WorkflowDocument;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

public class T2FlowParser {

	private Logger logger = Logger.getLogger(T2FlowParser.class);

	public TavernaResearchObject parseT2Flow(File t2File) throws XmlException,
			IOException, ParseException {
		WorkflowDocument wfDoc = WorkflowDocument.Factory.parse(t2File);
		return parseT2Flow(wfDoc.getWorkflow());
	}

	public TavernaResearchObject parseT2Flow(InputStream t2File)
			throws XmlException, IOException, ParseException {
		WorkflowDocument wfDoc = WorkflowDocument.Factory.parse(t2File);
		return parseT2Flow(wfDoc.getWorkflow());
	}

	public TavernaResearchObject parseT2Flow(
			net.sf.taverna.scufl2.xml.t2flow.Workflow wf) throws ParseException {
		TavernaResearchObject ro = new TavernaResearchObject();
		for (Dataflow df : wf.getDataflowArray()) {
			Workflow workflow = parseDataflow(df);
			if (df.getRole().equals(Role.TOP)) {
				ro.setMainWorkflow(workflow);
			}
			ro.getWorkflows().add(workflow);
		}
		return ro;
	}

	protected ReceiverPort findReceiverPort(Workflow wf, Link sink)
			throws ParseException {
		if (sink.getType().equals(LinkType.DATAFLOW)) {
			String portName = sink.getPort();
			for (OutputWorkflowPort candidate : wf.getOutputPorts()) {
				if (candidate.getName().equals(portName)) {
					return candidate;
				}
			}
			throw new ParseException("Link to unknown workflow port "
					+ portName);
		} else if (sink.getType().equals(LinkType.PROCESSOR)) {
			String processorName = sink.getProcessor();
			for (Processor processor : wf.getProcessors()) {
				if (processor.getName().equals(processorName)) {
					String portName = sink.getPort();
					for (InputProcessorPort candidate : processor
							.getInputPorts()) {
						if (candidate.getName().equals(portName)) {
							return candidate;
						}
					}
					throw new ParseException("Link to unknown port " + portName
							+ " in " + processorName);
				}
			}
			throw new ParseException("Link to unknown processor "
					+ processorName);
		} else if (sink.getType().equals(LinkType.MERGE)) {
			throw new ParseException(
					"Translation of merges not yet implemented");
		}
		throw new ParseException("Could not parse receiver " + sink.xmlText());
	}

	protected SenderPort findSenderPort(Workflow wf, Link source)
			throws ParseException {
		if (source.getType().equals(LinkType.DATAFLOW)) {
			String portName = source.getPort();
			for (InputWorkflowPort candidate : wf.getInputPorts()) {
				if (candidate.getName().equals(portName)) {
					return candidate;
				}
			}
			throw new ParseException("Link from unknown workflow port "
					+ portName);
		} else if (source.getType().equals(LinkType.PROCESSOR)) {
			String processorName = source.getProcessor();
			for (Processor processor : wf.getProcessors()) {
				if (processor.getName().equals(processorName)) {
					String portName = source.getPort();
					for (OutputProcessorPort candidate : processor
							.getOutputPorts()) {
						if (candidate.getName().equals(portName)) {
							return candidate;
						}
					}
					throw new ParseException("Link from unknown port "
							+ portName + " in " + processorName);
				}
			}
			throw new ParseException("Link from unknown processor "
					+ processorName);
		} else if (source.getType().equals(LinkType.MERGE)) {
			throw new ParseException(
					"Translation of merges not yet implemented");
		}
		throw new ParseException("Could not parse sender " + source.xmlText());
	}

	protected Workflow parseDataflow(Dataflow df) throws ParseException {
		Workflow wf = new Workflow();
		wf.setName(df.getName());
		// wf.setId(df.getId());
		wf.setInputPorts(parseInputPorts(wf, df.getInputPorts()));
		wf.setOutputPorts(parseOutputPorts(wf, df.getOutputPorts()));
		wf.setProcessors(parseProcessors(wf, df.getProcessors()));
		wf.setDatalinks(parseDatalinks(wf, df.getDatalinks()));
		// TODO: Start conditions, annotations
		return wf;
	}

	protected Set<DataLink> parseDatalinks(Workflow wf, Datalinks origLinks)
			throws ParseException {
		HashSet<DataLink> newLinks = new HashSet<DataLink>();
		for (net.sf.taverna.scufl2.xml.t2flow.DataLink origLink : origLinks
				.getDatalinkArray()) {
			try {
				SenderPort senderPort = findSenderPort(wf, origLink.getSource());
				ReceiverPort receiverPort = findReceiverPort(wf, origLink
						.getSink());
				DataLink newLink = new DataLink(senderPort, receiverPort);
				newLinks.add(newLink);
			} catch (ParseException ex) {
				logger.warn("Could not translate link:\n" + origLink.xmlText(),
						ex);
				continue;
			}
		}
		return newLinks;
	}

	protected ToBeDecided parseDispatchStack(DispatchStack dispatchStack) {
		return new ToBeDecided();
	}

	@SuppressWarnings("boxing")
	protected Set<InputWorkflowPort> parseInputPorts(Workflow wf,
			AnnotatedGranularDepthPorts originalPorts) {
		Set<InputWorkflowPort> createdPorts = new HashSet<InputWorkflowPort>();
		for (AnnotatedGranularDepthPort originalPort : originalPorts
				.getPortArray()) {
			InputWorkflowPort newPort = new InputWorkflowPort(wf, originalPort
					.getName());
			newPort.setDepth(originalPort.getDepth().intValue());
			if (!originalPort.getGranularDepth()
					.equals(originalPort.getDepth())) {
				logger.warn("Specific input port granular depth not "
						+ "supported in scufl2, port " + originalPort.getName()
						+ " has depth " + originalPort.getDepth()
						+ " and granular depth "
						+ originalPort.getGranularDepth());
			}
			createdPorts.add(newPort);
		}
		return createdPorts;
	}

	protected List<IterationStrategy> parseIterationStrategyStack(
			IterationStrategyStack originalStack) {
		List<IterationStrategy> newStack = new ArrayList<IterationStrategy>();
		// TODO: Copy iteration strategy
		return newStack;
	}

	protected Set<OutputWorkflowPort> parseOutputPorts(Workflow wf,
			Ports originalPorts) {
		Set<OutputWorkflowPort> createdPorts = new HashSet<OutputWorkflowPort>();
		for (Port originalPort : originalPorts.getPortArray()) {
			OutputWorkflowPort newPort = new OutputWorkflowPort(wf,
					originalPort.getName());
			createdPorts.add(newPort);
		}
		return createdPorts;

	}

	@SuppressWarnings("boxing")
	protected Set<InputProcessorPort> parseProcessorInputPorts(
			Processor newProc, DepthPorts origPorts) {
		Set<InputProcessorPort> newPorts = new HashSet<InputProcessorPort>();
		for (DepthPort origPort : origPorts.getPortArray()) {
			InputProcessorPort newPort = new InputProcessorPort(newProc,
					origPort.getName());
			newPort.setDepth(origPort.getDepth().intValue());
			// TODO: What about InputProcessorPort granular depth?
			newPorts.add(newPort);
		}
		return newPorts;
	}

	@SuppressWarnings("boxing")
	protected Set<OutputProcessorPort> parseProcessorOutputPorts(
			Processor newProc, GranularDepthPorts origPorts) {
		Set<OutputProcessorPort> newPorts = new HashSet<OutputProcessorPort>();
		for (GranularDepthPort origPort : origPorts.getPortArray()) {
			OutputProcessorPort newPort = new OutputProcessorPort(newProc,
					origPort.getName());
			newPort.setDepth(origPort.getDepth().intValue());
			newPort.setGranularDepth(origPort.getGranularDepth()
								.intValue());
			newPorts.add(newPort);
		}
		return newPorts;
	}

	protected Set<Processor> parseProcessors(Workflow wf,
			Processors originalProcessors) {
		HashSet<Processor> newProcessors = new HashSet<Processor>();
		for (net.sf.taverna.scufl2.xml.t2flow.Processor origProc : originalProcessors
				.getProcessorArray()) {
			Processor newProc = new Processor(wf, origProc.getName());
			newProc.setInputPorts(parseProcessorInputPorts(newProc, origProc
					.getInputPorts()));
			newProc.setOutputPorts(parseProcessorOutputPorts(newProc, origProc
					.getOutputPorts()));
			newProc.setDispatchStack(parseDispatchStack(origProc
					.getDispatchStack()));
			newProc
					.setIterationStrategyStack(parseIterationStrategyStack(origProc
							.getIterationStrategyStack()));
			newProcessors.add(newProc);
		}
		System.out.println(newProcessors);
		return newProcessors;
	}

}
