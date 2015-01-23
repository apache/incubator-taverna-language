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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NonAbsoluteURIProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestRoot {
	
	@Test
	public void testCorrectnessOfMissingGlobalBaseURI() {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingGlobalBaseURI() {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(wb) && nlp.getFieldName().equals("globalBaseURI")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfGlobalBaseURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(new URI("http://www.taverna.org.uk"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(wb) && nlp.getFieldName().equals("globalBaseURI")) {
				problem = true;
			}
		}
		assertFalse(problem);
	}
	
	@Test
	public void testNonAbsoluteURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		URI globalBaseURI = new URI("fred/soup");
		wb.setGlobalBaseURI(globalBaseURI);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(wb) && p.getFieldName().equals("globalBaseURI") && p.getFieldValue().equals(globalBaseURI)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testFileURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		URI globalBaseURI = new URI("file:///fred/soup");
		wb.setGlobalBaseURI(globalBaseURI);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(wb) && p.getFieldName().equals("globalBaseURI") && p.getFieldValue().equals(globalBaseURI)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
}
