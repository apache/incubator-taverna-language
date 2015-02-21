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


import static org.apache.taverna.scufl2.translator.t2flow.defaultactivities.WSDLActivityParser.WSDL;

import java.net.URI;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.XMLSplitterConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class WSDLXMLSplitterParser extends AbstractActivityParser {
	private static final URI wsdlActivityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/wsdl-activity/");
	private static final String inputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLInputSplitterActivity";
	private static final String outputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLOutputSplitterActivity";
	public static final URI SPLITTER = WSDL.resolve("xml-splitter");
	public static final URI XML_INPUT_SPLITTER = SPLITTER
			.resolve("xml-splitter/in");
	public static final URI XML_OUTPUT_SPLITTER = SPLITTER
			.resolve("xml-splitter/out");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(wsdlActivityRavenURI.toASCIIString()))
			return false;
		return activityUriStr.endsWith(inputSplitterClassName)
				|| activityUriStr.endsWith(outputSplitterClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		String activityUriStr = t2flowActivity.toASCIIString();
		if (activityUriStr.endsWith(inputSplitterClassName))
			return XML_INPUT_SPLITTER;
		else if (activityUriStr.endsWith(outputSplitterClassName))
			return XML_OUTPUT_SPLITTER;
		throw new IllegalArgumentException("Unexpected URI " + t2flowActivity);
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		XMLSplitterConfig splitterConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", XMLSplitterConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(SPLITTER.resolve("#Config"));

		String wrappedTypeXML = splitterConfig.getWrappedTypeXML();

		json.put("wrappedType", wrappedTypeXML);

		Activity activity = parserState.getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();

		for (ActivityPortDefinitionBean portBean : splitterConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean())
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		for (ActivityPortDefinitionBean portBean : splitterConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean())
			parseAndAddOutputPortDefinition(portBean, configuration, activity);

		return configuration;
	}
}
