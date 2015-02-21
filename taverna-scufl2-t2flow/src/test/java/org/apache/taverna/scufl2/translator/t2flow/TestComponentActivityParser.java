package org.apache.taverna.scufl2.translator.t2flow;
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


import static org.apache.taverna.scufl2.translator.t2flow.defaultactivities.ComponentActivityParser.ACTIVITY_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Iterator;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestComponentActivityParser {
	private static final String WF_SIMPLE_COMPONENT = "/component_simple.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	private WorkflowBundle parseWorkflow(String wfPath) throws Exception {
		URL wfResource = getClass().getResource(wfPath);
		assertNotNull("could not find workflow " + wfPath, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		return researchObj;
	}

	@Test
	public void parseSimpleTell() throws Exception {
		WorkflowBundle researchObj = parseWorkflow(WF_SIMPLE_COMPONENT);
		Profile profile = researchObj.getMainProfile();
		assertNotNull("could not find profile in bundle", profile);

		Processor comp = researchObj.getMainWorkflow().getProcessors()
				.getByName("combiner");
		assertNotNull("could not find processor 'combiner'", comp);

		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(comp, profile);

		Activity act = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, act.getType());

		ObjectNode resource = config.getJsonAsObjectNode();
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());

		int length = 0;
		Iterator<?> i = resource.fieldNames();
		while (i.hasNext()) {
			i.next();
			length++;
		}
		assertEquals("must be exactly 4 items in the translated component", 4,
				length);

		assertEquals("http://www.myexperiment.org", resource
				.get("registryBase").textValue());
		assertEquals("SCAPE Utility Components", resource.get("familyName")
				.textValue());
		assertEquals("MeasuresDocCombiner", resource.get("componentName")
				.textValue());
		assertEquals(1, resource.get("componentVersion").asInt());

		assertEquals(2, comp.getInputPorts().size());
		assertEquals(1, comp.getOutputPorts().size());
	}
}
