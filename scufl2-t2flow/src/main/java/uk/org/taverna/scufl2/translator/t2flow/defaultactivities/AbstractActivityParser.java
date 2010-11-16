package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;

import uk.org.taverna.scufl2.translator.t2flow.ParseException;
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
			throws ParseException {
		Object config = configBean.getAny();
		if (!(config instanceof Element)
				|| !configBean.getEncoding().equals(encoding)) {
			throw new ParseException("Unsupported config bean " + configBean);
		}
		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);
		JAXBElement<ConfigType> configElemElem;
		try {
			configElemElem = unmarshaller2.unmarshal((Element) config,
					configType);
		} catch (JAXBException e) {
			throw new ParseException("Can't parse config bean " + configBean, e);
		}

		return configElemElem.getValue();
	}

}
