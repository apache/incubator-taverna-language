package com.example;

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

import org.junit.Ignore;
import org.junit.Test;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

public class TestProcessorNames {

	@Test
	public void processorNames() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/t2flow/biomartandembossanalysis_904962.t2flow");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow,
				"application/vnd.taverna.t2flow+xml");

		List<String> expected = Arrays.asList("CreateFasta",
				"FlattenImageList", "GetUniqueHomolog", "emma",
				"getHSapSequence", "getMMusSequence", "getRNorSequence",
				"hsapiensGeneEnsembl", "plot", "seqret");
		ProcessorNames processorNames = new ProcessorNames();
		assertEquals(expected, processorNames.showProcessorNames(ro));
		System.out.println(processorNames.showProcessorTree(ro));
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
		System.out.println(processorNames.showProcessorTree(ro));
	}
	

	@Test
	public void nestedWorkflowBundle() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/wfbundle/as.wfbundle");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow, null);
		ProcessorNames processorNames = new ProcessorNames();
		System.out.println(processorNames.showProcessorTree(ro));
	}
	
}
