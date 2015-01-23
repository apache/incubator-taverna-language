package org.apache.taverna.scufl2.translator.t2flow.t23activities;
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


import static org.apache.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.XPathConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.XPathNamespaceMap.Entry;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class XPathActivityParser extends AbstractActivityParser {
	private static final String ACTIVITY_XSD = "/org/apache/taverna/scufl2/translator/t2flow/xsd/xpathactivity.xsd";
	private static URI xpathRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/xpath-activity/");
	private static URI ravenUIURI = ravenURI
			.resolve("net.sf.taverna.t2.ui-activities/xpath-activity/");
	private static String className = "net.sf.taverna.t2.activities.xpath.XPathActivity";
	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/xpath");
	public static URI NAMESPACE_MAPPING_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/xpath/NamespaceMapping");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return (activityUriStr.startsWith(xpathRavenURI.toASCIIString()) || activityUriStr
				.startsWith(ravenUIURI.toASCIIString()))
				&& activityUriStr.endsWith(className);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL xpathXsd = XPathActivityParser.class.getResource(ACTIVITY_XSD);
		try {
			return Arrays.asList(xpathXsd.toURI());
		} catch (Exception e) {
			throw new IllegalStateException("Can't find XPath schema "
					+ xpathXsd);
		}
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {

		XPathConfig xpathConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", XPathConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());
		parserState.setCurrentConfiguration(configuration);

		try {
		    
		    ObjectNode json = (ObjectNode)configuration.getJson();
		    configuration.setType(ACTIVITY_URI.resolve("#Config"));

			String xmlDocument = xpathConfig.getXmlDocument();
			if (xmlDocument != null) {
			    json.put("exampleXmlDocument", xmlDocument);
			}

			String xpathExpression = xpathConfig.getXpathExpression();
			json.put("xpathExpression", xpathExpression);

			
			ArrayNode namespaceMap = json.arrayNode();
			json.put("xpathNamespaceMap", namespaceMap);

			// TODO look at why the schema translation here is so wrong
			for (Entry list : xpathConfig.getXpathNamespaceMap().getEntry()) {
				String namespacePrefix = list.getContent().get(0).getValue();
				String namespaceURI = list.getContent().get(1).getValue();

				ObjectNode map = json.objectNode();
				map.put("prefix", namespacePrefix);
				map.put("uri", namespaceURI);
				namespaceMap.add(map);
			}
		} finally {
			parserState.setCurrentConfiguration(null);
		}
		return configuration;
	}
}
