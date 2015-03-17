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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


public class TestT2FlowParser {

	private static final String INTERACTION_WITH_LOOP = "/interaction-with-strange-loop.t2flow";
	private static final String AS_T2FLOW = "/as.t2flow";
	
	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Profile profile = wfBundle.getMainProfile();
		assertEquals(1, wfBundle.getProfiles().size());
		assertEquals(profile, wfBundle.getProfiles().getByName("taverna-2.1.0"));
		
	}
	
	@Test
	public void unconfigureLoopLayer() throws Exception {
		URL wfResource = getClass().getResource(INTERACTION_WITH_LOOP);
		assertNotNull("Could not find workflow " + INTERACTION_WITH_LOOP, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(false);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Scufl2Tools scufl2Tools = new Scufl2Tools();
		Processor interaction = wfBundle.getMainWorkflow().getProcessors().getByName("BioSTIFInteraction");
		
		Configuration config = scufl2Tools.configurationFor(interaction, wfBundle.getMainProfile());
		assertNull(config.getJsonAsObjectNode().get("loop"));
	}
	
}
