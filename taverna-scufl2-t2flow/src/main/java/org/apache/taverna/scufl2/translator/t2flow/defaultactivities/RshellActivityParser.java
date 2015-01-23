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


import static org.apache.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.RShellConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.RShellConnection;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.RShellSymanticType;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.RShellSymanticType.RShellPortSymanticTypeBean;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RshellActivityParser extends AbstractActivityParser {
	/*
	 * A lovely artifact of xstream 'efficiency' - Xpath backpointers to
	 * previous elements. luckily we are here restricted within this specific
	 * config bean
	 * 
	 * Example:
	 * ../../net.sf.taverna.t2.activities.rshell.RShellPortSymanticTypeBean
	 * [3]/symanticType
	 * ../../../inputSymanticTypes/net.sf.taverna.t2.activities.
	 * rshell.RShellPortSymanticTypeBean[2]/symanticType
	 */
	Pattern strangeXpath = Pattern
			.compile("[./]+/(inputSymanticTypes)?.*TypeBean(\\[(\\d+)\\])?/symanticType$");

	private static final URI activityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/rshell-activity/");
	private static final String activityClassName = "net.sf.taverna.t2.activities.rshell.RshellActivity";
	public static final URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/rshell");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {
		RShellConfig rshellConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", RShellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(ACTIVITY_URI.resolve("#Config"));

		// Basic properties
		String script = rshellConfig.getScript();
		json.put("script", script);
//		if (rshellConfig.getRVersion() != null) {
//            json.put("rVersion", rshellConfig.getRVersion());
//		}

		// Connection
		ObjectNode connection = json.objectNode();
		json.put("connection", connection);
        RShellConnection conn = rshellConfig.getConnectionSettings();
        connection.put("hostname", conn.getHost());
        json.put("port", conn.getPort());

		// ignored - Taverna 2.3+ uses credential manager
		// connection.put("username", conn.getUsername());
		// connection.put("password", conn.getPassword());
        
        connection.put("keepSessionAlive", conn.isKeepSessionAlive());

		// ignoooooored - we won't support the legacy ones anymore
		// if (rshellConfig.getConnectionSettings().isNewRVersion() == null || !
		// rshellConfig.getConnectionSettings().isNewRVersion()) {
		// connection.put("legacy", true);
		// }

		// Activity ports
		Activity activity = parserState.getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		
		for (ActivityPortDefinitionBean portBean : rshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean())
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		for (ActivityPortDefinitionBean portBean : rshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean())
			parseAndAddOutputPortDefinition(portBean, configuration, activity);
		
		RShellSymanticType inputSymanticTypes = rshellConfig
				.getInputSymanticTypes();
		List<String> foundInputTypes = new ArrayList<>();

		ArrayNode inputPorts = json.arrayNode();
		json.put("inputTypes", inputPorts);
		for (RShellPortSymanticTypeBean symanticType : inputSymanticTypes
				.getNetSfTavernaT2ActivitiesRshellRShellPortSymanticTypeBean()) {
            ObjectNode port = json.objectNode();
			port.put("port", symanticType.getName());
			String dataType = symanticType.getSymanticType().getValue(); 
			String reference = symanticType.getSymanticType().getReference();
			if (reference != null) {
				Matcher matcher = strangeXpath.matcher(reference);
				if (matcher == null || !matcher.matches())
					throw new ReaderException(
							"Unhandled xstream xpath expression: " + reference);
				String position = matcher.group(3);
				if (position == null)
					position = "1";
				dataType = foundInputTypes.get(Integer.parseInt(position) - 1);
			}
			
			foundInputTypes.add(dataType);
			// Even if it's null - so the index is correct

			if (dataType != null)
			    port.put("dataType", dataType);
		}
		/*
		 * FIXME: Avoid this repetition. Would require a fair bit of parser
		 * state...
		 */
		RShellSymanticType outputSymanticTypes = rshellConfig
				.getOutputSymanticTypes();
		List<String> foundOutputTypes = new ArrayList<>();
        ArrayNode outputPorts = json.arrayNode();
        json.put("outputTypes", outputPorts);

		for (RShellPortSymanticTypeBean symanticType : outputSymanticTypes
				.getNetSfTavernaT2ActivitiesRshellRShellPortSymanticTypeBean()) {
            ObjectNode port = json.objectNode();
            port.put("port", symanticType.getName());

			String dataType = symanticType.getSymanticType().getValue();
			String reference = symanticType.getSymanticType().getReference();
			if (reference != null) {
				/*
				 * A lovely artifact of xstream 'efficiency' - Xpath
				 * backpointers to previous elements. luckily we are here
				 * restricted within this specific config bean.
				 *
				 * Example:
				 * ../../net.sf.taverna.t2.activities.rshell.RShellPortSymanticTypeBean[3]/symanticType
				 * ../../../inputSymanticTypes/net.sf.taverna.t2.activities.rshell.RShellPortSymanticTypeBean[2]/symanticType
				 */

				Matcher matcher = strangeXpath.matcher(reference);
				if (matcher == null || !matcher.matches())
					throw new ReaderException(
							"Unhandled xstream xpath expression: " + reference);

				boolean isInputSymantic = matcher.group(1) != null;
				String position = matcher.group(3);
				if (position == null)
					position = "1";
				if (isInputSymantic)
					dataType = foundInputTypes
							.get(Integer.parseInt(position) - 1);
				else
					dataType = foundOutputTypes
							.get(Integer.parseInt(position) - 1);
			}

			foundOutputTypes.add(dataType);
			// Even if it's null - so the index is correct

			if (dataType != null)
                port.put("dataType", dataType);
		}
		return configuration;
	}
}
