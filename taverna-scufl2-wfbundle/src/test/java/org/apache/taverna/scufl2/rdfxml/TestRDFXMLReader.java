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


import static org.apache.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.rdfxml.RDFXMLSerializer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class TestRDFXMLReader {

	private static final String EXAMPLE_SCUFL2 = "example.wfbundle";
	private URL exampleBundle;
	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	protected WorkflowBundle workflowBundle;
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	org.apache.taverna.scufl2.api.io.TestWorkflowBundleIO testWorkflowBundleIO = new org.apache.taverna.scufl2.api.io.TestWorkflowBundleIO();

	@Test
	public void bundleName() throws Exception {
		assertEquals("HelloWorld", workflowBundle.getName());
	}

	@Before
	public void exampleBundle() throws ReaderException, IOException {
		String name = EXAMPLE_SCUFL2;
		exampleBundle = getClass().getResource(name);
		assertNotNull("Can't find example workflow bundle " + name,
				exampleBundle);
		workflowBundle = bundleIO.readBundle(exampleBundle,
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}

	@Test
	public void iterationStrategy() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(1, hello.getIterationStrategyStack().size());
		IterationStrategyTopNode iterationStrategyTopNode = hello
				.getIterationStrategyStack().get(0);
		assertTrue(iterationStrategyTopNode instanceof CrossProduct);
		CrossProduct cross = (CrossProduct) iterationStrategyTopNode;
		assertEquals(1, cross.size());
		PortNode portNode = (PortNode) cross.get(0);
		assertEquals(hello.getInputPorts().getByName("name"),
				portNode.getInputProcessorPort());
		assertEquals(0, portNode.getDesiredDepth().intValue());
	}

	@Test
	public void iterationStrategyWait4Me() throws Exception {
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		assertEquals(0, wait4me.getIterationStrategyStack().size());
	}

	@Test
	public void processorInputPorts() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(1, hello.getInputPorts().size());
		assertEquals("name", hello.getInputPorts().getByName("name").getName());
		assertEquals(0, hello.getInputPorts().getByName("name").getDepth()
				.intValue());
	}

	@Test
	public void processorOutputPorts() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(1, hello.getOutputPorts().size());
		assertEquals("greeting", hello.getOutputPorts().getByName("greeting")
				.getName());
		assertEquals(0, hello.getOutputPorts().getByName("greeting").getDepth()
				.intValue());
		assertEquals(0, hello.getOutputPorts().getByName("greeting")
				.getGranularDepth().intValue());
	}

	@Test
	public void processorPortsWait4Me() throws Exception {
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		assertEquals(0, wait4me.getInputPorts().size());
		assertEquals(0, wait4me.getOutputPorts().size());
	}

	@Test
	public void processors() throws Exception {
		Set<String> expected = new HashSet<String>();
		expected.add("Hello");
		expected.add("wait4me");
		assertEquals(expected, workflowBundle.getMainWorkflow().getProcessors()
				.getNames());
	}

	@Test
	public void profiles() throws Exception {
		assertEquals(2, workflowBundle.getProfiles().size());
		Profile tavernaWorkbench = workflowBundle.getProfiles().getByName(
				"tavernaWorkbench");
		assertEquals("tavernaWorkbench", tavernaWorkbench.getName());
		assertEquals(workflowBundle.getMainProfile(), tavernaWorkbench);
		assertEquals("tavernaServer", workflowBundle.getProfiles().getByName("tavernaServer").getName());
	}

	@Test
	public void globalBaseURI() throws Exception {
		assertEquals("http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/", workflowBundle.getGlobalBaseURI().toASCIIString());
	}

	// TODO: Un-ignore and update this test
	@Ignore
	@Test
	public void testParsedWorkflow() throws Exception {
		assertEquals("HelloWorld", workflowBundle.getName());

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(workflowBundle, output,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		String bundleTxt = new String(output.toByteArray(), "UTF-8");

		assertEquals(testWorkflowBundleIO.getStructureFormatWorkflowBundle(),
				bundleTxt);

	}

	@Test
	public void guessMediaType() throws Exception {

		byte[] firstBytes = new byte[1024];
		getClass().getResourceAsStream(EXAMPLE_SCUFL2).read(firstBytes);		
		assertEquals(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE, bundleIO.guessMediaTypeForSignature(firstBytes));
		// Mess up the mime type string
		firstBytes[45] = 32;
		assertEquals(null, bundleIO.guessMediaTypeForSignature(firstBytes));
	}

	@Test
	public void readStreamNoMediaType() throws ReaderException, IOException {
		workflowBundle = bundleIO.readBundle(getClass().getResourceAsStream(EXAMPLE_SCUFL2), null);
		assertNotNull(workflowBundle);
	}
	
	@Test
	public void workflowIdentifier() throws Exception {
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/",
				workflowBundle.getMainWorkflow().getIdentifier()
						.toASCIIString());
	}

	@Test
	public void workflowInputPorts() throws Exception {
		NamedSet<InputWorkflowPort> inputPorts = workflowBundle
				.getMainWorkflow().getInputPorts();
		assertEquals(1, inputPorts.size());
		assertEquals("yourName", inputPorts.getByName("yourName").getName());
		assertEquals(0, inputPorts.getByName("yourName").getDepth().intValue());
	}

	@Test
	public void workflowOutputPorts() throws Exception {
		NamedSet<OutputWorkflowPort> outputPorts = workflowBundle
				.getMainWorkflow().getOutputPorts();
		assertEquals(1, outputPorts.size());
		assertEquals("results", outputPorts.getByName("results").getName());
	}

	@Test
	public void workflows() throws Exception {
		assertEquals(1, workflowBundle.getWorkflows().size());
		Workflow helloWorld = workflowBundle.getWorkflows().getByName(
				"HelloWorld");
		assertEquals("HelloWorld", helloWorld.getName());
		assertEquals(workflowBundle.getMainWorkflow(), helloWorld);
	}
	 
	@Test
	public void xmlOutput() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		new RDFXMLSerializer(workflowBundle).workflowDoc(output,
				workflowBundle.getMainWorkflow(),
				URI.create("workflows/HelloWorld.rdf"));
		@SuppressWarnings("unused")
		String bundleTxt = new String(output.toByteArray(), "UTF-8");
//		System.out.println(bundleTxt);

	}

}
