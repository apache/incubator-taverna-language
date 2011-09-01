package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
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
//import static uk.org.taverna.scufl2.api.common.Scufl2Tools.Scufl2Tools.PORT_DEFINITION;


public abstract class AbstractActivityParser implements T2Parser {

	public static URI MEDIATYPES_URI = URI.create("http://purl.org/NET/mediatypes/");

	@Deprecated
	public static URI PORT_DEFINITION = Scufl2Tools.PORT_DEFINITION;
	
	private ThreadLocal<ParserState> parserState;

	public <ConfigType> ConfigType unmarshallConfig(T2FlowParser t2FlowParser,
			ConfigBean configBean, String encoding, Class<ConfigType> configType)
			throws ReaderException {
		Object config = configBean.getAny();
		if (config instanceof JAXBElement) {
			JAXBElement jaxbElement = (JAXBElement) config;
			if (! configType.isInstance((jaxbElement.getValue()))) {
				throw new ReaderException("Unexpected config type: " + 
						jaxbElement.getValue().getClass() + ", expected " + configType);
			}
			return configType.cast(jaxbElement.getValue());
		}
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

	protected PropertyResource parseAndAddOutputPortDefinition(ActivityPortDefinitionBean portBean,
			Configuration configuration, Activity activity) {
		PropertyResource configResource = configuration.getPropertyResource();
		OutputActivityPort outputPort = new OutputActivityPort();
		
		outputPort.setName(getPortElement(portBean, "name", String.class));
		outputPort.setParent(activity);
		

		BigInteger depth = getPortElement(portBean, "depth", BigInteger.class);
		if (depth != null) {
			outputPort.setDepth(depth.intValue());
		}
		
		BigInteger granularDepth = getPortElement(portBean, "granularDepth", BigInteger.class);		
		if (granularDepth != null) {
			outputPort.setGranularDepth(granularDepth.intValue());
		}

		PropertyResource portConfig = configResource.addPropertyAsNewResource(
				Scufl2Tools.PORT_DEFINITION.resolve("#outputPortDefinition"),
				Scufl2Tools.PORT_DEFINITION.resolve("#OutputPortDefinition"));

		URI portUri = new URITools().relativeUriForBean(outputPort, configuration);
		portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#definesOutputPort"), portUri);

		parseMimeTypes(portBean, portConfig);
		return portConfig;
	}

	/**
	 * Deals with the not-so-helpful getHandledReferenceSchemesOrTranslatedElementTypeOrName method
	 * 
	 * @param portBean
	 * @param elementName
	 * @return
	 */
	
	
	private <T> T getPortElement(ActivityPortDefinitionBean portBean,
			String elementName, Class<T> type) {
		for (JAXBElement<?> elem : portBean.getHandledReferenceSchemesOrTranslatedElementTypeOrName()) {
			if (elem.getName().getLocalPart().equals(elementName)) {
				return type.cast(elem.getValue());
			}
		}
		return null;
	}

	protected PropertyResource parseAndAddInputPortDefinition(ActivityPortDefinitionBean portBean,
			Configuration configuration, Activity activity) {
		PropertyResource configResource = configuration.getPropertyResource();

		InputActivityPort inputPort = new InputActivityPort();
		inputPort.setName(getPortElement(portBean, "name", String.class));
		inputPort.setParent(activity);

		BigInteger depth = getPortElement(portBean, "depth", BigInteger.class);
		if (depth != null) {
			inputPort.setDepth(depth.intValue());
		}

		PropertyResource portConfig = configResource.addPropertyAsNewResource(
				Scufl2Tools.PORT_DEFINITION.resolve("#inputPortDefinition"),
				Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"));

		URI portUri = new URITools().relativeUriForBean(inputPort, configuration);
		portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#definesInputPort"), portUri);

		parseMimeTypes(portBean, portConfig);
		
		String translated = getPortElement(portBean, "translatedElementType", String.class);
		if (translated != null) {
			// As "translated element type" is confusing, we'll instead use "dataType"
			portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#dataType"),
					URI.create("java:" + translated));

			// TODO: Include mapping to XSD types like xsd:string
		}
		// T2-1681: Ignoring isAllowsLiteralValues and handledReferenceScheme
		return portConfig;

	}

	private void parseMimeTypes(ActivityPortDefinitionBean portBean,
			PropertyResource portConfig) {
		MimeTypes mimeTypes = getPortElement(portBean, "mimeTypes", MimeTypes.class);
		if (mimeTypes == null) {
			return;
		}
		// FIXME: Do as annotation as this is not configuration
		URI mimeType = Scufl2Tools.PORT_DEFINITION.resolve("#expectedMimeType");
		List<String> strings = mimeTypes.getString();
		if (strings == null && mimeTypes.getElement() != null) {
			strings = Arrays.asList(mimeTypes.getElement().getValue());
		} 
		if (strings != null) { 
			for (String s : strings) {
				if (s.contains("'")) {
					s = s.split("'")[1];
				}
				portConfig.addPropertyReference(mimeType,
						MEDIATYPES_URI.resolve(s));
			}
		}
	}
	
	@Override
	public List<URI> getAdditionalSchemas() {
		return null;
	}

}
