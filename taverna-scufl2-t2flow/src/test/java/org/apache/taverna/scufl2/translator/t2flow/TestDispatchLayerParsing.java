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
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Before;
import org.junit.Test;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestDispatchLayerParsing {

    private static final String LOOP = "loop";
	private static final String INVOKE = "invoke";
    private static final String RETRY = "retry";
    private static final String FAILOVER = "failover";
    private static final String ERRORBOUNCE = "errorbounce";
    private static final String PARALLELIZE = "parallelize";
    private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private T2FlowParser parser;
	private WorkflowBundle wfBundle;
	private Profile profile;
	private Workflow workflow;
	private NamedSet<Processor> processors;
	
	private String WF_DISPATCH_LAYERS = "/dispatchlayers.t2flow";
	
	@Before
	public void readWorkflow() throws Exception {		
		parser = new T2FlowParser();
		parser.setValidating(true);
		URL wfResource = getClass().getResource(WF_DISPATCH_LAYERS);
		assertNotNull("Could not find workflow " + WF_DISPATCH_LAYERS,
				wfResource);		
		// parser.setStrict(true);
		wfBundle = parser
				.parseT2Flow(wfResource.openStream());
		profile = wfBundle.getMainProfile();
		workflow = wfBundle.getMainWorkflow();
		processors = workflow.getProcessors();
	}
	

	@Test
	public void whichLayers() throws Exception {
		Processor parallelise = processors.getByName("retries");
		// As inspected in /scufl2-t2flow/src/test/resources/dispatchlayers-xsd.t2flow

		//		List<String> expectedNames = Arrays.asList(PARALLELIZE, ERRORBOUNCE, FAILOVER, RETRY, INVOKE);
		// NOTE: Only those with configuration are present
		List<String> expectedNames = Arrays.asList(RETRY);
        
		Configuration config = scufl2Tools.configurationFor(parallelise, profile);
		ObjectNode json = config.getJsonAsObjectNode();
        for (String name : expectedNames) {
		    assertTrue("Could not find config for dispatch layer " + name, 
		            json.has(name)); 		}
		assertEquals("Additional dispatch layer configurations found", 
		        expectedNames.size(), json.size());

	}
	
	@Test
	public void retriesDefault() throws Exception {
		Processor parallelise = processors.getByName("parallelise");
		Configuration config = scufl2Tools.configurationFor(parallelise, profile);
		JsonNode retry = config.getJsonAsObjectNode().get(RETRY);
		assertNull(retry);
	}
	
	@Test
	public void retriesDefaultFromT1() throws Exception {
		Processor alternates = processors.getByName("alternates");
        Configuration config = scufl2Tools.configurationFor(alternates, profile);
        JsonNode retry = config.getJsonAsObjectNode().get(RETRY);
        assertNull(retry);
	}
	
	
	@Test
	public void parallelizeDefault() throws Exception {
		Processor retry = processors.getByName("retries");
        Configuration config = scufl2Tools.configurationFor(retry, profile);
        JsonNode parallelize = config.getJsonAsObjectNode().get(PARALLELIZE);
        assertNull(parallelize);
	}
	
	@Test
	public void errorBounceEmpty() throws Exception {
		Processor retry = processors.getByName("retries");
        Configuration config = scufl2Tools.configurationFor(retry, profile);
        JsonNode errorbounce = config.getJsonAsObjectNode().get(ERRORBOUNCE);
        assertNull(errorbounce);
	}
	

	@Test
	public void failoverEmpty() throws Exception {	    
	    Processor retry = processors.getByName("retries");
        Configuration config = scufl2Tools.configurationFor(retry, profile);
        JsonNode failover = config.getJsonAsObjectNode().get(FAILOVER);
        assertNull(failover);	    
	}
	
	@Test
	public void invokeEmpty() throws Exception {
		Processor retry = processors.getByName("retries");
        Configuration config = scufl2Tools.configurationFor(retry, profile);
        assertNull(config.getJsonAsObjectNode().get(INVOKE));
	}
	

	@Test
	public void parallelizeDefaultFromT1() throws Exception {
		Processor alternates = processors.getByName("alternates");
		Configuration config = scufl2Tools.configurationFor(alternates, profile);
        assertNull(config.getJsonAsObjectNode().get(PARALLELIZE));
	}
	
	@Test
	public void parallelize() throws Exception {
		Processor proc = processors.getByName("parallelise");
        Configuration config = scufl2Tools.configurationFor(proc, profile);
        JsonNode parallelize = config.getJsonAsObjectNode().get(PARALLELIZE);
		assertEquals(5, parallelize.get("maxJobs").intValue());
	}
	
	
	@Test
	public void retriesCustom() throws Exception {
		Processor retries = processors.getByName("retries_custom");

        Configuration config = scufl2Tools.configurationFor(retries, profile);
        JsonNode retry = config.getJsonAsObjectNode().get(RETRY);

        assertEquals(5, retry.get("maxRetries").intValue());
        assertEquals(1337, retry.get("initialDelay").intValue());
        assertEquals(7000, retry.get("maxDelay").intValue());
        assertEquals(1.13, retry.get("backoffFactor").doubleValue(), 0.01);
        assertEquals(4, retry.size());
	}
	

	@Test
	public void retries() throws Exception {
		Processor retries = processors.getByName("retries");
        Configuration config = scufl2Tools.configurationFor(retries, profile);
        JsonNode retry = config.getJsonAsObjectNode().get(RETRY);

        assertEquals(3, retry.get("maxRetries").intValue());
        // The remaining properties are at default and should NOT be present
//        assertEquals(1000, retry.get("initialDelay").intValue());
//        assertEquals(5000, retry.get("maxDelay").intValue());
//        assertEquals(1.0, retry.get("backoffFactor").doubleValue(), 0.01);        
        assertEquals(1, retry.size());
	}

    @Test
    public void looping() throws Exception {
        Processor looping = processors.getByName("looping");
        Configuration config = scufl2Tools.configurationFor(looping, profile);
        ObjectNode json = config.getJsonAsObjectNode();
        JsonNode loop = json.get(LOOP);
//        System.out.println(loop);
        String activityName = loop.get("conditionActivity").asText();
        Activity activity = profile.getActivities().getByName(activityName);
        assertNotNull("Unknown activity " + activityName, activity);
        
        assertEquals(true, loop.get("runFirst").asBoolean());

        // The properties
        assertEquals("fred", loop.get("compareValue").asText());
        assertEquals("value", loop.get("comparePort").asText());
        assertEquals(0.5, loop.get("delay").asDouble(), 0.01);
        assertEquals(false, loop.get("isFeedBack").asBoolean());
    }
	
}

