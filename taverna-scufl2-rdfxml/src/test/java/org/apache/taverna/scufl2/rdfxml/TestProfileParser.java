package org.apache.taverna.scufl2.rdfxml;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.rdfxml.ProfileParser;
import org.junit.Before;
import org.junit.Test;


public class TestProfileParser {
	private static final String PROFILE_RDF = "example/profile/tavernaWorkbench.rdf";

	protected ProfileParser profileParser = new ProfileParser();

	private URL profileUrl;

	private WorkflowBundle bundle;

	private Profile profile;

	@Test
	public void activity() throws Exception {
		assertEquals(1, profile.getActivities().size());
		Activity helloScript = profile.getActivities().getByName("HelloScript");
		assertEquals("HelloScript", helloScript.getName());
		assertEquals(
"http://ns.taverna.org.uk/2010/activity/beanshell",
				helloScript.getType().toASCIIString());
		assertEquals(1, helloScript.getInputPorts().size());
		InputActivityPort personName = helloScript.getInputPorts().getByName(
				"personName");
		assertEquals("personName", personName.getName());
		assertEquals(0, personName.getDepth().intValue());

		assertEquals(1, helloScript.getOutputPorts().size());
		OutputActivityPort hello = helloScript.getOutputPorts().getByName(
				"hello");
		assertEquals("hello", hello.getName());
		assertEquals(0, hello.getDepth().intValue());
		assertEquals(0, hello.getGranularDepth().intValue());

	}

	@Test
	public void configuration() throws Exception {
		assertEquals(1, profile.getConfigurations().size());
		Configuration hello = profile.getConfigurations().getByName("Hello");
		assertEquals("Hello", hello.getName());
		assertEquals(profile.getActivities().getByName("HelloScript"),
				hello.getConfigures());
		assertEquals(
"http://ns.taverna.org.uk/2010/activity/beanshell#Config",
				hello.getType().toASCIIString());
	}

	public void loadProfileDocument() {
		profileUrl = getClass().getResource(PROFILE_RDF);
		assertNotNull("Could not find profile document " + PROFILE_RDF,
				profileUrl);
	}

	@Test
	public void parserStackEmpty() throws Exception {
		Stack<WorkflowBean> stack = profileParser.getParserState().getStack();
		assertEquals(1, stack.size());
		assertEquals(bundle, stack.peek());
	}

	@Test
	public void portBindings() throws Exception {
		ProcessorBinding hello = profile.getProcessorBindings().getByName(
				"Hello");
		assertEquals(1, hello.getInputPortBindings().size());
		assertEquals(1, hello.getOutputPortBindings().size());

		ProcessorInputPortBinding input = hello.getInputPortBindings()
				.iterator().next();
		InputActivityPort inputActivityPort = profile.getActivities()
				.getByName("HelloScript").getInputPorts()
				.getByName("personName");
		assertEquals(inputActivityPort, input.getBoundActivityPort());

		InputProcessorPort inputProcessorPort = bundle.getMainWorkflow()
				.getProcessors().getByName("Hello").getInputPorts()
				.getByName("name");
		assertEquals(inputProcessorPort, input.getBoundProcessorPort());

		ProcessorOutputPortBinding output = hello.getOutputPortBindings()
				.iterator().next();
		OutputActivityPort outputActivityPort = profile.getActivities()
				.getByName("HelloScript").getOutputPorts().getByName("hello");
		assertEquals(outputActivityPort, output.getBoundActivityPort());

		OutputProcessorPort outputProcessorPort = bundle.getMainWorkflow()
				.getProcessors().getByName("Hello").getOutputPorts()
				.getByName("greeting");
		assertEquals(outputProcessorPort, output.getBoundProcessorPort());

	}

	public void prepareParserState() throws URISyntaxException {
		bundle = new ExampleWorkflow().makeWorkflowBundle();
		bundle.getProfiles().clear();
		bundle.setMainProfile(null);

		profileParser.getParserState().setLocation(URI.create("/"));
		profileParser.getParserState().push(bundle);
	}

	@Test
	public void processorBinding() throws Exception {
		assertEquals(1, profile.getProcessorBindings().size());
		ProcessorBinding hello = profile.getProcessorBindings().getByName(
				"Hello");
		assertEquals(profile.getActivities().getByName("HelloScript"),
				hello.getBoundActivity());
		assertEquals(bundle.getMainWorkflow().getProcessors()
				.getByName("Hello"), hello.getBoundProcessor());
		assertEquals("Hello", hello.getName());
		assertEquals(10, hello.getActivityPosition().intValue());
	}

	@Test
	public void profileName() throws Exception {
		assertEquals("tavernaWorkbench", profile.getName());
	}

	@Before
	public void readProfile() throws Exception {
		loadProfileDocument();
		prepareParserState();
		profileParser.readProfile(URI.create("/profile/tavernaWorkbench/"),
				URI.create("profile/tavernaWorkbench.rdf"),
				profileUrl.openStream());
		profile = bundle.getProfiles().getByName("tavernaWorkbench");
		assertNotNull(profile);
	}

}
