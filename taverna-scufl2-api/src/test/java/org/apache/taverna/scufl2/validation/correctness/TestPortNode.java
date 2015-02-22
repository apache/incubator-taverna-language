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
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NegativeValueProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.apache.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestPortNode {
	
	@Test
	public void testCorrectnessOfDesiredDepthSpecifiedIncorrectly() {
		PortNode pn = new PortNode();
		Integer desiredDepth = new Integer(-3);
		pn.setDesiredDepth(desiredDepth);
		InputProcessorPort ipp = new InputProcessorPort();
		pn.setInputProcessorPort(ipp);

		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), pn);
			assertEquals(problem.getFieldName(), "desiredDepth");
			assertEquals(problem.getFieldValue(), desiredDepth);
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}

	@Test
	public void testCompletenessOfDepthSpecifiedIncorrectly() {
		PortNode pn = new PortNode();
		Integer desiredDepth = new Integer(-3);
		pn.setDesiredDepth(desiredDepth);
		InputProcessorPort ipp = new InputProcessorPort();
		pn.setInputProcessorPort(ipp);

		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), pn);
			assertEquals(problem.getFieldName(), "desiredDepth");
			assertEquals(problem.getFieldValue(), desiredDepth);
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // parent
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pn) && nlp.getFieldName().equals("desiredDepth")) {
				depthFieldProblem = true;
			}
		}
		assertFalse(depthFieldProblem);
	}

	@Test
	public void testCorrectnessOfMissingDepth() {
		PortNode pn = new PortNode();
		InputProcessorPort ipp = new InputProcessorPort();
		pn.setInputProcessorPort(ipp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}
	
	@Test
	public void testCompletenessOfMissingDepth() {
		PortNode pn = new PortNode();
		InputProcessorPort ipp = new InputProcessorPort();
		pn.setInputProcessorPort(ipp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pn) && nlp.getFieldName().equals("desiredDepth")) {
				depthFieldProblem = true;
			}
		}
		assertTrue(depthFieldProblem);

	}

	@Test
	public void testCorrectnessOfMissingInputProcessorPort() {
		PortNode pn = new PortNode();
		Integer desiredDepth = new Integer(-3);
		pn.setDesiredDepth(desiredDepth);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertTrue(nullFieldProblems.isEmpty());
	}
	
	@Test
	public void testCompletenessOfMissingInputProcessorPort() {
		PortNode pn = new PortNode();
		Integer desiredDepth = new Integer(-3);
		pn.setDesiredDepth(desiredDepth);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pn) && nlp.getFieldName().equals("inputProcessorPort")) {
				depthFieldProblem = true;
			}
		}
		assertTrue(depthFieldProblem);

	}
	
	@Test
	public void testOutOfScopeInputProcessorPort() {
		Processor p = new Processor();
		InputProcessorPort ipp = new InputProcessorPort();
//		ipp.setParent(p);
		IterationStrategyStack iss = new IterationStrategyStack();
		p.setIterationStrategyStack(iss);
		CrossProduct cp = new CrossProduct();
		iss.add(cp);
		cp.setParent(iss);
		PortNode pn = new PortNode();
		pn.setInputProcessorPort(ipp);
		cp.add(pn);
		pn.setParent(cp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pn) && nlp.getFieldName().equals("inputProcessorPort") && nlp.getValue().equals(ipp)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}

	@Test
	public void testInScopeInputProcessorPort() {
		Processor p = new Processor();
		InputProcessorPort ipp = new InputProcessorPort();
		ipp.setParent(p);
		IterationStrategyStack iss = new IterationStrategyStack();
		p.setIterationStrategyStack(iss);
		CrossProduct cp = new CrossProduct();
		iss.add(cp);
		cp.setParent(iss);
		PortNode pn = new PortNode();
		pn.setInputProcessorPort(ipp);
		cp.add(pn);
		pn.setParent(cp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pn, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertEquals(Collections.EMPTY_SET, outOfScopeValueProblems);
		
	}

}
