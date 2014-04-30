package uk.org.taverna.scufl2.translator.t2flow;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
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
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Activity;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedGranularDepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotatedPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotationAssertionImpl.NetSfTavernaT2AnnotationAnnotationAssertionImpl;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.AnnotationChain;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Annotations;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Condition;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Conditions;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.CrossProduct;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Dataflow;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Datalinks;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DispatchLayer;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DispatchStack;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DotProduct;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.GranularDepthPort;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.GranularDepthPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.IterationNode;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.IterationNodeParent;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Link;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.LinkType;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Mapping;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.PortProduct;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Processors;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Raven;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Role;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.TopIterationNode;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class T2FlowParser {

	private static final URI INTERNAL_DISPATCH_PREFIX = URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/");
    private static final String TEXT_TURTLE = "text/turtle";
	private static final String SEMANTIC_ANNOTATION = "net.sf.taverna.t2.annotation.annotationbeans.SemanticAnnotation";
	private static final String IDENTIFICATION_ASSERTION = "net.sf.taverna.t2.annotation.annotationbeans.IdentificationAssertion";
	private static final String T2FLOW_EXTENDED_XSD = "xsd/t2flow-extended.xsd";
	@SuppressWarnings("unused")
	private static final String T2FLOW_XSD = "xsd/t2flow.xsd";

	private static final Logger logger = Logger.getLogger(T2FlowParser.class
			.getCanonicalName());

	public static final URI ravenURI = URI
			.create("http://ns.taverna.org.uk/2010/xml/t2flow/raven/");

	public static final URI configBeanURI = URI
			.create("http://ns.taverna.org.uk/2010/xml/t2flow/configbean/");
	
	public static final URI t2flowParserURI = URI.create("http://ns.taverna.org.uk/2012/scufl2/t2flowParser");
	
	// TODO: Find better example predicate
	public static final URI exampleDataURI = URI
			.create("http://biocatalogue.org/attribute/exampleData");
	
	public static final String DEFAULT_PRODUCED_BY = "unspecified";
	

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

	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	private static URITools uriTools = new URITools();
    private static TransformerFactory transformerFactory;

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
	private Map<String, URI> predicates;

	public T2FlowParser() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
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
			throws ReaderException {
		String portName = sink.getPort();
		if (portName == null) {
			throw new ReaderException("Port name not specified");
		}
		String processorName = sink.getProcessor();
		if (processorName == null) {
			if (sink.getType().equals(LinkType.PROCESSOR)) {
				throw new ReaderException(
						"Link type was processor, but no processor name found");
			}
			OutputWorkflowPort candidate = wf.getOutputPorts().getByName(
					portName);
			if (candidate == null) {
				throw new ReaderException("Link to unknown workflow port "
						+ portName);
			}
			return candidate;
		} else {
			if (sink.getType().equals(LinkType.DATAFLOW)) {
				throw new ReaderException(
						"Link type was dataflow, but processor name was found");
			}
			Processor processor = wf.getProcessors().getByName(processorName);
			if (processor == null) {
				throw new ReaderException("Link to unknown processor "
						+ processorName);
			}
			InputProcessorPort candidate = processor.getInputPorts().getByName(
					portName);
			if (candidate == null) {
				throw new ReaderException("Link to unknown port " + portName
						+ " in " + processorName);
			}
			return candidate;
		}
	}

	protected SenderPort findSenderPort(Workflow wf, Link source)
			throws ReaderException {
		if (source.getType().equals(LinkType.MERGE)) {
			throw new ReaderException(
					"Link type Merge unexpected for sender ports");
		}
		String portName = source.getPort();
		if (portName == null) {
			throw new ReaderException("Port name not specified");
		}
		String processorName = source.getProcessor();
		if (processorName == null) {
			if (source.getType().equals(LinkType.PROCESSOR)) {
				throw new ReaderException(
						"Link type was processor, but no processor name found");
			}
			InputWorkflowPort candidate = wf.getInputPorts()
					.getByName(portName);
			if (candidate == null) {
				throw new ReaderException("Link from unknown workflow port "
						+ portName);
			}
			return candidate;
		} else {
			if (source.getType().equals(LinkType.DATAFLOW)) {
				throw new ReaderException(
						"Link type was dataflow, but processor name was found");
			}
			Processor processor = wf.getProcessors().getByName(processorName);
			if (processor == null) {
				throw new ReaderException("Link from unknown processor "
						+ processorName);
			}
			OutputProcessorPort candidate = processor.getOutputPorts()
					.getByName(portName);
			if (candidate == null) {
				throw new ReaderException("Link from unknown port " + portName
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
		// TODO: What should the default be? Should there be one? Who knows
		Profile profile = new Profile(wf.getProducedBy() == null ? DEFAULT_PRODUCED_BY : wf.getProducedBy());
		profile.setParent(parserState.get().getCurrentWorkflowBundle());
		parserState.get().getCurrentWorkflowBundle().setMainProfile(profile);
		parserState.get().setCurrentProfile(profile);
	}

	private URI makeRavenURI(Raven raven, String className) {
		if (raven == null) {
			return ravenURI.resolve("undefined/" + uriTools.validFilename(className));
		}
		
		return ravenURI.resolve(uriTools.validFilename(raven.getGroup()) + "/"
				+ uriTools.validFilename(raven.getArtifact()) + "/"
				+ uriTools.validFilename(raven.getVersion()) + "/"
				+ uriTools.validFilename(className));
	}

	private URI mapTypeFromRaven(Raven raven, String activityClass)
			throws ReaderException {
		URI classURI = makeRavenURI(raven, activityClass);
		parserState.get().setCurrentT2Parser(null);
		T2Parser t2Parser = getT2Parser(classURI);
		if (t2Parser == null) {
			String message = "Unknown T2 activity or dispatch layer " + classURI
					+ ", install supporting T2Parser";
			if (isStrict()) {
				throw new ReaderException(message);
			} else {
				logger.warning(message);
				return classURI;
			}
		}
		parserState.get().setCurrentT2Parser(t2Parser);
		return t2Parser.mapT2flowRavenIdToScufl2URI(classURI);
	}

	protected uk.org.taverna.scufl2.api.activity.Activity parseActivityAndAddToProfile(
			Activity origActivity) throws ReaderException {
		uk.org.taverna.scufl2.api.activity.Activity newActivity = parseActivity(origActivity);
		newActivity.setName(parserState.get().getCurrentProcessorBinding()
				.getName());
		parserState.get().getCurrentProfile().getActivities()
				.addWithUniqueName(newActivity);
		newActivity.setParent(parserState.get().getCurrentProfile());
		return newActivity;
	}

	protected uk.org.taverna.scufl2.api.activity.Activity parseActivity(
			Activity origActivity) throws ReaderException {
		Raven raven = origActivity.getRaven();
		String activityClass = origActivity.getClazz();
		URI activityId = mapTypeFromRaven(raven, activityClass);
		uk.org.taverna.scufl2.api.activity.Activity newActivity = new uk.org.taverna.scufl2.api.activity.Activity();
		newActivity.setType(activityId);
		return newActivity;
	}

	
	protected void parseActivityBinding(Activity origActivity,
			int activityPosition) throws ReaderException, JAXBException {
		ProcessorBinding processorBinding = new ProcessorBinding();

		processorBinding.setName(parserState.get().getCurrentProcessor()
				.getName());
		parserState.get().getCurrentProfile().getProcessorBindings()
				.addWithUniqueName(processorBinding);

		processorBinding.setBoundProcessor(parserState.get()
				.getCurrentProcessor());
		parserState.get().setCurrentProcessorBinding(processorBinding);
		uk.org.taverna.scufl2.api.activity.Activity newActivity = parseActivityAndAddToProfile(origActivity);
		parserState.get().setCurrentActivity(newActivity);

		parserState.get().getCurrentProfile().getActivities().add(newActivity);
		processorBinding.setBoundActivity(newActivity);
		processorBinding.setActivityPosition(activityPosition);

		parserState.get().setCurrentConfigurable(newActivity);

		try {
			parseConfigurationAndAddToProfile(origActivity.getConfigBean());
		} catch (JAXBException e) {
			if (isStrict()) {
				throw e;
			}
			logger.log(Level.WARNING, "Can't configure activity" + newActivity,
					e);
		}

		parseActivityInputMap(origActivity.getInputMap());
		parseActivityOutputMap(origActivity.getOutputMap());

		parserState.get().setCurrentConfigurable(null);
		parserState.get().setCurrentActivity(null);
		parserState.get().setCurrentProcessorBinding(null);
	}

	protected Configuration parseConfigurationAndAddToProfile(ConfigBean configBean) throws JAXBException, ReaderException {
		Configuration configuration = parseConfiguration(configBean);
		if (configuration == null) { 
			return null;
		}
		Profile profile = parserState.get().getCurrentProfile();
		configuration.setName(parserState.get().getCurrentActivity()
				.getName());
        profile.getConfigurations().addWithUniqueName(configuration);
        configuration.setConfigures(parserState.get().getCurrentConfigurable());
        return configuration;
		
	}
	
	protected Configuration parseConfiguration(ConfigBean configBean) throws JAXBException, ReaderException {
		// Placeholder to check later if no configuration have been provided
		Configuration UNCONFIGURED = new Configuration();
		
		Configuration configuration = UNCONFIGURED;
		if (parserState.get().getCurrentT2Parser() == null) {
			String message = "No config parser for " 
					+ parserState.get().getCurrentConfigurable();
			if (isStrict()) {
				throw new ReaderException(message);
			}
			return null;
		}

		try {
			configuration = parserState.get().getCurrentT2Parser()
					.parseConfiguration(this, configBean, parserState.get());
		} catch (ReaderException e) {
			if (isStrict()) {
				throw e;
			}
		}
		if (configuration == null) {
			// Perfectly valid - true for say Invoke layer
			return null;
		}
		
		if (configuration == UNCONFIGURED) {
			if (isStrict()) {
				throw new ReaderException("No configuration returned from "
						+ parserState.get().getCurrentT2Parser() + " for "						
						+ parserState.get().getCurrentConfigurable());
			}
			// We'll have to fall back by just keeping the existing XML
			
			configuration = new Configuration();
			configuration.setType(configBeanURI.resolve("Config"));
			String xml = elementToXML((Element) configBean.getAny());
			String encoding = configBean.getEncoding();
			ObjectNode json = (ObjectNode)configuration.getJson();
			json.put(encoding, xml);
		}
		return configuration;
		
		
	}
	
	public static String elementToXML(Element element) {
	    try {
           Transformer transformer = getTransformer();
           CharArrayWriter writer = new CharArrayWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException("Can't write XML", e);
        }
	}

    public static Transformer getTransformer() throws TransformerConfigurationException {
        if (transformerFactory == null) {
            transformerFactory = TransformerFactory.newInstance();
        }
        return transformerFactory.newTransformer();
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
				List<URI> schemas = getAdditionalSchemas();
				URL t2flowExtendedXSD = T2FlowParser.class
						.getResource(T2FLOW_EXTENDED_XSD);
				schemas.add(t2flowExtendedXSD.toURI());

				List<Source> schemaSources = new ArrayList<Source>();
				for (URI schemaUri : schemas) {
					schemaSources.add(new StreamSource(schemaUri
							.toASCIIString()));
				}
				Source[] sources = schemaSources
						.toArray(new Source[schemaSources.size()]);
				schema = schemaFactory.newSchema(sources);
			} catch (SAXException e) {
				throw new RuntimeException("Can't load schemas", e);
			} catch (URISyntaxException e) {
				throw new RuntimeException("Can't find schemas", e);
			} catch (NullPointerException e) {
				throw new RuntimeException("Can't find schemas", e);
			}
			u.setSchema(schema);
		}
		return u;
	}

	protected List<URI> getAdditionalSchemas() {
		List<URI> uris = new ArrayList<URI>();
		for (T2Parser parser : getT2Parsers()) {
			List<URI> schemas = parser.getAdditionalSchemas();
			if (schemas != null) {
				uris.addAll(schemas);
			}
		}
		return uris;
	}

	protected void parseActivityInputMap(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Map inputMap)
			throws ReaderException {
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
					throw new ReaderException(message);
				} else {
					logger.log(Level.WARNING, message);
					continue;
				}
			}

			InputActivityPort inputActivityPort = parserState.get()
					.getCurrentActivity().getInputPorts()
					.getByName(toActivityOutput);
			if (inputActivityPort == null) {
				inputActivityPort = new InputActivityPort();
				inputActivityPort.setName(toActivityOutput);
				inputActivityPort.setParent(parserState.get()
						.getCurrentActivity());
				parserState.get().getCurrentActivity().getInputPorts()
						.add(inputActivityPort);
			}

			if (inputActivityPort.getDepth() == null) {
				inputActivityPort.setDepth(inputProcessorPort.getDepth());
			}

			processorInputPortBinding.setBoundActivityPort(inputActivityPort);
			processorInputPortBinding.setBoundProcessorPort(inputProcessorPort);
			parserState.get().getCurrentProcessorBinding()
					.getInputPortBindings().add(processorInputPortBinding);
		}

	}

	protected void parseActivityOutputMap(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Map outputMap)
			throws ReaderException {
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
					throw new ReaderException(message);
				} else {
					logger.log(Level.WARNING, message);
					continue;
				}
			}

			OutputActivityPort outputActivityPort = parserState.get()
					.getCurrentActivity().getOutputPorts()
					.getByName(fromActivityOutput);
			if (outputActivityPort == null) {
				outputActivityPort = new OutputActivityPort();
				outputActivityPort.setName(fromActivityOutput);
				outputActivityPort.setParent(parserState.get()
						.getCurrentActivity());
				parserState.get().getCurrentActivity().getOutputPorts()
						.add(outputActivityPort);
			}

			if (outputActivityPort.getDepth() == null) {
				outputActivityPort.setDepth(outputProcessorPort.getDepth());
			}
			if (outputActivityPort.getGranularDepth() == null) {
				outputActivityPort.setGranularDepth(outputProcessorPort
						.getGranularDepth());
			}

			processorOutputPortBinding.setBoundActivityPort(outputActivityPort);
			processorOutputPortBinding
					.setBoundProcessorPort(outputProcessorPort);
			parserState.get().getCurrentProcessorBinding()
					.getOutputPortBindings().add(processorOutputPortBinding);
		}

	}

	protected Workflow parseDataflow(Dataflow df, Workflow wf)
			throws ReaderException, JAXBException {
		parserState.get().setCurrentWorkflow(wf);
		wf.setInputPorts(parseInputPorts(df.getInputPorts()));
		wf.setOutputPorts(parseOutputPorts(df.getOutputPorts()));
		wf.setProcessors(parseProcessors(df.getProcessors()));
		wf.setDataLinks(parseDatalinks(df.getDatalinks()));
		wf.setControlLinks(parseControlLinks(df.getConditions()));		
		Revision revision = parseIdentificationAnnotations(df.getAnnotations());
		if (revision != null) {
			wf.setCurrentRevision(revision);
		}
		parseAnnotations(wf, df.getAnnotations());

		parserState.get().setCurrentWorkflow(null);
		return wf;
	}

	public void parseAnnotations(WorkflowBean annotatedBean, Annotations annotations) throws ReaderException {
		
		//logger.fine("Checking annotations for " + annotatedSubject);
		
		Map<String, NetSfTavernaT2AnnotationAnnotationAssertionImpl> annotationElems = new HashMap<String, NetSfTavernaT2AnnotationAnnotationAssertionImpl>();
		if (annotations == null || annotations.getAnnotationChainOrAnnotationChain22() == null) {
			return;
		}
		for (JAXBElement<AnnotationChain> el : annotations
				.getAnnotationChainOrAnnotationChain22()) {
			NetSfTavernaT2AnnotationAnnotationAssertionImpl ann = el.getValue()
					.getNetSfTavernaT2AnnotationAnnotationChainImpl()
					.getAnnotationAssertions()
					.getNetSfTavernaT2AnnotationAnnotationAssertionImpl();
			String annClass = ann.getAnnotationBean().getClazz();
			if (annotationElems.containsKey(annClass) && annotationElems.get(annClass).getDate().compareToIgnoreCase(ann.getDate()) > 0) {
				// ann.getDate() is less than current 'latest' annotation, skip
				continue;
			}
			annotationElems.put(annClass, ann);
		}
		
		
		for (String clazz : annotationElems.keySet()) {			
			NetSfTavernaT2AnnotationAnnotationAssertionImpl ann = annotationElems.get(clazz);
			Calendar cal = parseDate(ann.getDate());					
			String value = null;	
			String semanticMediaType = TEXT_TURTLE;
			for (Object obj : ann.getAnnotationBean().getAny()) {
				if (!(obj instanceof Element)) {
					continue;
				}
				Element elem = (Element) obj;
				if (! (elem.getNamespaceURI() == null)) {
					continue;
				}
				if (elem.getLocalName().equals("text")) {
					value = elem.getTextContent().trim();
					break;
				}
				if (clazz.equals(SEMANTIC_ANNOTATION)) {
					if (elem.getLocalName().equals("content")) {
						value = elem.getTextContent().trim();
					}
					if (elem.getLocalName().equals("mimeType")) {					
						semanticMediaType = elem.getTextContent().trim();
					}
				}
			}
			if (value != null) {
				// Add the annotation
				Annotation annotation = new Annotation();
				WorkflowBundle workflowBundle = parserState.get().getCurrentWorkflowBundle();
				annotation.setParent(workflowBundle);
				
                String path = "annotation/" + annotation.getName() + ".ttl";
                URI bodyURI = URI.create(path);
				
				annotation.setTarget(annotatedBean);
				annotation.setAnnotatedAt(cal);
				//annotation.setAnnotator();
				annotation.setSerializedBy(t2flowParserURI);
				annotation.setSerializedAt(new GregorianCalendar());
				URI annotatedSubject = uriTools.relativeUriForBean(annotatedBean,
						annotation);
				String body;
                if (clazz.equals(SEMANTIC_ANNOTATION)) {
                    String baseStr = "@base <" + annotatedSubject.toASCIIString() + "> .\n";
					body = baseStr + value;
				} else {
				    // Generate Turtle from 'classic' annotation 
				    URI predicate = getPredicatesForClass().get(clazz);
	                if (predicate == null) {
	                    if (isStrict()) {
	                        throw new ReaderException("Unsupported annotation class " + clazz);
	                    } 
	                    return;
	                }
	               
	                
	               StringBuilder turtle = new StringBuilder();
	               turtle.append("<");
	               turtle.append(annotatedSubject.toASCIIString());
	               turtle.append("> ");
	               
	               turtle.append("<");
                   turtle.append(predicate.toASCIIString());
                   turtle.append("> ");
                   
                   // A potentially multi-line string
                   turtle.append("\"\"\"");
                   // Escape existing \ to \\
                   String escaped = value.replace("\\", "\\\\");
                   // Escape existing """ to \"\"\"  (beware Java's escaping of \ and ")
                   escaped = escaped.replace("\"\"\"", "\\\"\\\"\\\"");
                   turtle.append(escaped);
                   turtle.append("\"\"\"");
                   turtle.append(" .");
                   body = turtle.toString();
                   
				}
                try {
                    workflowBundle.getResources().addResource(body, path, semanticMediaType);
                } catch (IOException e) {
                    throw new ReaderException("Could not store annotation body to " + path, e);
                }
                annotation.setBody(bodyURI);
				
			}
		}
	}

	private Map<String, URI> getPredicatesForClass() {
		if (this.predicates != null) {
			return this.predicates;
		}
		synchronized (this) {
			if (this.predicates != null) {
				return this.predicates;
			}
			Map<String, URI> predicates = new HashMap<String, URI>();
			predicates.put("net.sf.taverna.t2.annotation.annotationbeans.DescriptiveTitle", 
					URI.create("http://purl.org/dc/terms/title"));
			
			predicates.put("net.sf.taverna.t2.annotation.annotationbeans.Author", 
					URI.create("http://purl.org/dc/elements/1.1/creator"));
			
			predicates.put("net.sf.taverna.t2.annotation.annotationbeans.FreeTextDescription", 
					URI.create("http://purl.org/dc/terms/description"));
			
			predicates.put("net.sf.taverna.t2.annotation.annotationbeans.MimeType", 
					URI.create("http://purl.org/dc/elements/1.1/format"));
			
			predicates.put("net.sf.taverna.t2.annotation.annotationbeans.ExampleValue", 
					exampleDataURI);
			this.predicates = predicates;
			return this.predicates;
		}
	}

	protected Revision parseIdentificationAnnotations(Annotations annotations) {
		SortedMap<Calendar, UUID> revisions = new TreeMap<Calendar, UUID>();
		if (annotations == null || annotations.getAnnotationChainOrAnnotationChain22() == null) {
			return null;
		}
		for (JAXBElement<AnnotationChain> el : annotations
				.getAnnotationChainOrAnnotationChain22()) {
			NetSfTavernaT2AnnotationAnnotationAssertionImpl ann = el.getValue()
					.getNetSfTavernaT2AnnotationAnnotationChainImpl()
					.getAnnotationAssertions()
					.getNetSfTavernaT2AnnotationAnnotationAssertionImpl();
			String annClass = ann.getAnnotationBean().getClazz();
			if (!annClass
					.equals(IDENTIFICATION_ASSERTION)) {
				continue;
			}
			for (Object obj : ann.getAnnotationBean().getAny()) {
				if (!(obj instanceof Element)) {
					continue;
				}
				Element elem = (Element) obj;
				if (elem.getNamespaceURI() == null
						&& elem.getLocalName().equals("identification")) {
					String uuid = elem.getTextContent().trim();
					String date = ann.getDate();
					Calendar cal = parseDate(date);					
					revisions.put(cal, UUID.fromString(uuid));
				}				
			}
		}
		
		Revision rev = null;
		for (Entry<Calendar, UUID> entry : revisions.entrySet()) {			
			Calendar cal = entry.getKey();
			UUID uuid = entry.getValue();
			URI uri = Workflow.WORKFLOW_ROOT.resolve(uuid.toString() + "/");
			rev = new Revision(uri, rev);
			rev.setGeneratedAtTime(cal);		
		}
		return rev;
	}

	private Calendar parseDate(String dateStr) {
		// Based briefly on patterns used by 
		// com.thoughtworks.xstream.converters.basic.DateConverter
		
		List<String> patterns = new ArrayList<String>();
		patterns.add("yyyy-MM-dd HH:mm:ss.S z");
		patterns.add("yyyy-MM-dd HH:mm:ss z");
		patterns.add("yyyy-MM-dd HH:mm:ssz");
		patterns.add("yyyy-MM-dd HH:mm:ss.S 'UTC'");
		patterns.add("yyyy-MM-dd HH:mm:ss 'UTC'");
        Date date;
		for (String pattern : patterns) {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        	try {        		
				date = dateFormat.parse(dateStr);
				GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
				cal.setTime(date);
				return cal;
			} catch (ParseException e) {
				continue;
			}
        }
        throw new IllegalArgumentException("Can't parse date: " + dateStr);		
	}

	private Set<ControlLink> parseControlLinks(Conditions conditions)
			throws ReaderException {
		Set<ControlLink> links = new HashSet<ControlLink>();
		for (Condition condition : conditions.getCondition()) {
			NamedSet<Processor> processors = parserState.get()
					.getCurrentWorkflow().getProcessors();
			String target = condition.getTarget();
			Processor block = processors.getByName(target);
			if (block == null && isStrict()) {
				throw new ReaderException(
						"Unrecognized blocking processor in control link: "
								+ target);
			}
			String control = condition.getControl();
			Processor untilFinished = processors.getByName(control);
			if (untilFinished == null && isStrict()) {
				throw new ReaderException(
						"Unrecognized untilFinished processor in control link: "
								+ control);
			}

			BlockingControlLink newLink = new BlockingControlLink(block, untilFinished);

			// FIXME: missing from t2flow and schema
			//parseAnnotations(newLink, condition.getAnnotations());

			links.add(newLink);
		}
		return links;
	}

	protected Set<DataLink> parseDatalinks(Datalinks origLinks)
			throws ReaderException {
		HashSet<DataLink> newLinks = new HashSet<DataLink>();
		Map<ReceiverPort, AtomicInteger> mergeCounts = new HashMap<ReceiverPort, AtomicInteger>();
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
						throw new ReaderException(
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
						throw new ReaderException(
								"More than one link to non-merged port "
										+ receiverPort);
					}
					mergeCounts.put(receiverPort, new AtomicInteger(-1));
				}

				// FIXME: missing from t2flow and schema
				//parseAnnotations(newLink, origLink.getAnnotations());

				newLinks.add(newLink);
			} catch (ReaderException ex) {
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

	protected void parseDispatchStack(
			DispatchStack dispatchStack) throws ReaderException {
        Processor processor = parserState.get().getCurrentProcessor();
        Configuration procConfig = scufl2Tools.createConfigurationFor(processor, parserState.get().getCurrentProfile());
        parserState.get().setCurrentConfigurable(processor);
        parserState.get().setCurrentConfiguration(procConfig);
        parserState.get().setPreviousDispatchLayerName(null);
        try {
            for (DispatchLayer dispatchLayer : dispatchStack.getDispatchLayer()) {
                parseDispatchStack(dispatchLayer);
            }
        } finally {
            parserState.get().setCurrentConfigurable(null);
            parserState.get().setCurrentConfiguration(null);
            parserState.get().setPreviousDispatchLayerName(null);
        }
    }
	
	protected void parseDispatchStack(DispatchLayer dispatchLayer)
			throws ReaderException {
		URI typeUri = mapTypeFromRaven(dispatchLayer.getRaven(),
				dispatchLayer.getClazz());
		ObjectNode procConfig = parserState.get().getCurrentConfiguration().getJsonAsObjectNode();
		
		try {
			Configuration dispatchConfig = parseConfiguration(dispatchLayer.getConfigBean());
			URI relUri = INTERNAL_DISPATCH_PREFIX.relativize(typeUri);
			String name;
			if (! relUri.isAbsolute()) {
			    // It's an internal layer. We'll put it under the name which we'll cleverly fish out 
			    // of the URI path, eg. "retry" or "parallelize"
                name = relUri.getPath().toLowerCase();
			    if (dispatchConfig != null && dispatchConfig.getJson().size() > 0) {
			        // But only if non-empty (non-default)
			        procConfig.put(name, dispatchConfig.getJson());
			    }
			} else {
			    ObjectNode json;
			    if (dispatchConfig != null && dispatchConfig.getJson().isObject()) {
			        json = dispatchConfig.getJsonAsObjectNode();
			    } else {
			        // We'll still need to create an objectNode to keep _type and _below
			        json = procConfig.objectNode();
			        if (dispatchConfig != null) {
			            // We'll put the non-objectnode here
			            json.put("_config", dispatchConfig.getJson());
			        }
			    }
			    
			    // Not really much to go from here, we don't want to use the typeUri as the name
			    // as third-party layers in theory could be added several times for same type
			    name = UUID.randomUUID().toString();
			    json.put("_type", typeUri.toString());
			    // Might be null - meaning "top"
			    json.put("_below", parserState.get().getPreviousDispatchLayerName());
			    procConfig.put(name, json);
			}
			parserState.get().setPreviousDispatchLayerName(name);			
		} catch (JAXBException ex) {
			String message = "Can't parse configuration for dispatch layer in "
					+ parserState.get().getCurrentProcessor();
			logger.log(Level.WARNING, message, ex);
			if (isStrict()) {
				throw new ReaderException(message, ex);
			}

		}
	}

	@SuppressWarnings("boxing")
	protected Set<InputWorkflowPort> parseInputPorts(
			AnnotatedGranularDepthPorts originalPorts) throws ReaderException {
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
					throw new ReaderException(message);
				}
			}
			parseAnnotations(newPort, originalPort.getAnnotations());
			
			createdPorts.add(newPort);
		}
		return createdPorts;
	}

	protected IterationStrategyStack parseIterationStrategyStack(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.IterationStrategyStack originalStack)
			throws ReaderException {
		IterationStrategyStack newStack = new IterationStrategyStack();

		List<TopIterationNode> strategies = originalStack.getIteration()
				.getStrategy();
		for (TopIterationNode strategy : strategies) {
			IterationNode topNode = strategy.getCross();
			if (topNode == null) {
				topNode = strategy.getDot();
			}
			if (topNode == null) {
				continue;
			}
			IterationNodeParent parent = (IterationNodeParent) topNode;
			if (parent.getCrossOrDotOrPort().isEmpty()) {
				continue;
			}
			try {
				newStack.add((IterationStrategyTopNode) parseIterationStrategyNode(topNode));
			} catch (ReaderException e) {
				logger.warning(e.getMessage());
				if (isStrict()) {
					throw e;
				}
			}
		}

		return newStack;
	}

	protected IterationStrategyNode parseIterationStrategyNode(
			IterationNode topNode) throws ReaderException {
		if (topNode instanceof PortProduct) {
			PortProduct portProduct = (PortProduct) topNode;
			PortNode portNode = new PortNode();
			portNode.setDesiredDepth(portProduct.getDepth().intValue());
			String name = portProduct.getName();
			Processor processor = parserState.get().getCurrentProcessor();
			InputProcessorPort inputProcessorPort = processor.getInputPorts()
					.getByName(name);
			portNode.setInputProcessorPort(inputProcessorPort);
			return portNode;
		}

		IterationStrategyNode node;
		if (topNode instanceof DotProduct) {
			node = new uk.org.taverna.scufl2.api.iterationstrategy.DotProduct();
		} else if (topNode instanceof CrossProduct) {
			node = new uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct();
		} else {
			throw new ReaderException("Invalid node " + topNode);
		}
		List<IterationStrategyNode> children = (List<IterationStrategyNode>) node;
		IterationNodeParent parent = (IterationNodeParent) topNode;
		for (IterationNode child : parent.getCrossOrDotOrPort()) {
			children.add(parseIterationStrategyNode(child));
		}
		return node;
	}

	protected Set<OutputWorkflowPort> parseOutputPorts(
			AnnotatedPorts originalPorts) throws ReaderException {
		Set<OutputWorkflowPort> createdPorts = new HashSet<OutputWorkflowPort>();
		for (AnnotatedPort originalPort : originalPorts.getPort()) {
			OutputWorkflowPort newPort = new OutputWorkflowPort(parserState
					.get().getCurrentWorkflow(), originalPort.getName());

			parseAnnotations(newPort, originalPort.getAnnotations());
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
			throws ReaderException, JAXBException {
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
			parseDispatchStack(origProc
					.getDispatchStack());
			newProc.setIterationStrategyStack(parseIterationStrategyStack(origProc
					.getIterationStrategyStack()));
			parseAnnotations(newProc, origProc.getAnnotations());

			
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
			ReaderException, JAXBException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) getUnmarshaller()
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	@SuppressWarnings("unchecked")
	public WorkflowBundle parseT2Flow(InputStream t2File) throws IOException,
			JAXBException, ReaderException {
		JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow> root = (JAXBElement<uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow>) getUnmarshaller()
				.unmarshal(t2File);
		return parseT2Flow(root.getValue());
	}

	public WorkflowBundle parseT2Flow(
			uk.org.taverna.scufl2.xml.t2flow.jaxb.Workflow wf)
			throws ReaderException, JAXBException {
		try {
			parserState.get().setT2FlowParser(this);
			WorkflowBundle wfBundle = new WorkflowBundle();
			parserState.get().setCurrentWorkflowBundle(wfBundle);
			makeProfile(wf);

			// First a skeleton scan of workflows (for nested workflow configs)
			Map<Dataflow, Workflow> dataflowMap = new HashMap<Dataflow, Workflow>();
			for (Dataflow df : wf.getDataflow()) {
				Workflow workflow = skeletonDataflow(df);
				dataflowMap.put(df, workflow);
				wfBundle.getWorkflows().addWithUniqueName(workflow);
				workflow.setParent(wfBundle);
				if (df.getRole().equals(Role.TOP)) {
					wfBundle.setMainWorkflow(workflow);
					wfBundle.setName(df.getName());
					wfBundle.setGlobalBaseURI(WorkflowBundle.WORKFLOW_BUNDLE_ROOT
							.resolve(df.getId() + "/"));
				}
			}
			// Second stage
			for (Dataflow df : wf.getDataflow()) {
				Workflow workflow = dataflowMap.get(df);
				parseDataflow(df, workflow);
			}
			if (isStrict() && wfBundle.getMainWorkflow() == null) {
				throw new ReaderException("No main workflow");
			}
			scufl2Tools.setParents(wfBundle);

			return wfBundle;
		} finally {
			parserState.remove();
		}
	}

	protected Workflow skeletonDataflow(Dataflow df) {
		Workflow wf = new Workflow();
		parserState.get().setCurrentWorkflow(wf);
		wf.setName(df.getName());
		wf.setIdentifier(Workflow.WORKFLOW_ROOT.resolve(df.getId()
				+ "/"));
		return wf;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

}
