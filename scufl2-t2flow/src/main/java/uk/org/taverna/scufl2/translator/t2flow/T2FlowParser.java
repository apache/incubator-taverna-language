package uk.org.taverna.scufl2.translator.t2flow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.common.ToBeDecided;
import uk.org.taverna.scufl2.api.container.TavernaResearchObject;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.IterationStrategy;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Dataflow;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Datalinks;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DispatchStack;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.GranularDepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.GranularDepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.IterationStrategyStack;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Link;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.LinkType;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Port;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Ports;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Processors;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Role;

@SuppressWarnings("restriction")
public class T2FlowParser {

	private Logger logger = Logger.getLogger(T2FlowParser.class);
	private JAXBContext jc;
	private Unmarshaller unmarshaller;

	public T2FlowParser() throws JAXBException {
		jc = JAXBContext.newInstance("uk.org.taverna.scufl2.xml.t2flow.jaxb",
				getClass().getClassLoader());
		unmarshaller = jc.createUnmarshaller();
	}

	@SuppressWarnings("unchecked")
	public TavernaResearchObject parseT2Flow(File t2File) throws IOException,
			ParseException, JAXBException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) unmarshaller
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	@SuppressWarnings("unchecked")
	public TavernaResearchObject parseT2Flow(InputStream t2File)
			throws IOException, JAXBException, ParseException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) unmarshaller
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	public TavernaResearchObject parseT2Flow(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow wf)
			throws ParseException {

		TavernaResearchObject ro = new TavernaResearchObject();
		for (Dataflow df : wf.getDataflow()) {
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
		throw new ParseException("Could not parse receiver " + sink);
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
		throw new ParseException("Could not parse sender " + source);
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
		for (uk.org.taverna.scufl2.xml.t2flow.jaxb.DataLink origLink : origLinks
				.getDatalink()) {
			try {
				SenderPort senderPort = findSenderPort(wf, origLink.getSource());
				ReceiverPort receiverPort = findReceiverPort(wf, origLink
						.getSink());
				DataLink newLink = new DataLink(senderPort, receiverPort);
				newLinks.add(newLink);
			} catch (ParseException ex) {
				logger.warn("Could not translate link:\n" + origLink, ex);
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
		for (AnnotatedGranularDepthPort originalPort : originalPorts.getPort()) {
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
		for (Port originalPort : originalPorts.getPort()) {
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
		for (DepthPort origPort : origPorts.getPort()) {
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
		for (GranularDepthPort origPort : origPorts.getPort()) {
			OutputProcessorPort newPort = new OutputProcessorPort(newProc,
					origPort.getName());
			newPort.setDepth(origPort.getDepth().intValue());
			newPort.setGranularDepth(origPort.getGranularDepth().intValue());
			newPorts.add(newPort);
		}
		return newPorts;
	}

	protected Set<Processor> parseProcessors(Workflow wf,
			Processors originalProcessors) {
		HashSet<Processor> newProcessors = new HashSet<Processor>();
		for (uk.org.taverna.scufl2.xml.t2flow.jaxb.Processor origProc : originalProcessors
				.getProcessor()) {
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
		return newProcessors;
	}

}
