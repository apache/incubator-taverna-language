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

import java.util.Set;

import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.IncompatibleGranularDepthProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NegativeValueProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.junit.Test;



public class TestAbstractGranularDepthPort {
	
	@Test
	public void testCorrectnessOfGranularDepthSpecifiedIncorrectly() {
		OutputActivityPort oap = new OutputActivityPort();
		oap.setDepth(new Integer(3));
		oap.setGranularDepth(new Integer(-2));
		oap.setName("fred");
		oap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(oap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), oap);
			assertEquals(problem.getFieldName(), "granularDepth");
			assertEquals(problem.getFieldValue(), Integer.valueOf("-2"));
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check
		
		assertEquals(0, rcvl.getIncompatibleGranularDepthProblems().size());
	}

	@Test
	public void testCompletenessOfGranularDepthSpecifiedIncorrectly() {
		OutputActivityPort oap = new OutputActivityPort();
		oap.setDepth(new Integer(3));
		oap.setGranularDepth(new Integer(-2));
		oap.setName("fred");
		oap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(oap, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), oap);
			assertEquals(problem.getFieldName(), "granularDepth");
			assertEquals(problem.getFieldValue(), Integer.valueOf("-2"));
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(1, nullFieldProblems.size()); // parent
		boolean granularDepthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(oap) && nlp.getFieldName().equals("granularDepth")) {
				granularDepthFieldProblem = true;
			}
		}
		assertFalse(granularDepthFieldProblem);
		assertEquals(0, rcvl.getIncompatibleGranularDepthProblems().size());
	}

	@Test
	public void testCorrectnessOfMissingGranularDepth() {
		OutputActivityPort oap = new OutputActivityPort();
		oap.setDepth(new Integer(3));
		oap.setGranularDepth(null);
		oap.setName("fred");
		oap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(oap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(0, negativeValueProblems.size());
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check
		assertEquals(0, rcvl.getIncompatibleGranularDepthProblems().size());
	}
	
	@Test
	public void testCompletenessOfMissingGranularDepth() {
		OutputActivityPort oap = new OutputActivityPort();
		oap.setDepth(new Integer(3));
		oap.setGranularDepth(null);
		oap.setName("fred");
		oap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(oap, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(0, negativeValueProblems.size());
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(2, nullFieldProblems.size()); // granular depth and parent
		boolean granularDepthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(oap) && nlp.getFieldName().equals("granularDepth")) {
				granularDepthFieldProblem = true;
			}
		}
		assertTrue(granularDepthFieldProblem);
		assertEquals(0, rcvl.getIncompatibleGranularDepthProblems().size());

	}
	
	@Test
	public void testIncompatibleGranularDepth() {
		OutputActivityPort oap = new OutputActivityPort();
		oap.setDepth(new Integer(0));
		oap.setGranularDepth(new Integer(1));
		oap.setName("fred");
		oap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(oap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(0, negativeValueProblems.size());
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check
		boolean granularDepthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(oap) && nlp.getFieldName().equals("granularDepth")) {
				granularDepthFieldProblem = true;
			}
		}
		assertFalse(granularDepthFieldProblem);
		
		Set<IncompatibleGranularDepthProblem> incompatibleGranularDepthProblems = rcvl.getIncompatibleGranularDepthProblems();
		assertEquals(1, incompatibleGranularDepthProblems.size());
		boolean incompatibleGranularDepthProblem = false;
		for (IncompatibleGranularDepthProblem nlp : incompatibleGranularDepthProblems) {
			if (nlp.getBean().equals(oap) && nlp.getDepth().equals(Integer.valueOf(0)) && nlp.getGranularDepth().equals(Integer.valueOf(1))) {
				incompatibleGranularDepthProblem = true;
			}
		}
		assertTrue(incompatibleGranularDepthProblem);
	}	
	
}
