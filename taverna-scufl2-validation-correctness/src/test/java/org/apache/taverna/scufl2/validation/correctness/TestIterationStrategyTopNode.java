/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness;
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


import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.PortMentionedTwiceProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestIterationStrategyTopNode {
	
	@Test
	public void testValidIterationStrategyTopNode() {
		Processor p = new Processor();
		IterationStrategyStack iss = new IterationStrategyStack();
		iss.setParent(p);
		
		InputProcessorPort p1 = new InputProcessorPort();
		p1.setParent(p);
		InputProcessorPort p2 = new InputProcessorPort();
		p2.setParent(p);
		InputProcessorPort p3 = new InputProcessorPort();
		p3.setParent(p);	
		
		// Do a crossproduct with a portnode and a dotproduct
		CrossProduct cp = new CrossProduct();
		PortNode pNode1 = new PortNode();
		pNode1.setInputProcessorPort(p1);
		cp.add(pNode1);
		iss.add(cp);
		
		DotProduct dp = new DotProduct();
		PortNode pNode2 = new PortNode();
		pNode2.setInputProcessorPort(p2);
		PortNode pNode3 = new PortNode();
		pNode3.setInputProcessorPort(p3);
		dp.add(pNode2);
		dp.add(pNode3);
		cp.add(dp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, false, rcvl);
		
		Set<PortMentionedTwiceProblem> problems = rcvl.getPortMentionedTwiceProblems();
		assertEquals(Collections.EMPTY_SET, problems);

	}

	@Test
	public void testInvalidAtTopIterationStrategyTopNode() {
		Processor p = new Processor();
		IterationStrategyStack iss = new IterationStrategyStack();
		iss.setParent(p);
		
		InputProcessorPort p1 = new InputProcessorPort();
		p1.setParent(p);
		InputProcessorPort p2 = new InputProcessorPort();
		p2.setParent(p);
		InputProcessorPort p3 = new InputProcessorPort();
		p3.setParent(p);	
		
		// Do a crossproduct with a portnode and a dotproduct
		CrossProduct cp = new CrossProduct();
		PortNode pNode1 = new PortNode();
		pNode1.setInputProcessorPort(p1);
		cp.add(pNode1);
		PortNode duplicateNode = new PortNode();
		duplicateNode.setInputProcessorPort(p1);
		cp.add(duplicateNode);
		iss.add(cp);
		
		DotProduct dp = new DotProduct();
		PortNode pNode2 = new PortNode();
		pNode2.setInputProcessorPort(p2);
		PortNode pNode3 = new PortNode();
		pNode3.setInputProcessorPort(p3);
		dp.add(pNode2);
		dp.add(pNode3);
		cp.add(dp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, false, rcvl);
		
		Set<PortMentionedTwiceProblem> problems = rcvl.getPortMentionedTwiceProblems();
		assertFalse(problems.isEmpty());
		boolean problemDetected = false;
		for (PortMentionedTwiceProblem problem : problems) {
			if (problem.getBean().equals(pNode1) && problem.getDuplicateNode().equals(duplicateNode)) {
				problemDetected = true;
			}
		}
		assertTrue(problemDetected);

	}

	@Test
	public void testInvalidDeepInIterationStrategyTopNode() {
		Processor p = new Processor();
		IterationStrategyStack iss = new IterationStrategyStack();
		iss.setParent(p);
		
		InputProcessorPort p1 = new InputProcessorPort();
		p1.setParent(p);
		InputProcessorPort p2 = new InputProcessorPort();
		p2.setParent(p);
		InputProcessorPort p3 = new InputProcessorPort();
		p3.setParent(p);	
		
		// Do a crossproduct with a portnode and a dotproduct
		CrossProduct cp = new CrossProduct();
		PortNode pNode1 = new PortNode();
		pNode1.setInputProcessorPort(p1);
		cp.add(pNode1);
		iss.add(cp);
		
		DotProduct dp = new DotProduct();
		PortNode pNode2 = new PortNode();
		pNode2.setInputProcessorPort(p2);
		PortNode pNode3 = new PortNode();
		pNode3.setInputProcessorPort(p3);
		dp.add(pNode2);
		dp.add(pNode3);
		PortNode duplicateNode = new PortNode();
		duplicateNode.setInputProcessorPort(p1);
		cp.add(duplicateNode);
		cp.add(dp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, false, rcvl);
		
		Set<PortMentionedTwiceProblem> problems = rcvl.getPortMentionedTwiceProblems();
		assertFalse(problems.isEmpty());
		boolean problemDetected = false;
		for (PortMentionedTwiceProblem problem : problems) {
			if (problem.getBean().equals(pNode1) && problem.getDuplicateNode().equals(duplicateNode)) {
				problemDetected = true;
			}
		}
		assertTrue(problemDetected);

	}

}
