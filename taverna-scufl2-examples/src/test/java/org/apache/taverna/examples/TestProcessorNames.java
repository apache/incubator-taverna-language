package org.apache.taverna.examples;

/*
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
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;

public class TestProcessorNames {

	@Test
	public void processorNames() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/t2flow/defaultActivitiesTaverna2.2.t2flow");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow,
				"application/vnd.taverna.t2flow+xml");

		List<String> expected = Arrays.asList("Beanshell", "Nested_workflow",
				"Rshell", "Send_an_Email", "SpreadsheetImport",
				"String_constant", "TavernaResearchObject", "biomart",
				"localWorker", "localWorker_bytearray", "mobyObject",
				"mobyService", "run", "run_input", "run_output",
				"setWorkflows", "soaplab", "wsdl_document", "wsdl_rpc",
				"wsdl_secured", "xmlSplitter");
		ProcessorNames processorNames = new ProcessorNames();
		assertEquals(expected, processorNames.showProcessorNames(ro));
	}
	

	@Test
	public void nestedWorkflow() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/t2flow/as.t2flow");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow,
				"application/vnd.taverna.t2flow+xml");
		ProcessorNames processorNames = new ProcessorNames();
		assertEquals(8, processorNames.showProcessorNames(ro).size());
	}
	

	@Test
	public void nestedWorkflowBundle() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/wfbundle/as.wfbundle");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow, null);
		ProcessorNames processorNames = new ProcessorNames();
		assertEquals(8, processorNames.showProcessorNames(ro).size());
	}
	
}
