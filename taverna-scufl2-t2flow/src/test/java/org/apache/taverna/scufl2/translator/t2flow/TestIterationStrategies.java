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


import static org.apache.taverna.scufl2.translator.t2flow.T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.junit.Before;
import org.junit.Test;


public class TestIterationStrategies {
	
	private static final String ITERATIONSTRATEGIES_T2FLOW = "/iterationstrategies.t2flow";
	private WorkflowBundle wfBundle;
	private Workflow wf;
	private Processor coloursLisr;
	private Processor concat;
	private Processor shape;

	@Before
	public void readWorkflow() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		InputStream is = getClass().getResourceAsStream(ITERATIONSTRATEGIES_T2FLOW);
		wfBundle = io.readBundle(is, APPLICATION_VND_TAVERNA_T2FLOW_XML);
		wf = wfBundle.getMainWorkflow();
		coloursLisr = wf.getProcessors().getByName("ColoursLisr");
		concat = wf.getProcessors().getByName("Concatenate_two_strings");
		shape = wf.getProcessors().getByName("ShapeAnimals");
	}

	@Test
	public void simpleCrossProduct() throws Exception {
		assertEquals(1, coloursLisr.getIterationStrategyStack().size());
		IterationStrategyTopNode top = coloursLisr.getIterationStrategyStack().get(0);
		assertTrue(top instanceof CrossProduct);
		assertEquals(1, top.size());
		IterationStrategyNode node = top.get(0);
		assertTrue(node instanceof PortNode);
		PortNode portNode = (PortNode) node;
		assertEquals(0, portNode.getDesiredDepth().intValue());
		assertEquals(coloursLisr.getInputPorts().getByName("string"), portNode.getInputProcessorPort());
		
	}

	@Test
	public void simpleDot() throws Exception {
		assertEquals(1, concat.getIterationStrategyStack().size());
		IterationStrategyTopNode top = concat.getIterationStrategyStack().get(0);
		assertTrue(top instanceof DotProduct);
		assertEquals(2, top.size());
		IterationStrategyNode node1 = top.get(0);
		assertTrue(node1 instanceof PortNode);
		PortNode portNode1 = (PortNode) node1;
		assertEquals(0, portNode1.getDesiredDepth().intValue());
		assertEquals(concat.getInputPorts().getByName("string1"), portNode1.getInputProcessorPort());

		IterationStrategyNode node2 = top.get(1);
		assertTrue(node2 instanceof PortNode);
		PortNode portNode2 = (PortNode) node2;
		assertEquals(0, portNode2.getDesiredDepth().intValue());
		assertEquals(concat.getInputPorts().getByName("string2"), portNode2.getInputProcessorPort());

		assertEquals(concat.getInputPorts().getByName("string2"), portNode2.getInputProcessorPort());
		
	}


	@Test
	public void crossAndDot() throws Exception {
		assertEquals(1, shape.getIterationStrategyStack().size());
		IterationStrategyTopNode top = shape.getIterationStrategyStack().get(0);
		assertTrue(top instanceof CrossProduct);
		assertEquals(2, top.size());
		IterationStrategyNode node1 = top.get(0);
		assertTrue(node1 instanceof PortNode);
		PortNode portNode1 = (PortNode) node1;
		assertEquals(0, portNode1.getDesiredDepth().intValue());
		assertEquals(shape.getInputPorts().getByName("string1"), portNode1.getInputProcessorPort());

		IterationStrategyNode node2 = top.get(1);
		assertTrue(node2 instanceof DotProduct);
		DotProduct portNode2 = (DotProduct) node2;
		
		// Note: string3 before string2
		
		IterationStrategyNode node21 = portNode2.get(0);
		assertTrue(node21 instanceof PortNode);
		PortNode portNode21 = (PortNode) node21;
		assertEquals(0, portNode21.getDesiredDepth().intValue());
		assertEquals(shape.getInputPorts().getByName("string3"), portNode21.getInputProcessorPort());

		IterationStrategyNode node22 = portNode2.get(1);
		assertTrue(node22 instanceof PortNode);
		PortNode portNode22 = (PortNode) node22;
		assertEquals(0, portNode22.getDesiredDepth().intValue());
		assertEquals(shape.getInputPorts().getByName("string2"), portNode22.getInputProcessorPort());

	}

}
