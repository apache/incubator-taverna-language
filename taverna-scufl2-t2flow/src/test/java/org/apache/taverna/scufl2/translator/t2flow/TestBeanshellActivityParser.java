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


import static org.apache.taverna.scufl2.translator.t2flow.defaultactivities.BeanshellActivityParser.ACTIVITY_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestBeanshellActivityParser {

	private static final String WF_AS = "/as.t2flow";
	private static final String WF_BEANSHELL_DEPS = "/beanshell-deps.t2flow";

	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void parseBeanshellScript() throws Exception {
		URL wfResource = getClass().getResource(WF_AS);
		assertNotNull("Could not find workflow " + WF_AS, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		// System.out.println(researchObj.getProfiles().iterator().next()
		// .getConfigurations());
		Profile profile = researchObj.getProfiles().getByName("taverna-2.1.0");
		// Processors: [Workflow19, Echo_List, Concatenate_two_strings,
		// Concatenate_two_strings_2, Concatenate_two_strings_3,
		// Concatenate_two_strings_4, Create_Lots_Of_Strings, String_constant]
		Processor concat = researchObj.getMainWorkflow().getProcessors()
				.getByName("Concatenate_two_strings");

		Configuration concatConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(concat, profile);		
		Activity concatAct = (Activity) concatConfig.getConfigures();
		assertEquals(ACTIVITY_URI,
				concatAct.getType());

		ObjectNode configResource = concatConfig.getJsonAsObjectNode();
		assertEquals(ACTIVITY_URI.resolve("#Config"), concatConfig.getType());
		
		assertEquals("http://ns.taverna.org.uk/2010/activity/localworker/org.embl.ebi.escience.scuflworkers.java.StringConcat", 
				configResource.get("derivedFrom").asText());
		
		String script = configResource.get("script").asText();
		assertEquals("output = string1 + string2;", script);

		assertFalse(configResource.has("classLoader"));
		assertFalse(configResource.has("dependency"));

		
		Set<String> expectedInputs = new HashSet<String>(Arrays.asList(
				"string1", "string2"));
		assertEquals(expectedInputs, concatAct.getInputPorts().getNames());
		InputActivityPort s1 = concatAct.getInputPorts().getByName("string1");
		assertEquals(0, s1.getDepth().intValue());
		InputActivityPort s2 = concatAct.getInputPorts().getByName("string2");
		assertEquals(0, s2.getDepth().intValue());

		/** TODO: Update tests
		Set<PropertyResource> inputDef = configResource
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#inputPortDefinition"));
		assertEquals(2, inputDef.size());

		Set<URI> expectedPortUris = new HashSet<URI>();
		for (InputActivityPort inPort : concatAct.getInputPorts()) {
			expectedPortUris.add(new URITools().relativeUriForBean(inPort,
					concatConfig));
		}
		assertEquals(2, expectedPortUris.size());
		assertEquals(2, inputDef.size());
		for (PropertyResource portDef : inputDef) {
			assertEquals(PORT_DEFINITION.resolve("#InputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI dataType = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#dataType"));

			assertEquals("java", dataType.getScheme());
			assertEquals("java.lang.String", dataType
					.getSchemeSpecificPart());

			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesInputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
			
		}

		// TODO: Is java class here OK? It's a beanshell script after all..

		Set<String> expectedOutputs = new HashSet<String>(
				Arrays.asList("output"));
		assertEquals(expectedOutputs, concatAct.getOutputPorts().getNames());
		OutputActivityPort out = concatAct.getOutputPorts().getByName("output");
		assertEquals(0, out.getDepth().intValue());

		Set<PropertyResource> outputDef = configResource
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#outputPortDefinition"));
		assertEquals(1, outputDef.size());
		PropertyResource out1Def = outputDef.iterator().next();
		assertEquals(PORT_DEFINITION.resolve("#OutputPortDefinition"),
				out1Def.getTypeURI());

		Set<URI> mimeTypes = out1Def.getPropertiesAsResourceURIs(PORT_DEFINITION
				.resolve("#expectedMimeType"));
		assertEquals(1, mimeTypes.size());

		assertEquals(URI.create("http://purl.org/NET/mediatypes/text/plain"),
				mimeTypes.iterator().next());

		 */

		Processor echoList = researchObj.getMainWorkflow().getProcessors()
				.getByName("Echo_List");
		Configuration echoConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(echoList, profile);
		Activity echoAct = (Activity) echoConfig.getConfigures();

		expectedInputs = new HashSet<String>(Arrays.asList("inputlist"));
		assertEquals(expectedInputs, echoAct.getInputPorts().getNames());
		InputActivityPort inputList = echoAct.getInputPorts().getByName(
				"inputlist");
		assertEquals(1, inputList.getDepth().intValue());
		/* TODO: Update tests
		expectedOutputs = new HashSet<String>(Arrays.asList("outputlist"));
		assertEquals(expectedOutputs, echoAct.getOutputPorts().getNames());
		OutputActivityPort outputList = echoAct.getOutputPorts().getByName(
				"outputlist");
		assertEquals(1, outputList.getDepth().intValue());
		*/
	}
	

	@Test
	public void beanshellDependencies() throws Exception {
		URL wfResource = getClass().getResource(WF_BEANSHELL_DEPS);
		assertNotNull("Could not find workflow " + WF_BEANSHELL_DEPS, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser
				.parseT2Flow(wfResource.openStream());

		Profile profile = wfBundle.getMainProfile();
		// Processors: [Workflow19, Echo_List, Concatenate_two_strings,
		// Concatenate_two_strings_2, Concatenate_two_strings_3,
		// Concatenate_two_strings_4, Create_Lots_Of_Strings, String_constant]
		Processor a_c_workflow = wfBundle.getMainWorkflow().getProcessors()
				.getByName("A_C_workflow");
		Configuration a_c_config = scufl2Tools
				.configurationForActivityBoundToProcessor(a_c_workflow, profile);
		assertNotNull(a_c_config);
		Configuration c2 = a_c_workflow.getActivityConfiguration(profile);
		assertEquals(a_c_config,c2);
/* TODO: Update tests
		PropertyResource a_c_configResource = a_c_config.getJson();
		
		assertFalse(a_c_configResource.hasProperty(ACTIVITY_URI.resolve("#derivedFrom")));
		SortedSet<PropertyResource> deps = a_c_configResource.getPropertiesAsResources(DEPENDENCY_URI.resolve("#dependency"));
		Set<String> jars = new HashSet<String>();
		for (PropertyResource dep : deps) {
			assertEquals(DEPENDENCY_URI.resolve("#LocalJarDependency"), dep.getTypeURI());
			jars.add(dep.getPropertyAsString(DEPENDENCY_URI.resolve("#jarFile")));
		}
		assertEquals(new HashSet<String>(Arrays.asList("libraryA.jar", "libraryC.jar")),
				jars);
		assertFalse(a_c_configResource.hasProperty(DEPENDENCY_URI.resolve("#classLoader")));

		
		Processor b_system = wfBundle.getMainWorkflow().getProcessors()
				.getByName("B_system");
		Configuration b_config = scufl2Tools
				.configurationForActivityBoundToProcessor(b_system, profile);		

		PropertyResource b_configResource = b_config.getJson();
		
		deps = b_configResource.getPropertiesAsResources(DEPENDENCY_URI.resolve("#dependency"));
		jars = new HashSet<String>();
		for (PropertyResource dep : deps) {
			assertEquals(DEPENDENCY_URI.resolve("#LocalJarDependency"), dep.getTypeURI());
			jars.add(dep.getPropertyAsString(DEPENDENCY_URI.resolve("#jarFile")));
		}
		assertEquals(new HashSet<String>(Arrays.asList("libraryB.jar")),
				jars);
		assertTrue(b_configResource.hasProperty(DEPENDENCY_URI.resolve("#classLoader")));
		assertEquals(DEPENDENCY_URI.resolve("#SystemClassLoader"), 
				b_configResource.getPropertyAsResourceURI(DEPENDENCY_URI.resolve("#classLoader")));
		
 */
		
	}
	

}
