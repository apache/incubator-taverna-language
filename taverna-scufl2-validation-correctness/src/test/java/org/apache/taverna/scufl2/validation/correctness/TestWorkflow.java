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
import java.util.TreeSet;

import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NonAbsoluteURIProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestWorkflow {
	
	@Test
	public void testCorrectnessOfMissingFields() {
		DummyWorkflow dw = new DummyWorkflow();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingFields() {
		DummyWorkflow dw = new DummyWorkflow();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("dataLinks")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("controlLinks")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("processors")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("workflowIdentifier")) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testCompletenessOfSpecifiedDataLinks() {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setDataLinks(new TreeSet<DataLink>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("dataLinks")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	
	@Test
	public void testCompletenessOfSpecifiedControlLinks() {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setControlLinks(new TreeSet<ControlLink>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("controlLinks")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	
	@Test
	public void testCompletenessOfSpecifiedProcessors() {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setProcessors(new NamedSet<Processor>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("processors")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	
	@Test
	public void testCompletenessOfSpecifiedWorkflowIdentifier() throws URISyntaxException {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setIdentifier(new URI("http://www.mygrid.org.uk/fred/"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("workflowIdentifier")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	@Test
	public void testNonAbsoluteURI() throws URISyntaxException {
		DummyWorkflow dw = new DummyWorkflow();
		URI workflowIdentifier = new URI("fred/soup");
		dw.setIdentifier(workflowIdentifier);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(dw) && p.getFieldName().equals("workflowIdentifier") && p.getFieldValue().equals(workflowIdentifier)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testFileURI() throws URISyntaxException {
		DummyWorkflow dw = new DummyWorkflow();
		URI workflowIdentifier = new URI("file:///fred/soup");
		dw.setIdentifier(workflowIdentifier);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(dw) && p.getFieldName().equals("workflowIdentifier") && p.getFieldValue().equals(workflowIdentifier)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
}
