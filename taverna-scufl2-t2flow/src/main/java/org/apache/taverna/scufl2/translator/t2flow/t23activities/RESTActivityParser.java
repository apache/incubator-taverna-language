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
import org.apache.taverna.scufl2.xml.t2flow.jaxb.HTTPHeaders;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.RESTConfig;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RESTActivityParser extends AbstractActivityParser {
	private static final String ACTIVITY_XSD = "/org/apache/taverna/scufl2/translator/t2flow/xsd/restactivity.xsd";
	private static final URI restRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/rest-activity/");
	private static final URI ravenUIURI = ravenURI
			.resolve("net.sf.taverna.t2.ui-activities/rest-activity/");
	private static final String className = "net.sf.taverna.t2.activities.rest.RESTActivity";
	public static final URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/rest");
	public static final URI HTTP_URI = URI.create("http://www.w3.org/2011/http#");
	public static final URI HTTP_HEADERS_URI = URI.create("http://www.w3.org/2011/http-headers#");
	public static final URI HTTP_METHODS_URI = URI.create("http://www.w3.org/2011/http-methods#");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return ( activityUriStr.startsWith(restRavenURI.toASCIIString()) ||
				 activityUriStr.startsWith(ravenUIURI.toASCIIString()) )
				&& activityUriStr.endsWith(className);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL restXsd = RESTActivityParser.class.getResource(ACTIVITY_XSD);
		try {
			return Arrays.asList(restXsd.toURI());
		} catch (Exception e) {
			throw new IllegalStateException("Can't find REST schema "
					+ restXsd);
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
		RESTConfig restConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", RESTConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());
		parserState.setCurrentConfiguration(configuration);
		try {
		    ObjectNode json = (ObjectNode)configuration.getJson();
		    
		    configuration.setType(ACTIVITY_URI.resolve("#Config"));

		    ObjectNode request = json.objectNode();
		    json.put("request", request);

		    String method = restConfig.getHttpMethod().toUpperCase();
		    request.put("httpMethod", method);
		    request.put("absoluteURITemplate", restConfig.getUrlSignature());
		    
		    ArrayNode headers = json.arrayNode();
		    request.put("headers", headers);

			if (restConfig.getAcceptsHeaderValue() != null && ! restConfig.getAcceptsHeaderValue().isEmpty()) {
			    ObjectNode accept = json.objectNode();
			    headers.add(accept);
			    accept.put("header", "Accept");
			    accept.put("value", restConfig.getAcceptsHeaderValue());
			}
            if (hasContent(method)) {
				if (restConfig.getContentTypeForUpdates() != null && ! restConfig.getContentTypeForUpdates().isEmpty()) {
				    ObjectNode accept = json.objectNode();
	                headers.add(accept);
	                accept.put("header", "Content-Type");
	                accept.put("value", restConfig.getContentTypeForUpdates());
				}
				if (restConfig.isSendHTTPExpectRequestHeader()) {
                    ObjectNode accept = json.objectNode();
                    headers.add(accept);
                    accept.put("header", "Expect");
                    accept.put("value", "100-Continue");
				}
			}
			if (restConfig.getOtherHTTPHeaders() != null
					&& restConfig.getOtherHTTPHeaders().getList() != null)
				for (HTTPHeaders.List list : restConfig.getOtherHTTPHeaders()
						.getList()) {
					String fieldName = list.getContent().get(0).getValue();
					String fieldValue = list.getContent().get(1).getValue();

					ObjectNode accept = json.objectNode();
					headers.add(accept);
					accept.put("header", fieldName);
					accept.put("value", fieldValue);
				}
			if (restConfig.isShowActualUrlPort() != null)
				json.put("showActualURLPort", restConfig.isShowActualUrlPort()
						.booleanValue());
			if (restConfig.isShowResponseHeadersPort() != null)
				json.put("showResponseHeadersPort", restConfig
						.isShowResponseHeadersPort().booleanValue());

			if (restConfig.isShowRedirectionOutputPort())
				json.put("showRedirectionOutputPort", true);
			if (restConfig.isEscapeParameters() != null
					&& !restConfig.isEscapeParameters())
				json.put("escapeParameters", false);
			if (restConfig.getOutgoingDataFormat() != null)
				json.put("outgoingDataFormat",
						restConfig.getOutgoingDataFormat());
			return configuration;
		} finally {
			parserState.setCurrentConfiguration(null);
		}
	}

	private boolean hasContent(String methodName) {
		if (Arrays.asList("GET", "HEAD", "DELETE", "CONNECT").contains(methodName))
			return false;
		// Most probably does have or could have content
		return true;
	}
}
