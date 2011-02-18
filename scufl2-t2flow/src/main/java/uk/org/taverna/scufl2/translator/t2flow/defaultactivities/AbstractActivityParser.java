package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.io.StringReader;
import java.net.URI;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean.MimeTypes;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public abstract class AbstractActivityParser implements T2Parser {

	public static URI MEDIATYPES_URI = URI.create("http://purl.org/NET/mediatypes/");

	public static URI PORT_DEFINITION = URI
			.create("http://ns.taverna.org.uk/2010/scufl2#portDefinition");

	private ParserState parserState;

	public ParserState getParserState() {
		return parserState;
	}

	public void setParserState(ParserState parserState) {
		this.parserState = parserState;
	}

	public <ConfigType> ConfigType unmarshallConfig(T2FlowParser t2FlowParser,
			ConfigBean configBean, String encoding, Class<ConfigType> configType)
			throws ReaderException {
		Object config = configBean.getAny();
		if (!(config instanceof Element) || !configBean.getEncoding().equals(encoding)) {
			throw new ReaderException("Unsupported config bean " + configBean);
		}
		return unmarshallElement(t2FlowParser, (Element) config, configType);
	}

	public <ConfigType> ConfigType unmarshallElement(T2FlowParser t2FlowParser, Element element,
			Class<ConfigType> configType) throws ReaderException {

		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);
		JAXBElement<ConfigType> configElemElem;
		try {
			configElemElem = unmarshaller2.unmarshal(element, configType);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse element " + element, e);
		}

		return configElemElem.getValue();
	}

	public <ConfigType> ConfigType unmarshallXml(T2FlowParser t2FlowParser, String xml,
			Class<ConfigType> configType) throws ReaderException {

		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);

		JAXBElement<ConfigType> configElemElem;

		Source source = new StreamSource(new StringReader(xml));
		try {
			configElemElem = unmarshaller2.unmarshal(source, configType);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse xml " + xml, e);
		}

		return configElemElem.getValue();
	}

	protected void parseAndAddOutputPortDefinition(ActivityPortDefinitionBean portBean,
			Configuration configuration, Activity activity) {
		PropertyResource configResource = configuration.getPropertyResource();
		OutputActivityPort outputPort = new OutputActivityPort();
		outputPort.setName(portBean.getName());
		outputPort.setParent(activity);
		if (portBean.getDepth() != null) {
			outputPort.setDepth(portBean.getDepth().intValue());
		}
		if (portBean.getGranularDepth() != null) {
			outputPort.setGranularDepth(portBean.getGranularDepth().intValue());
		}

		PropertyResource portConfig = configResource.addPropertyAsNewResource(
				PORT_DEFINITION.resolve("#outputPortDefinition"),
				PORT_DEFINITION.resolve("#OutputPortDefinition"));

		URI portUri = new URITools().relativeUriForBean(outputPort, configuration);
		portConfig.addPropertyReference(PORT_DEFINITION.resolve("#definesOutputPort"), portUri);

		parseMimeTypes(portBean, portConfig);
	}

	protected void parseAndAddInputPortDefinition(ActivityPortDefinitionBean portBean,
			Configuration configuration, Activity activity) {
		PropertyResource configResource = configuration.getPropertyResource();

		InputActivityPort inputPort = new InputActivityPort();
		inputPort.setName(portBean.getName());
		inputPort.setParent(activity);

		if (portBean.getDepth() != null) {
			inputPort.setDepth(portBean.getDepth().intValue());
		}

		PropertyResource portConfig = configResource.addPropertyAsNewResource(
				PORT_DEFINITION.resolve("#inputPortDefinition"),
				PORT_DEFINITION.resolve("#InputPortDefinition"));

		URI portUri = new URITools().relativeUriForBean(inputPort, configuration);
		portConfig.addPropertyReference(PORT_DEFINITION.resolve("#definesInputPort"), portUri);

		parseMimeTypes(portBean, portConfig);
		
		if (portBean.getTranslatedElementType() != null) {
			// As "translated element type" is confusing, we'll instead use "dataType"
			portConfig.addPropertyReference(PORT_DEFINITION.resolve("#dataType"),
					URI.create("java:" + portBean.getTranslatedElementType()));

			// TODO: Include mapping to XSD types like xsd:string
		}
		// T2-1681: Ignoring isAllowsLiteralValues and handledReferenceScheme
		// TODO: Mime types, etc

	}

	private void parseMimeTypes(ActivityPortDefinitionBean portBean, PropertyResource portConfig) {
		MimeTypes mimeTypes = portBean.getMimeTypes();
		if (mimeTypes != null) {
			// FIXME: Do as annotation as this is not configuration
			URI mimeType = PORT_DEFINITION.resolve("#expectedMimeType");
			if (mimeTypes.getElement() != null) {
				String s = mimeTypes.getElement().getValue();
				if (s.contains("'")) {
					s = s.split("'")[1];
				}
				portConfig.addPropertyReference(mimeType, MEDIATYPES_URI.resolve(s));
			}
			if (mimeTypes.getString() != null) {
				for (String s : mimeTypes.getString()) {
					if (s.contains("'")) {
						s = s.split("'")[1];
					}
					portConfig.addPropertyReference(mimeType, MEDIATYPES_URI.resolve(s));
				}
			}
		}
	}

}
