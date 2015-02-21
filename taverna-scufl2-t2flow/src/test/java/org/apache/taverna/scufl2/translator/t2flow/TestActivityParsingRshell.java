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


import static org.apache.taverna.scufl2.api.common.Scufl2Tools.PORT_DEFINITION;
import static org.apache.taverna.scufl2.translator.t2flow.defaultactivities.RshellActivityParser.ACTIVITY_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("unused")
public class TestActivityParsingRshell {

	private static final String WF_RSHELL_2_2 = "/rshell-2-2.t2flow";
	private static final String WF_RSHELL_2_3 = "/rshell-2-3.t2flow";
	private static final String WF_RSHELL_SIMPLE_2_3 = "/rshell-simple-2-3.t2flow";

	private static final String WF_ALL_ACTIVITIES = "/defaultActivitiesTaverna2.2.t2flow";
	
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private static URITools uriTools = new URITools();
	private T2FlowParser parser;

	@Before
	public void makeParser() throws JAXBException {
		parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		
	}
	
	@Test
	public void parseRShell22WithReferences() throws Exception {
		URL wfResource = getClass().getResource(WF_RSHELL_2_2);
		assertNotNull("Could not find workflow " + WF_RSHELL_2_2, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		// TODO: Check data types defined (semantic types)
		
	}
	
	/** FIXME: Update tests for JSON config

	@Test
	public void parseRShell23() throws Exception {
		URL wfResource = getClass().getResource(WF_RSHELL_2_3);
		assertNotNull("Could not find workflow " + WF_RSHELL_2_3, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);

		Activity activity = (Activity) config.getConfigures();

//		System.out.println(activity.getInputPorts().getNames());
//		System.out.println(activity.getOutputPorts().getNames());

		
		assertEquals(ACTIVITY_URI.resolve("#BOOL_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("logVec"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		
		assertEquals(ACTIVITY_URI.resolve("#R_EXP"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("regxp"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#STRING"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("str"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#STRING"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("str2"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));

		assertEquals(ACTIVITY_URI.resolve("#INTEGER"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("int"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#INTEGER_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("intVector"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#BOOL"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("log"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#BOOL_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("logicVector"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));

		assertEquals(ACTIVITY_URI.resolve("#DOUBLE"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("num"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#DOUBLE_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("numVector"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));

		assertEquals(ACTIVITY_URI.resolve("#PNG_FILE"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("png"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#R_EXP"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("rexpr"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));

		
		assertEquals(ACTIVITY_URI.resolve("#STRING"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("str"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#STRING_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("strVector"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));

		assertEquals(ACTIVITY_URI.resolve("#TEXT_FILE"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("txt"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		
	}
	


	@Test
	public void parseRShellAllActiv() throws Exception {
		URL wfResource = getClass().getResource(WF_ALL_ACTIVITIES);
		assertNotNull("Could not find workflow " + WF_ALL_ACTIVITIES, wfResource);
		parser.setStrict(false); // Ignore other broken activities
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		
	}
		
	@Test
	public void parseSimpleRShellScript() throws Exception {
		URL wfResource = getClass().getResource(WF_RSHELL_SIMPLE_2_3);
		assertNotNull("Could not find workflow " + WF_RSHELL_SIMPLE_2_3, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
				
		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI,
				activity.getType());
		assertEquals(ACTIVITY_URI.resolve("#Config"), config
				.getJson().getTypeURI());
		String script = config.getJson().getPropertyAsString(
				ACTIVITY_URI.resolve("#script"));
		assertEquals("too\nsimple", script);

		Set<String> expectedInputs = new HashSet<String>(Arrays.asList(
				"in1", "in2", "in3"));
		assertEquals(expectedInputs, activity.getInputPorts().getNames());
		InputActivityPort in1 = activity.getInputPorts().getByName("in1");
		assertEquals(0, in1.getDepth().intValue());
		InputActivityPort in2 = activity.getInputPorts().getByName("in2");
		assertEquals(0, in2.getDepth().intValue());
		InputActivityPort in3 = activity.getInputPorts().getByName("in3");
		assertEquals(0, in3.getDepth().intValue());

		Set<PropertyResource> inputDef = config.getJson()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#inputPortDefinition"));
		assertEquals(3, inputDef.size());

		Set<URI> expectedPortUris = new HashSet<URI>();
		for (InputActivityPort inPort : activity.getInputPorts()) {
			expectedPortUris.add(uriTools.relativeUriForBean(inPort,
					config));
			PropertyResource portDef = scufl2Tools.portDefinitionFor(inPort, profile);
			assertNotNull("Could not find port definition for port " + inPort, portDef);
		}
		assertEquals(3, expectedPortUris.size());
		assertEquals(3, inputDef.size());
		
		Map<URI, URI> dataTypes = new HashMap<URI, URI>();
		
		for (PropertyResource portDef : inputDef) {
			assertEquals(PORT_DEFINITION.resolve("#InputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesInputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
			
			URI dataType = portDef.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType"));
			assertEquals(ACTIVITY_URI.resolve("#samePrefix"), dataType.resolve("#samePrefix"));			
			// For instance http://ns.taverna.org.uk/2010/activity/rshell#BOOL_LIST
			dataTypes.put(portURI, dataType);
			
			
		}


		Set<String> expectedOutputs = new HashSet<String>(
				Arrays.asList("out1", "out2", "out3"));
		assertEquals(expectedOutputs, activity.getOutputPorts().getNames());
		OutputActivityPort out1 = activity.getOutputPorts().getByName("out1");
		assertEquals(0, out1.getDepth().intValue());
		OutputActivityPort out2 = activity.getOutputPorts().getByName("out2");
		assertEquals(1, out2.getDepth().intValue());
		OutputActivityPort out3 = activity.getOutputPorts().getByName("out3");
		assertEquals(1, out3.getDepth().intValue());

		expectedPortUris.clear();
		for (OutputActivityPort outPort : activity.getOutputPorts()) {
	
			expectedPortUris.add(uriTools.relativeUriForBean(outPort,
					config));
			PropertyResource portDef = scufl2Tools.portDefinitionFor(outPort, profile);
			assertNotNull("Could not find port definition for port " + outPort, portDef);

		}
		
		Set<PropertyResource> outputDef = config.getJson()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#outputPortDefinition"));
		assertEquals(3, outputDef.size());
		for (PropertyResource portDef : outputDef) {
			assertEquals(PORT_DEFINITION.resolve("#OutputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesOutputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
			
			URI dataType = portDef.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType"));
			assertEquals(ACTIVITY_URI.resolve("#samePrefix"), dataType.resolve("#samePrefix"));			
			// For instance http://ns.taverna.org.uk/2010/activity/rshell#BOOL_LIST
			dataTypes.put(portURI, dataType);
			
		}


		//System.out.println(dataTypes);
		
		assertEquals(ACTIVITY_URI.resolve("#STRING"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("in1"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		
		assertEquals(ACTIVITY_URI.resolve("#DOUBLE"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("in2"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#STRING"), 
				scufl2Tools.portDefinitionFor(activity.getInputPorts().getByName("in3"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#STRING"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("out1"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#BOOL_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("out2"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#STRING_LIST"), 
				scufl2Tools.portDefinitionFor(activity.getOutputPorts().getByName("out3"), profile).
				getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		

		PropertyResource connection = config.getJson().getPropertyAsResource(ACTIVITY_URI.resolve("#connection"));
		assertEquals(ACTIVITY_URI.resolve("#Connection"), connection.getTypeURI());

		assertEquals("localhost", connection.getPropertyAsString(ACTIVITY_URI.resolve("#hostname")));
		
		PropertyLiteral portLiteral = connection.getPropertyAsLiteral(ACTIVITY_URI.resolve("#port"));
		assertEquals(6311, portLiteral.getLiteralValueAsInt());
		assertEquals(PropertyLiteral.XSD_UNSIGNEDSHORT, portLiteral.getLiteralType());

		assertEquals(false, connection.getPropertyAsLiteral(ACTIVITY_URI.resolve("#keepSessionAlive")).getLiteralValueAsBoolean());
	}

	*/
}
