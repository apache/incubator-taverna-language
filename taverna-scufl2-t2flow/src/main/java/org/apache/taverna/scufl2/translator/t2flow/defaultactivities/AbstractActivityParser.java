package org.apache.taverna.scufl2.translator.t2flow.defaultactivities;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


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

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.T2Parser;
import org.w3c.dom.Element;

//import org.apache.taverna.scufl2.api.property.PropertyResource;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean.MimeTypes;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractActivityParser implements T2Parser {
	public static URI MEDIATYPES_URI = URI.create("http://purl.org/NET/mediatypes/");
	@Deprecated
	public static URI PORT_DEFINITION = Scufl2Tools.PORT_DEFINITION;
	
	@SuppressWarnings("unused")
	private ThreadLocal<ParserState> parserState;

	public <ConfigType> ConfigType unmarshallConfig(T2FlowParser t2FlowParser,
			ConfigBean configBean, String encoding, Class<ConfigType> configType)
			throws ReaderException {
		Object config = configBean.getAny();
		if (config instanceof JAXBElement) {
			JAXBElement<?> jaxbElement = (JAXBElement<?>) config;
			if (!configType.isInstance((jaxbElement.getValue())))
				throw new ReaderException("Unexpected config type: "
						+ jaxbElement.getValue().getClass() + ", expected "
						+ configType);
			return configType.cast(jaxbElement.getValue());
		}
		if (!(config instanceof Element)
				|| !configBean.getEncoding().equals(encoding))
			throw new ReaderException("Unsupported config bean " + configBean);
		return unmarshallElement(t2FlowParser, (Element) config, configType);
	}

	public <ConfigType> ConfigType unmarshallElement(T2FlowParser t2FlowParser,
			Element element, Class<ConfigType> configType)
			throws ReaderException {
		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);
		try {
			JAXBElement<ConfigType> configElemElem = unmarshaller2.unmarshal(
					element, configType);
			return configElemElem.getValue();
		} catch (JAXBException|ClassCastException e) {
			throw new ReaderException("Can't parse element " + element, e);
		}
	}

	public <ConfigType> ConfigType unmarshallXml(T2FlowParser t2FlowParser,
			String xml, Class<ConfigType> configType) throws ReaderException {
		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);

		Source source = new StreamSource(new StringReader(xml));
		try {
			JAXBElement<ConfigType> configElemElem = unmarshaller2.unmarshal(
					source, configType);
			return configElemElem.getValue();
		} catch (JAXBException|ClassCastException e) {
			throw new ReaderException("Can't parse xml " + xml, e);
		}
	}

	protected ObjectNode parseAndAddOutputPortDefinition(
			ActivityPortDefinitionBean portBean, Configuration configuration,
			Activity activity) {
		ObjectNode configResource = (ObjectNode) configuration.getJson();
		OutputActivityPort outputPort = new OutputActivityPort();

		outputPort.setName(getPortElement(portBean, "name", String.class));
		outputPort.setParent(activity);

		BigInteger depth = getPortElement(portBean, "depth", BigInteger.class);
		if (depth != null)
			outputPort.setDepth(depth.intValue());
		
		BigInteger granularDepth = getPortElement(portBean, "granularDepth",
				BigInteger.class);
		if (granularDepth != null)
			outputPort.setGranularDepth(granularDepth.intValue());
		
		ObjectNode portConfig = configResource.objectNode();
//		PropertyResource portConfig = configResource.addPropertyAsNewResource(
//				Scufl2Tools.PORT_DEFINITION.resolve("#outputPortDefinition"),
//				Scufl2Tools.PORT_DEFINITION.resolve("#OutputPortDefinition"));

		@SuppressWarnings("unused")
		URI portUri = new URITools().relativeUriForBean(outputPort, configuration);
//		portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#definesOutputPort"), portUri);

	      // Legacy duplication of port details for XMLSplitter activities
        portConfig.put("name", outputPort.getName());
        portConfig.put("depth", outputPort.getDepth());
        portConfig.put("granularDepth", outputPort.getDepth());
		
		parseMimeTypes(portBean, portConfig);
		return portConfig;
	}

	/**
	 * Deals with the not-so-helpful
	 * getHandledReferenceSchemesOrTranslatedElementTypeOrName method
	 * 
	 * @param portBean
	 * @param elementName
	 * @return
	 */
	private <T> T getPortElement(ActivityPortDefinitionBean portBean,
			String elementName, Class<T> type) {
		for (JAXBElement<?> elem : portBean
				.getHandledReferenceSchemesOrTranslatedElementTypeOrName())
			if (elem.getName().getLocalPart().equals(elementName))
				return type.cast(elem.getValue());
		return null;
	}

	protected ObjectNode parseAndAddInputPortDefinition(
			ActivityPortDefinitionBean portBean, Configuration configuration,
			Activity activity) {
		ObjectNode configResource = (ObjectNode) configuration.getJson();
		ObjectNode portConfig = configResource.objectNode();

		InputActivityPort inputPort = new InputActivityPort();
		inputPort.setName(getPortElement(portBean, "name", String.class));
		inputPort.setParent(activity);

		BigInteger depth = getPortElement(portBean, "depth", BigInteger.class);
		if (depth != null)
			inputPort.setDepth(depth.intValue());
		
//		PropertyResource portConfig = configResource.addPropertyAsNewResource(
//				Scufl2Tools.PORT_DEFINITION.resolve("#inputPortDefinition"),
//				Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"));

		@SuppressWarnings("unused")
		URI portUri = new URITools().relativeUriForBean(inputPort, configuration);
//		portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#definesInputPort"), portUri);

		parseMimeTypes(portBean, portConfig);
		
		String translated = getPortElement(portBean, "translatedElementType", String.class);
		if (translated != null) {
			// As "translated element type" is confusing, we'll instead use "dataType"
//			portConfig.addPropertyReference(Scufl2Tools.PORT_DEFINITION.resolve("#dataType"),
//					URI.create("java:" + translated));
			portConfig.put("dataType", "java:" + translated);

			// TODO: Include mapping to XSD types like xsd:string
		}
		// T2-1681: Ignoring isAllowsLiteralValues and handledReferenceScheme

		// Legacy duplication of port details for XMLSplitter activities
		portConfig.put("name", inputPort.getName());
		portConfig.put("depth", inputPort.getDepth());

		return portConfig;
	}

	private void parseMimeTypes(ActivityPortDefinitionBean portBean,
			ObjectNode portConfig) {
		MimeTypes mimeTypes = getPortElement(portBean, "mimeTypes",
				MimeTypes.class);
		if (mimeTypes == null)
			return;
		// FIXME: Do as annotation as this is not configuration
//		URI mimeType = Scufl2Tools.PORT_DEFINITION.resolve("#expectedMimeType");
		List<String> strings = mimeTypes.getString();
		if ((strings == null || strings.isEmpty())
				&& mimeTypes.getElement() != null)
			strings = Arrays.asList(mimeTypes.getElement().getValue());
		if (strings != null)
			for (String s : strings) {
				if (s.contains("'"))
					s = s.split("'")[1];
//				portConfig.addPropertyReference(mimeType,
//						MEDIATYPES_URI.resolve(s));
				portConfig.put("mimeType", s);
				return; // Just first mimeType survives
			}
	}
	
	@Override
	public List<URI> getAdditionalSchemas() {
		return null;
	}
}
