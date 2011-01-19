package uk.org.taverna.scufl2.translator.t2flow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.configurations.Configuration;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.IterationStrategy;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Activity;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
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
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Map;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Mapping;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Port;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Processors;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Raven;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Role;

public class T2FlowParser {

	private static final String T2FLOW_EXTENDED_XSD = "xsd/t2flow-extended.xsd";
	@SuppressWarnings("unused")
	private static final String T2FLOW_XSD = "xsd/t2flow.xsd";

	private static final Logger logger = Logger.getLogger(T2FlowParser.class
			.getCanonicalName());

	public static final URI ravenURI = URI
			.create("http://ns.taverna.org.uk/2010/xml/t2flow/raven/");

	public static final URI configBeanURI = URI
			.create("http://ns.taverna.org.uk/2010/xml/t2flow/configbean/");

	public static <T extends Named> T findNamed(Collection<T> namedObjects,
			String name) {
		for (T named : namedObjects) {
			if (named.getName().equals(name)) {
				return named;
			}
		}
		return null;
	}

	protected ThreadLocal<ParserState> parserState = new ThreadLocal<ParserState>() {
		@Override
		protected ParserState initialValue() {
			return new ParserState();
		};
	};

	protected Set<T2Parser> t2Parsers = null;
	protected final JAXBContext jaxbContext;
	private boolean strict = false;
	private boolean validating = false;

	public final boolean isValidating() {
		return validating;
	}

	public final void setValidating(boolean validating) {
		this.validating = validating;
	}

	protected ServiceLoader<T2Parser> discoveredT2Parsers;
	protected final ThreadLocal<Unmarshaller> unmarshaller;

