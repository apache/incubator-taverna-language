package uk.org.taverna.scufl2.rdfxml;

import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.COLLECTION;
import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.DATATYPE;
import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.LI;
import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.LITERAL;
import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.PARSE_TYPE;
import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.RDF;
import static uk.org.taverna.scufl2.rdfxml.PropertyResourceSerialiser.RESOURCE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.rdfxml.jaxb.Configuration;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding.InputPortBinding;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding.OutputPortBinding;
import uk.org.taverna.scufl2.rdfxml.jaxb.Profile;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProfileDocument;

public class ProfileParser extends AbstractParser {

	public ProfileParser() {
		super();
	}

	public ProfileParser(ThreadLocal<ParserState> parserState) {
		super(parserState);
	}

	private Element getChildElement(Element element) {
		for (Node node : nodeIterable(element.getChildNodes())) {
			if (node instanceof Element) {
				return (Element) node;
			}
		}
		return null;
	}

	private Iterable<Node> nodeIterable(final NodeList childNodes) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return new Iterator<Node>() {
					int position = 0;

					@Override
					public boolean hasNext() {
						return childNodes.getLength() > position;
					}

					@Override
					public Node next() {
						return childNodes.item(position++);
					}

					@Override
					public void remove() {
						Node node = childNodes.item(position);
						node.getParentNode().removeChild(node);
					}
				};
			}
		};
	}

	protected void parseActivity(
			uk.org.taverna.scufl2.rdfxml.jaxb.Activity original) {
		Activity activity = new Activity();

		getParserState().push(activity);
		try {

			mapBean(original.getAbout(), activity);
			if (original.getName() != null) {
				activity.setName(original.getName());
			}
			activity.setParent(getParserState().getCurrent(
					uk.org.taverna.scufl2.api.profiles.Profile.class));
			if (original.getType() != null) {
				activity.setConfigurableType(resolve(original.getType()
						.getResource()));
			}
			for (uk.org.taverna.scufl2.rdfxml.jaxb.Activity.InputActivityPort inputActivityPort : original
					.getInputActivityPort()) {
				parseInputActivityPort(inputActivityPort.getInputActivityPort());
			}
			for (uk.org.taverna.scufl2.rdfxml.jaxb.Activity.OutputActivityPort outputActivityPort : original
					.getOutputActivityPort()) {
				parseOutputActivityPort(outputActivityPort
						.getOutputActivityPort());
			}
		} finally {
			getParserState().pop();
		}

	}

	protected void parseConfiguration(Configuration original)
			throws ReaderException {
		uk.org.taverna.scufl2.api.configurations.Configuration config = new uk.org.taverna.scufl2.api.configurations.Configuration();
		mapBean(original.getAbout(), config);
		config.setParent(getParserState().getCurrent(
				uk.org.taverna.scufl2.api.profiles.Profile.class));

		if (original.getName() != null) {
			config.setName(original.getName());
		}

		if (original.getType() != null) {
			config.setConfigurableType(resolve(original.getType().getResource()));
		}
		if (original.getConfigure() != null) {
			Configurable configurable = resolveBeanUri(original.getConfigure()
					.getResource(), Configurable.class);
			config.setConfigures(configurable);
		}

		getParserState().push(config);
		getParserState().push(config.getPropertyResource());
		for (Object o : original.getAny()) {
			parseProperty(o);
		}
		getParserState().pop();
		getParserState().pop();
	}

	private PropertyObject parseElement(Element element) throws ReaderException {
		Element childElement = getChildElement(element);
		String parseType = element.getAttributeNS(RDF, PARSE_TYPE);
		PropertyObject property = null;
		if (parseType.equals(COLLECTION)) {
			property = parseList(element);
		} else if (parseType.equals(RESOURCE)) {
			property = parseResource(element);
		} else if (parseType.equals(LITERAL)) {
			property = parseXmlLiteral(childElement);
		} else if (!parseType.isEmpty()) {
			throw new ReaderException("Unsupported rdf:parseType=" + parseType);
		}

		Attr resourceAttr = element.getAttributeNodeNS(RDF, RESOURCE);
		if (resourceAttr != null) {
			property = new PropertyReference(
					URI.create(resourceAttr.getValue()));
		}

		if (childElement == null && property == null) {
			property = parseLiteral(element);
		}
		if (property == null) {
			// If we made it down here, we're a resource
			property = parseResource(childElement);
		}
		return property;
	}

	protected void parseInputActivityPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputActivityPort original) {
		InputActivityPort port = new InputActivityPort();
		mapBean(original.getAbout(), port);
		port.setParent(getParserState().getCurrent(Activity.class));

		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
	}

	protected void parseInputPortBinding(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputPortBinding original)
			throws ReaderException {
		ProcessorInputPortBinding binding = new ProcessorInputPortBinding();
		mapBean(original.getAbout(), binding);

		binding.setBoundActivityPort(resolveBeanUri(original
				.getBindInputActivityPort().getResource(),
				InputActivityPort.class));
		binding.setBoundProcessorPort(resolveBeanUri(original
				.getBindInputProcessorPort().getResource(),
				InputProcessorPort.class));
		binding.setParent(getParserState().getCurrent(ProcessorBinding.class));

	}

	protected PropertyObject parseList(Element element) throws ReaderException {
		PropertyList propertyList = new PropertyList();
		for (Node node : nodeIterable(element.getChildNodes())) {
			if (!(node instanceof Element)) {
				continue;
			}
			PropertyObject propertyObject = parseElement(element);
			propertyList.add(propertyObject);

		}
		return propertyList;
	}

	protected PropertyObject parseLiteral(Element element) {
		String text = element.getTextContent();
		PropertyLiteral property = new PropertyLiteral(text);
		String dataType = element.getAttributeNS(RDF, DATATYPE);
		if (!dataType.isEmpty()) {
			property.setLiteralType(URI.create(dataType));
		}
		return property;
	}

	protected void parseOutputActivityPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputActivityPort original) {
		OutputActivityPort port = new OutputActivityPort();
		mapBean(original.getAbout(), port);
		port.setParent(getParserState().getCurrent(Activity.class));

		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
		if (original.getGranularPortDepth() != null) {
			port.setGranularDepth(original.getGranularPortDepth().getValue());
		}
	}

	protected void parseOutputPortBinding(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputPortBinding original)
			throws ReaderException {
		ProcessorOutputPortBinding binding = new ProcessorOutputPortBinding();
		mapBean(original.getAbout(), binding);

		binding.setBoundActivityPort(resolveBeanUri(original
				.getBindOutputActivityPort().getResource(),
				OutputActivityPort.class));
		binding.setBoundProcessorPort(resolveBeanUri(original
				.getBindOutputProcessorPort().getResource(),
				OutputProcessorPort.class));
		binding.setParent(getParserState().getCurrent(ProcessorBinding.class));

	}

	protected void parseProcessorBinding(
			uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding original)
			throws ReaderException {
		uk.org.taverna.scufl2.api.profiles.ProcessorBinding binding = new uk.org.taverna.scufl2.api.profiles.ProcessorBinding();
		binding.setParent(getParserState().getCurrent(
				uk.org.taverna.scufl2.api.profiles.Profile.class));
		mapBean(original.getAbout(), binding);
		getParserState().push(binding);

		if (original.getName() != null) {
			binding.setName(original.getName());
		}
		if (original.getActivityPosition() != null) {
			binding.setActivityPosition(original.getActivityPosition()
					.getValue());
		}

		URI processorUri = resolve(original.getBindProcessor().getResource());
		URI activityUri = resolve(original.getBindActivity().getResource());

		binding.setBoundProcessor((Processor) resolveBeanUri(processorUri));
		binding.setBoundActivity((Activity) resolveBeanUri(activityUri));

		for (InputPortBinding inputPortBinding : original.getInputPortBinding()) {
			parseInputPortBinding(inputPortBinding.getInputPortBinding());
		}
		for (OutputPortBinding outputPortBinding : original
				.getOutputPortBinding()) {
			parseOutputPortBinding(outputPortBinding.getOutputPortBinding());
		}

		getParserState().pop();

	}

	protected void parseProfile(Profile original, URI profileUri) {
		uk.org.taverna.scufl2.api.profiles.Profile p = new uk.org.taverna.scufl2.api.profiles.Profile();
		p.setParent(getParserState().getCurrent(WorkflowBundle.class));

		getParserState().push(p);

		if (original.getAbout() != null) {
			URI about = getParserState().getCurrentBase().resolve(
					original.getAbout());
			mapBean(about, p);
		} else {
			mapBean(profileUri, p);
		}

		if (original.getName() != null) {
			p.setName(original.getName());
		}
		// Note - we'll pop() in profileSecond() instead

	}

	protected void parseProfileSecond(Profile profileElem) {
		// TODO: Parse activates config etc.
		getParserState().pop();

	}

	protected void parseProperty(Object o) throws ReaderException {
		if (!(o instanceof Element)) {
			// throw new ReaderException("Unexpected property element " + o);
			return;
		}

		Element element = (Element) o;
		URI predicate = URI.create(element.getNamespaceURI()
				+ element.getLocalName());

		PropertyObject property = parseElement(element);

		PropertyResource propResource = getParserState().getCurrent(
				PropertyResource.class);
		propResource.addProperty(predicate, property);
	}

	private PropertyResource parseResource(Element element)
			throws ReaderException {
		String parseType = element.getAttributeNS(RDF, PARSE_TYPE);
		PropertyResource resource = new PropertyResource();
		boolean isListItem = element.getNamespaceURI().equals(RDF)
				&& element.getLocalName().equals(LI);
		if (!parseType.equals(RESOURCE) && !isListItem) {
			URI typeURI = URI.create(element.getNamespaceURI()
					+ element.getLocalName());
			resource.setTypeURI(typeURI);
		}
		getParserState().push(resource);
		for (Node node : nodeIterable(element.getChildNodes())) {
			parseProperty(node);
		}
		getParserState().pop();
		return resource;
	}

	private PropertyLiteral parseXmlLiteral(Element childElement) {
		PropertyLiteral literal = new PropertyLiteral(childElement);
		return literal;
	}

	protected void readProfile(URI profileUri, URI source)
			throws ReaderException, IOException {
		if (source.isAbsolute()) {
			throw new ReaderException("Can't read external profile source "
					+ source);
		}
		InputStream bundleStream = getParserState().getUcfPackage()
				.getResourceAsInputStream(source.getPath());

		readProfile(profileUri, source, bundleStream);
	}

	protected void readProfile(URI profileUri, URI source,
			InputStream bundleStream) throws ReaderException, IOException {
		JAXBElement<ProfileDocument> elem;
		try {
			elem = (JAXBElement<ProfileDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse profile document " + source,
					e);
		}

		URI base = getParserState().getLocation().resolve(source);
		if (elem.getValue().getBase() != null) {
			base = base.resolve(elem.getValue().getBase());
		}

		getParserState().setCurrentBase(base);

		uk.org.taverna.scufl2.rdfxml.jaxb.Profile profileElem = null;
		for (Object any : elem.getValue().getAny()) {
			if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.Profile) {
				if (profileElem != null) {
					throw new ReaderException("More than one <Profile> found");
				}
				profileElem = (uk.org.taverna.scufl2.rdfxml.jaxb.Profile) any;
				parseProfile(profileElem, profileUri);
			} else if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.Activity) {
				if (profileElem == null) {
					throw new ReaderException("No <Profile> found");
				}
				parseActivity((uk.org.taverna.scufl2.rdfxml.jaxb.Activity) any);

			} else if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding) {
				if (profileElem == null) {
					throw new ReaderException("No <Profile> found");
				}

				parseProcessorBinding((uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding) any);
			} else if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.Configuration) {
				if (profileElem == null) {
					throw new ReaderException("No <Profile> found");
				}
				parseConfiguration((uk.org.taverna.scufl2.rdfxml.jaxb.Configuration) any);
			}
		}
		parseProfileSecond(profileElem);
	}

}
