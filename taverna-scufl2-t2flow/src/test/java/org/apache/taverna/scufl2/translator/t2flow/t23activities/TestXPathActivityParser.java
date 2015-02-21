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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;


public class TestXPathActivityParser {

	private static final String XPATH_WORKFLOW_SANS_EXAMPLE_XML = "/xpath_workflow.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void parseXPathActivityWorkflow() throws Exception {
		URL wfResource = getClass().getResource(XPATH_WORKFLOW_SANS_EXAMPLE_XML);
		assertNotNull("Could not find workflow " + XPATH_WORKFLOW_SANS_EXAMPLE_XML, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(false);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Profile profile = wfBundle.getMainProfile();
		//XPath_height has missing xmlDocument from its configuration
		Processor heightProc = wfBundle.getMainWorkflow().getProcessors().getByName("XPath_height");
		ObjectNode heightConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(heightProc, profile).getJsonAsObjectNode();
		assertNotNull(heightConfig);
		assertEquals("//height/text()",heightConfig.get("xpathExpression").textValue());
		assertFalse(heightConfig.has("exampleXmlDocument"));
		//XPath_width has xmlDocument
		Processor widthProc = wfBundle.getMainWorkflow().getProcessors().getByName("XPath_width");
		ObjectNode widthConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(widthProc, profile).getJsonAsObjectNode();		
		assertNotNull(widthConfig);		
		assertEquals("//width/text()",widthConfig.get("xpathExpression").asText());		
		assertTrue(widthConfig.has("exampleXmlDocument"));		
	}
	
}
