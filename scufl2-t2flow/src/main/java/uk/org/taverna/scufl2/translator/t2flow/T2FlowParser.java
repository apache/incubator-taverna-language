package uk.org.taverna.scufl2.translator.t2flow;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import uk.org.taverna.scufl2.api.activity.ActivityType;
import uk.org.taverna.scufl2.api.activity.InputActivityPort;
import uk.org.taverna.scufl2.api.activity.OutputActivityPort;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.ToBeDecided;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.DataProperty;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
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
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Activity;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Dataflow;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DataflowConfig;
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

	protected Set<T2Parser> additionalParsers = new HashSet<T2Parser>();
	protected ThreadLocal<uk.org.taverna.scufl2.api.activity.Activity> currentActivity = new ThreadLocal<uk.org.taverna.scufl2.api.activity.Activity>();
	protected ThreadLocal<Profile> currentProfile = new ThreadLocal<Profile>();
	protected ThreadLocal<Processor> currentProcessor = new ThreadLocal<Processor>();
	protected ThreadLocal<ProcessorBinding> currentProcessorBinding = new ThreadLocal<ProcessorBinding>();
	protected ThreadLocal<WorkflowBundle> currentResearchObject = new ThreadLocal<WorkflowBundle>();
	protected ThreadLocal<T2Parser> currentT2Parser = new ThreadLocal<T2Parser>();
	protected ThreadLocal<Workflow> currentWorkflow = new ThreadLocal<Workflow>();
	protected final JAXBContext jaxbContext;
	private boolean strict = false;
	private boolean validating = false;

	public final boolean isValidating() {
		return validating;
	}

	public final void setValidating(boolean validating) {
		this.validating = validating;
	}

	protected final ServiceLoader<T2Parser> t2Parsers;
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
					logger
							.log(Level.SEVERE, "Could not create unmarshaller",
									e);
					return null;
				}
			}
		};
		t2Parsers = ServiceLoader.load(T2Parser.class);
	}

	public void addT2Parser(T2Parser t2Parser) {
		synchronized (additionalParsers) {
			additionalParsers.add(t2Parser);
		}
	}

	protected ReceiverPort findReceiverPort(Workflow wf, Link sink)
			throws ParseException {
		if (sink.getType().equals(LinkType.DATAFLOW)) {
			String portName = sink.getPort();
			OutputWorkflowPort candidate = wf.getOutputPorts().getByName(
					portName);
			if (candidate == null) {
				throw new ParseException("Link to unknown workflow port "
						+ portName);
			}
			return candidate;
		} else if (sink.getType().equals(LinkType.PROCESSOR)) {
			String processorName = sink.getProcessor();
			Processor processor = wf.getProcessors().getByName(processorName);
			if (processor == null) {
				throw new ParseException("Link to unknown processor "
						+ processorName);
			}
			String portName = sink.getPort();
			InputProcessorPort candidate = processor.getInputPorts().getByName(
					portName);
			if (candidate == null) {
				throw new ParseException("Link to unknown port " + portName
						+ " in " + processorName);
			}
			return candidate;
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
			InputWorkflowPort candidate = wf.getInputPorts()
					.getByName(portName);
			if (candidate == null) {
				throw new ParseException("Link from unknown workflow port "
						+ portName);
			}
			return candidate;
		} else if (source.getType().equals(LinkType.PROCESSOR)) {
			String processorName = source.getProcessor();
			Processor processor = wf.getProcessors().getByName(processorName);
			if (processor == null) {
				throw new ParseException("Link from unknown processor "
						+ processorName);
			}
			String portName = source.getPort();
			OutputProcessorPort candidate = processor.getOutputPorts()
					.getByName(portName);
			if (candidate == null) {
				throw new ParseException("Link from unknown port " + portName
						+ " in " + processorName);
			}
			return candidate;
		} else if (source.getType().equals(LinkType.MERGE)) {
			throw new ParseException(
					"Translation of merges not yet implemented");
		}
		throw new ParseException("Could not parse sender " + source);
	}

	protected T2Parser getT2Parser(URI classURI) {
		for (T2Parser t2Parser : getT2Parsers()) {
			if (t2Parser.canHandlePlugin(classURI)) {
				return t2Parser;
			}
		}
		return null;
	}

	private Set<T2Parser> getT2Parsers() {
		Set<T2Parser> parsers = new HashSet<T2Parser>();
		// TODO: Do we need to cache this, or is the cache in ServiceLoader fast
		// enough?
		synchronized (t2Parsers) {
			for (T2Parser parser : t2Parsers) {
				parsers.add(parser);
			}
		}
		synchronized (additionalParsers) {
			parsers.addAll(additionalParsers);
		}
		return parsers;
	}

	public boolean isStrict() {
		return strict;
	}

	protected void makeDefaultBindings(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow wf) {
		Profile profile = new Profile(wf.getProducedBy());
		currentResearchObject.get().getProfiles().add(profile);
		currentProfile.set(profile);
	}

	private URI makeRavenURI(Raven raven, String className) {
		return ravenURI.resolve(raven.getGroup() + "/" + raven.getArtifact()
				+ "/" + raven.getVersion() + "/" + className);
	}

	private URI mapActivityFromRaven(Raven raven, String activityClass)
			throws ParseException {
		URI classURI = makeRavenURI(raven, activityClass);
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
		currentT2Parser.set(t2Parser);
		return t2Parser.mapT2flowActivityToURI(classURI);
	}

	protected uk.org.taverna.scufl2.api.activity.Activity parseActivity(
			Activity origActivity) throws ParseException {
		Raven raven = origActivity.getRaven();
		String activityClass = origActivity.getClazz();
		URI activityId = mapActivityFromRaven(raven, activityClass);
		uk.org.taverna.scufl2.api.activity.Activity newActivity = new uk.org.taverna.scufl2.api.activity.Activity();
		newActivity.setType(new ActivityType(activityId.toASCIIString()));
		return newActivity;
	}

	protected void parseActivityBinding(Activity origActivity,
			int activityPosition)
			throws ParseException, JAXBException {
		ProcessorBinding processorBinding = new ProcessorBinding();
		processorBinding.setBoundProcessor(currentProcessor.get());
		currentProcessorBinding.set(processorBinding);
		uk.org.taverna.scufl2.api.activity.Activity newActivity = parseActivity(origActivity);
		currentActivity.set(newActivity);
		currentProfile.get().getProcessorBindings().add(processorBinding);
		currentProfile.get().getActivities().add(newActivity);
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

		currentActivity.remove();
		currentProcessorBinding.remove();
	}

	protected void parseActivityConfiguration(ConfigBean configBean)
			throws JAXBException, ParseException {

		Configuration configuration = null;
		if (currentT2Parser.get() == null) {
			String message = "No config parser for activity "
					+ currentActivity.get();
			if (isStrict()) {
				throw new ParseException(message);
			}
		}
		try {
			configuration = currentT2Parser.get().parseActivityConfiguration(
					this, configBean);
		} catch (ParseException e) {
			if (isStrict()) {
				throw e;
			}
		}
		if (configuration == null) {
			if (isStrict()) {
				throw new ParseException("No configuration returned from "
						+ currentT2Parser.get() + " for activity "
						+ currentActivity.get());
			}
			// We'll have to fake it
			configuration = new Configuration();

			URI fallBackURI = configBeanURI.resolve(configBean.getEncoding());

			DataProperty property = new DataProperty();
			property.setPredicate(fallBackURI);
			// FIXME: Can't do toString() on getAny()
			property.setDataValue(configBean.getAny().toString());
			configuration.getProperties().add(property);
		}

		configuration.setConfigures(currentActivity.get());
		currentProfile.get().getConfigurations().add(configuration);
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

			InputProcessorPort inputProcessorPort = findNamed(currentProcessor
					.get().getInputPorts(), fromProcessorOutput);
			if (inputProcessorPort == null) {
				String message = "Invalid input port binding, "
						+ "unknown processor port: " + fromProcessorOutput
						+ "->" + toActivityOutput + " in "
						+ currentProcessor.get();
				if (isStrict()) {
					throw new ParseException(message);
				} else {
					logger.log(Level.WARNING, message);
					continue;
				}
			}

			InputActivityPort inputActivityPort = new InputActivityPort();
			inputActivityPort.setName(toActivityOutput);
			inputActivityPort.setParent(currentActivity.get());
			currentActivity.get().getInputPorts().add(inputActivityPort);

			processorInputPortBinding.setBoundActivityPort(inputActivityPort);
			processorInputPortBinding.setBoundProcessorPort(inputProcessorPort);
			currentProcessorBinding.get().getInputPortBindings().add(
					processorInputPortBinding);
		}

	}

	protected void parseActivityOutputMap(Map outputMap) throws ParseException {
		for (Mapping mapping : outputMap.getMap()) {
			String fromActivityOutput = mapping.getFrom();
			String toProcessorOutput = mapping.getTo();
			ProcessorOutputPortBinding processorOutputPortBinding = new ProcessorOutputPortBinding();

			OutputProcessorPort outputProcessorPort = findNamed(
					currentProcessor.get().getOutputPorts(), toProcessorOutput);
			if (outputProcessorPort == null) {
				String message = "Invalid output port binding, "
						+ "unknown processor port: " + fromActivityOutput
						+ "->" + toProcessorOutput + " in "
						+ currentProcessor.get();
				if (isStrict()) {
					throw new ParseException(message);
				} else {
					logger.log(Level.WARNING, message);
					continue;
				}
			}

			OutputActivityPort outputActivityPort = new OutputActivityPort();
			outputActivityPort.setName(fromActivityOutput);
			outputActivityPort.setParent(currentActivity.get());
			currentActivity.get().getOutputPorts().add(outputActivityPort);

			processorOutputPortBinding.setBoundActivityPort(outputActivityPort);
			processorOutputPortBinding
					.setBoundProcessorPort(outputProcessorPort);
			currentProcessorBinding.get().getOutputPortBindings().add(
					processorOutputPortBinding);
		}

	}

	protected Workflow parseDataflow(Dataflow df) throws ParseException,
			JAXBException {
		Workflow wf = new Workflow();
		currentWorkflow.set(wf);
		wf.setName(df.getId());
		// wf.setId(df.getId());
		wf.setInputPorts(parseInputPorts(df.getInputPorts()));
		wf.setOutputPorts(parseOutputPorts(df.getOutputPorts()));
		wf.setProcessors(parseProcessors(df.getProcessors()));
		wf.setDatalinks(parseDatalinks(df.getDatalinks()));
		// TODO: Start conditions, annotations
		currentWorkflow.remove();
		return wf;
	}

	protected Set<DataLink> parseDatalinks(Datalinks origLinks)
			throws ParseException {
		HashSet<DataLink> newLinks = new HashSet<DataLink>();
		for (uk.org.taverna.scufl2.xml.t2flow.jaxb.DataLink origLink : origLinks
				.getDatalink()) {
			try {
				SenderPort senderPort = findSenderPort(currentWorkflow.get(),
						origLink.getSource());
				ReceiverPort receiverPort = findReceiverPort(currentWorkflow
						.get(), origLink.getSink());
				DataLink newLink = new DataLink(currentWorkflow.get(),
						senderPort, receiverPort);
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
			InputWorkflowPort newPort = new InputWorkflowPort(currentWorkflow
					.get(), originalPort.getName());
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
			OutputWorkflowPort newPort = new OutputWorkflowPort(currentWorkflow
					.get(), originalPort.getName());
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
			Processor newProc = new Processor(currentWorkflow.get(), origProc
					.getName());
			currentProcessor.set(newProc);
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
			int i = 0;
			for (Activity origActivity : origProc.getActivities().getActivity()) {
				parseActivityBinding(origActivity, i++);
			}
		}
		currentProcessor.remove();
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
	public WorkflowBundle parseT2Flow(InputStream t2File)
			throws IOException, JAXBException, ParseException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) getUnmarshaller()
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	public WorkflowBundle parseT2Flow(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow wf)
			throws ParseException, JAXBException {

		WorkflowBundle ro = new WorkflowBundle();
		currentResearchObject.set(ro);
		makeDefaultBindings(wf);

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
		currentResearchObject.remove();
		return ro;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

}
