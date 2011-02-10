package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.io.StringReader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public abstract class AbstractActivityParser implements T2Parser {

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
		if (!(config instanceof Element)
				|| !configBean.getEncoding().equals(encoding)) {
			throw new ReaderException("Unsupported config bean " + configBean);
		}
		return unmarshallElement(t2FlowParser, (Element)config, configType);
	}
	
	public <ConfigType> ConfigType unmarshallElement(T2FlowParser t2FlowParser,
			Element element, Class<ConfigType> configType)
			throws ReaderException {
		
		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);
		JAXBElement<ConfigType> configElemElem;
		try {
			configElemElem = unmarshaller2.unmarshal(element,
					configType);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse element " + element, e);
		}

		return configElemElem.getValue();
	}
	
	public <ConfigType> ConfigType unmarshallXml(T2FlowParser t2FlowParser,
			String xml, Class<ConfigType> configType)
			throws ReaderException {
		
		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);
		
		JAXBElement<ConfigType> configElemElem;
		
		Source source = new StreamSource(new StringReader(xml));		
		try {
			configElemElem = unmarshaller2.unmarshal(source,
					configType);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse xml " + xml, e);
		}

		return configElemElem.getValue();
	}

}
