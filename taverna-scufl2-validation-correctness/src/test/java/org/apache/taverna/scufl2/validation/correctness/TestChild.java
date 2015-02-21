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

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.apache.taverna.scufl2.validation.correctness.report.WrongParentProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestChild {



	/**
	 * Test method for {@link org.apache.taverna.scufl2.validation.correctness.CorrectnessVisitor#visitChild(org.apache.taverna.scufl2.api.common.Child)}.
	 */
	@Test
	public void testCorrectnessOfMissingParent() {
		Workflow w = new Workflow();
		w.setName("fred");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check
	}
	
	@Test
	public void testCompletenessOfMissingParent() {
		Workflow w = new Workflow();
		w.setName("fred");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(1, nullFieldProblems.size()); // parent
		boolean parentProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(w) && nlp.getFieldName().equals("parent")) {
				parentProblem = true;
			}
		}
		assertTrue(parentProblem);
	}
	
	@Test
	public void testCompletenessOfSpecifiedParent() {
		Workflow w = new Workflow();
		w.setName("fred");
		WorkflowBundle wb = new WorkflowBundle();
		w.setParent(wb);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size());
	}
	
	@Test
	public void testValidParent() {
		WorkflowBundle parent = new WorkflowBundle();
		Workflow fw = new Workflow();
		fw.setParent(parent);
				
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(parent, false, rcvl);
		Set<WrongParentProblem> wrongParentProblems = rcvl.getWrongParentProblems();
		assertEquals(Collections.EMPTY_SET, wrongParentProblems);
	}
	
	@Test
	public void testInvalidParent() {

		WorkflowBundle parent = new WorkflowBundle();
		DummyWorkflow fw = new DummyWorkflow(parent);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(parent, false, rcvl);
		Set<WrongParentProblem> wrongParentProblems = rcvl.getWrongParentProblems();
		assertEquals(1, wrongParentProblems.size());
		boolean parentProblem = false;
		for (WrongParentProblem p : wrongParentProblems) {
			if (p.getBean().equals(fw)) {
				parentProblem = true;
			}
		}
		assertTrue(parentProblem);
	}



}
