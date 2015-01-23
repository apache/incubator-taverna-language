/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl.processorelement;
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


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;

import org.apache.taverna.scufl2.xml.scufl.jaxb.StringconstantType;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author alanrw
 */
public class StringConstantExtensionParser extends AbstractExtensionParser {
	private static final String STRINGCONSTANT_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-stringconstant.xsd";

	private static final String VALUE = "value";

	public static URI CONSTANT = URI
			.create("http://ns.taverna.org.uk/2010/activity/constant");

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(org.apache.taverna.scufl2.xml.scufl.jaxb.StringconstantType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL stringConstantXsd = getClass().getResource(STRINGCONSTANT_XSD);
		try {
			return Arrays.asList(stringConstantXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(
					"Can't find String Constant schema " + stringConstantXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		StringconstantType sc = (StringconstantType) o;
		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());
		configuration.setType(CONSTANT.resolve("#Config"));
		((ObjectNode) configuration.getJson()).put("string", sc.getValue());

		Activity activity = new Activity();
		getParserState().setCurrentActivity(activity);
		activity.setParent(getParserState().getCurrentProfile());
		activity.setType(CONSTANT);
		OutputActivityPort valuePort = new OutputActivityPort(activity, VALUE);
		valuePort.setDepth(0);
		valuePort.setGranularDepth(0);
		configuration.setConfigures(activity);

		ProcessorBinding pb = new ProcessorBinding();
		pb.setParent(getParserState().getCurrentProfile());
		pb.setBoundProcessor(getParserState().getCurrentProcessor());
		pb.setBoundActivity(activity);
	}
}
