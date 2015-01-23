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


import java.math.BigInteger;
import java.net.URI;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ApiConsumerConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ApiConsomerActivityParser extends AbstractActivityParser {
	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/apiconsumer-activity/");
	private static String activityClassName = "net.sf.taverna.t2.activities.apiconsumer.ApiConsumerActivity";
	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/apiconsumer");

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
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		ApiConsumerConfig config = unmarshallConfig(t2FlowParser, configBean,
				"xstream", ApiConsumerConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(ACTIVITY_URI.resolve("#Config"));

		json.put("apiConsumerDescription", config.getApiConsumerDescription());
		json.put("apiConsumerName", config.getApiConsumerName());
		json.put("description", config.getDescription());
		json.put("className", config.getClassName());
		json.put("methodName", config.getMethodName());

		ArrayNode parameterNames = json.arrayNode();
		json.put("parameterNames", parameterNames);
		for (String parameterName : config.getParameterNames().getString())
			parameterNames.add(parameterName);

		ArrayNode parameterDimensions = json.arrayNode();
		json.put("parameterDimensions", parameterDimensions);
		for (BigInteger parameterDimension : config.getParameterDimensions()
				.getInt())
			parameterDimensions.add(parameterDimension.intValue());

		ArrayNode parameterTypes = json.arrayNode();
		json.put("parameterTypes", parameterTypes);
		for (String parameterType : config.getParameterTypes().getString())
			parameterTypes.add(parameterType);

		json.put("returnType", config.getReturnType());
		json.put("returnDimension", config.getReturnDimension().intValue());
		json.put("isMethodConstructor", config.isIsMethodConstructor());
		json.put("isMethodStatic", config.isIsMethodStatic());

		return configuration;
	}
}