	public T2FlowParser() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(
				"uk.org.taverna.scufl2.xml.t2flow.jaxb", getClass()
						.getClassLoader());
		unmarshaller = new ThreadLocal<Unmarshaller>() {
			@Override
			protected Unmarshaller initialValue() {
				try {
					return jaxbContext.createUnmarshaller();
				} catch (JAXBException e) {
					logger.log(Level.SEVERE, "Could not create unmarshaller", e);
					return null;
				}
			}
		};
	}

	protected ReceiverPort findReceiverPort(Workflow wf, Link sink)
			throws ParseException {
		String portName = sink.getPort();
		if (portName == null) {
			throw new ParseException("Port name not specified");
		}
		String processorName = sink.getProcessor();
		if (processorName == null) {
			if (sink.getType().equals(LinkType.PROCESSOR)) {
				throw new ParseException(
						"Link type was processor, but no processor name found");
			}
			OutputWorkflowPort candidate = wf.getOutputPorts().getByName(
					portName);
			if (candidate == null) {
				throw new ParseException("Link to unknown workflow port "
						+ portName);
			}
			return candidate;
		} else {
			if (sink.getType().equals(LinkType.DATAFLOW)) {
				throw new ParseException(
						"Link type was dataflow, but processor name was found");
			}
			Processor processor = wf.getProcessors().getByName(processorName);
			if (processor == null) {
				throw new ParseException("Link to unknown processor "
						+ processorName);
			}
			InputProcessorPort candidate = processor.getInputPorts().getByName(
					portName);
			if (candidate == null) {
				throw new ParseException("Link to unknown port " + portName
						+ " in " + processorName);
			}
			return candidate;
		}
	}

	protected SenderPort findSenderPort(Workflow wf, Link source)
			throws ParseException {
		if (source.getType().equals(LinkType.MERGE)) {
			throw new ParseException(
					"Link type Merge unexpected for sender ports");
		}
		String portName = source.getPort();
		if (portName == null) {
			throw new ParseException("Port name not specified");
		}
		String processorName = source.getProcessor();
		if (processorName == null) {
			if (source.getType().equals(LinkType.PROCESSOR)) {
				throw new ParseException(
						"Link type was processor, but no processor name found");
			}
			InputWorkflowPort candidate = wf.getInputPorts()
					.getByName(portName);
			if (candidate == null) {
				throw new ParseException("Link from unknown workflow port "
						+ portName);
			}
			return candidate;
		} else {
			if (source.getType().equals(LinkType.DATAFLOW)) {
				throw new ParseException(
						"Link type was dataflow, but processor name was found");
			}
			Processor processor = wf.getProcessors().getByName(processorName);
			if (processor == null) {
				throw new ParseException("Link from unknown processor "
						+ processorName);
			}
			OutputProcessorPort candidate = processor.getOutputPorts()
					.getByName(portName);
			if (candidate == null) {
				throw new ParseException("Link from unknown port " + portName
						+ " in " + processorName);
			}
			return candidate;
		}
	}

	protected T2Parser getT2Parser(URI classURI) {
		for (T2Parser t2Parser : getT2Parsers()) {
			if (t2Parser.canHandlePlugin(classURI)) {
				return t2Parser;
			}
		}
		return null;
	}

	public synchronized Set<T2Parser> getT2Parsers() {
		Set<T2Parser> parsers = t2Parsers;
		if (parsers != null) {
			return parsers;
		}
		parsers = new HashSet<T2Parser>();
		// TODO: Do we need to cache this, or is the cache in ServiceLoader
		// fast enough?
		if (discoveredT2Parsers == null) {
			discoveredT2Parsers = ServiceLoader.load(T2Parser.class);
		}
		for (T2Parser parser : discoveredT2Parsers) {
			parsers.add(parser);
		}
		return parsers;
	}

	public synchronized void setT2Parsers(Set<T2Parser> parsers) {
		this.t2Parsers = parsers;
	}

	public boolean isStrict() {
		return strict;
	}

	protected void makeProfile(uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow wf) {
		Profile profile = new Profile(wf.getProducedBy());
		profile.setParent(parserState.get().getCurrentResearchObject());
		parserState.get().getCurrentResearchObject().setMainProfile(profile);
		parserState.get().setCurrentProfile(profile);
	}

	private URI makeRavenURI(Raven raven, String className) {
		return ravenURI.resolve(raven.getGroup() + "/" + raven.getArtifact()
				+ "/" + raven.getVersion() + "/" + className);
	}

	private URI mapActivityFromRaven(Raven raven, String activityClass)
			throws ParseException {
		URI classURI = makeRavenURI(raven, activityClass);
		parserState.get().setCurrentT2Parser(null);
		T2Parser t2Parser = getT2Parser(classURI);
		if (t2Parser == null) {
			String message = "Unknown T2 activity " + classURI
					+ ", install supporting T2Parser";
			if (isStrict()) {
				throw new ParseException(message);
			} else {
				logger.warning(message);
				return classURI;
			}
		}
		t2Parser.setParserState(parserState.get());
		parserState.get().setCurrentT2Parser(t2Parser);
		return t2Parser.mapT2flowActivityToURI(classURI);
	}

	protected uk.org.taverna.scufl2.api.activity.Activity parseActivity(
			Activity origActivity) throws ParseException {
		Raven raven = origActivity.getRaven();
		String activityClass = origActivity.getClazz();
		URI activityId = mapActivityFromRaven(raven, activityClass);
		uk.org.taverna.scufl2.api.activity.Activity newActivity = new uk.org.taverna.scufl2.api.activity.Activity();
		newActivity.setConfigurableType(activityId);
		newActivity.setParent(parserState.get().getCurrentProfile());
		return newActivity;
	}

	protected void parseActivityBinding(Activity origActivity,
			int activityPosition) throws ParseException, JAXBException {
		ProcessorBinding processorBinding = new ProcessorBinding();
		processorBinding.setBoundProcessor(parserState.get()
				.getCurrentProcessor());
		parserState.get().setCurrentProcessorBinding(processorBinding);
		uk.org.taverna.scufl2.api.activity.Activity newActivity = parseActivity(origActivity);
		parserState.get().setCurrentActivity(newActivity);
		parserState.get().getCurrentProfile().getProcessorBindings()
				.add(processorBinding);
		parserState.get().getCurrentProfile().getActivities().add(newActivity);
		processorBinding.setBoundActivity(newActivity);
		processorBinding.setActivityPosition(activityPosition);

		parseActivityInputMap(origActivity.getInputMap());
		parseActivityOutputMap(origActivity.getOutputMap());

		try {
			parseActivityConfiguration(origActivity.getConfigBean());
		} catch (JAXBException e) {
			if (isStrict()) {
				throw e;
			}
			logger.log(Level.WARNING, "Can't configure activity" + newActivity,
					e);
		}

		parserState.get().setCurrentActivity(null);
		parserState.get().setCurrentProcessorBinding(null);
	}

	protected void parseActivityConfiguration(ConfigBean configBean)
			throws JAXBException, ParseException {

		Configuration configuration = null;
		if (parserState.get().getCurrentT2Parser() == null) {
			String message = "No config parser for activity "
					+ parserState.get().getCurrentActivity();
			if (isStrict()) {
				throw new ParseException(message);
			}
			return;
		}

		try {
			configuration = parserState.get().getCurrentT2Parser()
					.parseActivityConfiguration(this, configBean);
		} catch (ParseException e) {
			if (isStrict()) {
				throw e;
			}
		}
		if (configuration == null) {
			if (isStrict()) {
				throw new ParseException("No configuration returned from "
						+ parserState.get().getCurrentT2Parser()
						+ " for activity "
						+ parserState.get().getCurrentActivity());
			}
			// We'll have to fake it
			configuration = new Configuration();

			URI fallBackURI = configBeanURI.resolve(configBean.getEncoding());

			java.util.Map<URI, Set<PropertyObject>> properties = configuration
					.getPropertyResource().getProperties();
			PropertyLiteral literal = new PropertyLiteral();
			literal.setLiteralValue(configBean.getAny().toString());
			literal.setLiteralType(PropertyLiteral.XML_LITERAL);
			properties.get(fallBackURI).add(literal);

		}

		configuration.setConfigures(parserState.get().getCurrentActivity());
		parserState.get().getCurrentProfile().getConfigurations()
				.add(configuration);
	}

	public Unmarshaller getUnmarshaller() {
		Unmarshaller u = unmarshaller.get();

		if (!isValidating() && u.getSchema() != null) {
			u.setSchema(null);
		} else if (isValidating() && u.getSchema() == null) {
			// Load and set schema to validate against
			Schema schema;
			try {
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				URL t2flowExtendedXSD = getClass().getResource(
						T2FLOW_EXTENDED_XSD);
				Source schemaSource = new StreamSource(t2flowExtendedXSD
						.toURI().toASCIIString());
				schema = schemaFactory.newSchema(schemaSource);
			} catch (SAXException e) {
				throw new RuntimeException("Can't load schema "
						+ T2FLOW_EXTENDED_XSD, e);
			} catch (URISyntaxException e) {
				throw new RuntimeException("Can't find schema "
						+ T2FLOW_EXTENDED_XSD, e);
			} catch (NullPointerException e) {
				throw new RuntimeException("Can't find schema "
						+ T2FLOW_EXTENDED_XSD, e);
			}
			u.setSchema(schema);
		}
		return u;
	}

	protected void parseActivityInputMap(Map inputMap) throws ParseException {
		for (Mapping mapping : inputMap.getMap()) {
			String fromProcessorOutput = mapping.getFrom();
			String toActivityOutput = mapping.getTo();
			ProcessorInputPortBinding processorInputPortBinding = new ProcessorInputPortBinding();

			InputProcessorPort inputProcessorPort = findNamed(parserState.get()
					.getCurrentProcessor().getInputPorts(), fromProcessorOutput);
			if (inputProcessorPort == null) {
				String message = "Invalid input port binding, "
						+ "unknown processor port: " + fromProcessorOutput
						+ "->" + toActivityOutput + " in "
						+ parserState.get().getCurrentProcessor();
				if (isStrict()) {
					throw new ParseException(message);
				} else {
					logger.log(Level.WARNING, message);
					continue;
				}
			}

			InputActivityPort inputActivityPort = new InputActivityPort();
			inputActivityPort.setName(toActivityOutput);
			inputActivityPort.setParent(parserState.get().getCurrentActivity());
			parserState.get().getCurrentActivity().getInputPorts()
					.add(inputActivityPort);

			processorInputPortBinding.setBoundActivityPort(inputActivityPort);
			processorInputPortBinding.setBoundProcessorPort(inputProcessorPort);
			parserState.get().getCurrentProcessorBinding()
					.getInputPortBindings().add(processorInputPortBinding);
		}

	}

	protected void parseActivityOutputMap(Map outputMap) throws ParseException {
		for (Mapping mapping : outputMap.getMap()) {
			String fromActivityOutput = mapping.getFrom();
			String toProcessorOutput = mapping.getTo();
			ProcessorOutputPortBinding processorOutputPortBinding = new ProcessorOutputPortBinding();

			OutputProcessorPort outputProcessorPort = findNamed(parserState
					.get().getCurrentProcessor().getOutputPorts(),
					toProcessorOutput);
			if (outputProcessorPort == null) {
				String message = "Invalid output port binding, "
						+ "unknown processor port: " + fromActivityOutput
						+ "->" + toProcessorOutput + " in "
						+ parserState.get().getCurrentProcessor();
				if (isStrict()) {
					throw new ParseException(message);
				} else {
					logger.log(Level.WARNING, message);
					continue;
				}
			}

			OutputActivityPort outputActivityPort = new OutputActivityPort();
			outputActivityPort.setName(fromActivityOutput);
			outputActivityPort
					.setParent(parserState.get().getCurrentActivity());
			parserState.get().getCurrentActivity().getOutputPorts()
					.add(outputActivityPort);

			processorOutputPortBinding.setBoundActivityPort(outputActivityPort);
			processorOutputPortBinding
					.setBoundProcessorPort(outputProcessorPort);
			parserState.get().getCurrentProcessorBinding()
					.getOutputPortBindings().add(processorOutputPortBinding);
		}

	}

	protected Workflow parseDataflow(Dataflow df) throws ParseException,
			JAXBException {
		Workflow wf = new Workflow();
		parserState.get().setCurrentWorkflow(wf);
		wf.setName(df.getId());
		// wf.setId(df.getId());
		wf.setInputPorts(parseInputPorts(df.getInputPorts()));
		wf.setOutputPorts(parseOutputPorts(df.getOutputPorts()));
		wf.setProcessors(parseProcessors(df.getProcessors()));
		wf.setDataLinks(parseDatalinks(df.getDatalinks()));
		// TODO: Start conditions, annotations
		parserState.get().setCurrentWorkflow(null);
		return wf;
	}

	protected Set<DataLink> parseDatalinks(Datalinks origLinks)
			throws ParseException {
		HashSet<DataLink> newLinks = new HashSet<DataLink>();
		java.util.Map<ReceiverPort, AtomicInteger> mergeCounts = new HashMap<ReceiverPort, AtomicInteger>();
		for (uk.org.taverna.scufl2.xml.t2flow.jaxb.DataLink origLink : origLinks
				.getDatalink()) {
			try {
				SenderPort senderPort = findSenderPort(parserState.get()
						.getCurrentWorkflow(), origLink.getSource());
				ReceiverPort receiverPort = findReceiverPort(parserState.get()
						.getCurrentWorkflow(), origLink.getSink());

				DataLink newLink = new DataLink(parserState.get()
						.getCurrentWorkflow(), senderPort, receiverPort);

				AtomicInteger mergeCount = mergeCounts.get(receiverPort);
				if (origLink.getSink().getType().equals(LinkType.MERGE)) {
					if (mergeCount != null && mergeCount.intValue() < 1) {
						throw new ParseException(
								"Merged and non-merged links to port "
										+ receiverPort);
					}
					if (mergeCount == null) {
						mergeCount = new AtomicInteger(0);
						mergeCounts.put(receiverPort, mergeCount);
					}
					newLink.setMergePosition(mergeCount.getAndIncrement());
				} else {
					if (mergeCount != null) {
						throw new ParseException(
								"More than one link to non-merged port "
										+ receiverPort);
					}
					mergeCounts.put(receiverPort, new AtomicInteger(-1));
				}
				newLinks.add(newLink);
			} catch (ParseException ex) {
				logger.log(Level.WARNING, "Could not translate link:\n"
						+ origLink, ex);
				if (isStrict()) {
					throw ex;
				}
				continue;
			}
		}
		return newLinks;
	}

	protected uk.org.taverna.scufl2.api.dispatchstack.DispatchStack parseDispatchStack(
			DispatchStack dispatchStack) {
		return null;
	}

	@SuppressWarnings("boxing")
	protected Set<InputWorkflowPort> parseInputPorts(
			AnnotatedGranularDepthPorts originalPorts) throws ParseException {
		Set<InputWorkflowPort> createdPorts = new HashSet<InputWorkflowPort>();
		for (AnnotatedGranularDepthPort originalPort : originalPorts.getPort()) {
			InputWorkflowPort newPort = new InputWorkflowPort(parserState.get()
					.getCurrentWorkflow(), originalPort.getName());
			newPort.setDepth(originalPort.getDepth().intValue());
			if (!originalPort.getGranularDepth()
					.equals(originalPort.getDepth())) {
				String message = "Specific input port granular depth not "
						+ "supported in scufl2, port " + originalPort.getName()
						+ " has depth " + originalPort.getDepth()
						+ " and granular depth "
						+ originalPort.getGranularDepth();
				logger.log(Level.WARNING, message);
				if (isStrict()) {
					throw new ParseException(message);
				}
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

	protected Set<OutputWorkflowPort> parseOutputPorts(
			AnnotatedPorts originalPorts) {
		Set<OutputWorkflowPort> createdPorts = new HashSet<OutputWorkflowPort>();
		for (Port originalPort : originalPorts.getPort()) {
			OutputWorkflowPort newPort = new OutputWorkflowPort(parserState
					.get().getCurrentWorkflow(), originalPort.getName());
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

	protected Set<Processor> parseProcessors(Processors originalProcessors)
			throws ParseException, JAXBException {
		HashSet<Processor> newProcessors = new HashSet<Processor>();
		for (uk.org.taverna.scufl2.xml.t2flow.jaxb.Processor origProc : originalProcessors
				.getProcessor()) {
			Processor newProc = new Processor(parserState.get()
					.getCurrentWorkflow(), origProc.getName());
			parserState.get().setCurrentProcessor(newProc);
			newProc.setInputPorts(parseProcessorInputPorts(newProc,
					origProc.getInputPorts()));
			newProc.setOutputPorts(parseProcessorOutputPorts(newProc,
					origProc.getOutputPorts()));
			newProc.setDispatchStack(parseDispatchStack(origProc
					.getDispatchStack()));
			newProc.setIterationStrategyStack(parseIterationStrategyStack(origProc
					.getIterationStrategyStack()));
			newProcessors.add(newProc);
			int i = 0;
			for (Activity origActivity : origProc.getActivities().getActivity()) {
				parseActivityBinding(origActivity, i++);
			}
		}
		parserState.get().setCurrentProcessor(null);
		return newProcessors;
	}

	@SuppressWarnings("unchecked")
	public WorkflowBundle parseT2Flow(File t2File) throws IOException,
			ParseException, JAXBException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) getUnmarshaller()
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	@SuppressWarnings("unchecked")
	public WorkflowBundle parseT2Flow(InputStream t2File) throws IOException,
			JAXBException, ParseException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) getUnmarshaller()
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	public WorkflowBundle parseT2Flow(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow wf)
			throws ParseException, JAXBException {
		try {
			WorkflowBundle ro = new WorkflowBundle();
			parserState.get().setCurrentResearchObject(ro);
			makeProfile(wf);

			for (Dataflow df : wf.getDataflow()) {
				Workflow workflow = parseDataflow(df);
				if (df.getRole().equals(Role.TOP)) {
					ro.setMainWorkflow(workflow);
				}
				ro.getWorkflows().add(workflow);
			}
			if (isStrict() && ro.getMainWorkflow() == null) {
				throw new ParseException("No main workflow");
			}
			return ro;
		} finally {
			parserState.remove();
		}
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

}
