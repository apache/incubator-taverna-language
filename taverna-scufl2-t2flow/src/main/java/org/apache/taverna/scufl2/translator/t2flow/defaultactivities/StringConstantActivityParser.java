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

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.StringConstantConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class StringConstantActivityParser extends AbstractActivityParser {
	private static final String VALUE = "value";
	private static final URI activityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/stringconstant-activity/");
	private static final String activityClassName = "net.sf.taverna.t2.activities.stringconstant.StringConstantActivity";
	public static final URI CONSTANT = URI
			.create("http://ns.taverna.org.uk/2010/activity/constant");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return CONSTANT;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {
		StringConstantConfig strConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", StringConstantConfig.class);
		String value = strConfig.getValue();
		if (value == null)
			throw new ReaderException("String constant configuration has no value set");
		Configuration configuration = new Configuration();
		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(CONSTANT.resolve("#Config"));
		json.put("string", value);
		
		Activity activity = parserState.getCurrentActivity();
		OutputActivityPort valuePort = new OutputActivityPort(activity, VALUE);
		valuePort.setDepth(0);
		valuePort.setGranularDepth(0);
		return configuration;
	}
}
