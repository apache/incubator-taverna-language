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

import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NegativeValueProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.junit.Test;



public class TestAbstractDepthPort {
	
	@Test
	public void testCorrectnessOfDepthSpecifiedIncorrectly() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(new Integer(-3));
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), iap);
			assertEquals(problem.getFieldName(), "depth");
			assertEquals(problem.getFieldValue(), Integer.valueOf("-3"));
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}

	@Test
	public void testCompletenessOfDepthSpecifiedIncorrectly() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(new Integer(-3));
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), iap);
			assertEquals(problem.getFieldName(), "depth");
			assertEquals(problem.getFieldValue(), Integer.valueOf("-3"));
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // parent
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(iap) && nlp.getFieldName().equals("depth")) {
				depthFieldProblem = true;
			}
		}
		assertFalse(depthFieldProblem);
	}

	@Test
	public void testCorrectnessOfMissingDepth() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(null);
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}
	
	@Test
	public void testCompletenessOfMissingDepth() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(null);
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(0, negativeValueProblems.size());
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // depth and parent
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(iap) && nlp.getFieldName().equals("depth")) {
				depthFieldProblem = true;
			}
		}
		assertTrue(depthFieldProblem);

	}
	
}
