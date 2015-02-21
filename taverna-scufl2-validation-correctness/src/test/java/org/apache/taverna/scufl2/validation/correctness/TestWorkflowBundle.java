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

import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.apache.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestWorkflowBundle {
	
	@Test
	public void testCorrectnessOfMissingFields() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingFields() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("profiles")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("workflows")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("mainProfile")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("mainWorkflow")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedProfiles() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		dwb.setProfiles(new NamedSet<Profile>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("profiles")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	@Test
	public void testCompletenessOfSpecifiedWorkflows() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		dwb.setWorkflows(new NamedSet<Workflow>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("workflows")) {
				problem = true;
			}
		}
		assertFalse(problem);
	}

	@Test
	public void testOutOfScopeMainProfile() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		Profile orphanProfile = new Profile();
		dwb.setMainProfile(orphanProfile);
		dwb.setProfiles(new NamedSet<Profile>());

		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("mainProfile") && nlp.getValue().equals(orphanProfile)) {
				problem = true;
			}
		}
		assertTrue(problem);

	}

	@Test
	public void testInScopeMainProfile() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		Profile profile = new Profile();
		dwb.setMainProfile(profile);
		NamedSet<Profile> profiles = new NamedSet<Profile>();
		dwb.setProfiles(profiles);
		profiles.add(profile);

		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("mainProfile") && nlp.getValue().equals(profile)) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	@Test
	public void testOutOfScopeMainWorkflow() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		Workflow orphanWorkflow = new Workflow();
		dwb.setMainWorkflow(orphanWorkflow);
		dwb.setWorkflows(new NamedSet<Workflow>());

		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("mainWorkflow") && nlp.getValue().equals(orphanWorkflow)) {
				problem = true;
			}
		}
		assertTrue(problem);

	}

	@Test
	public void testInScopeMainWorkflow() {
		DummyWorkflowBundle dwb = new DummyWorkflowBundle();
		Workflow workflow = new Workflow();
		dwb.setMainWorkflow(workflow);
		NamedSet<Workflow> workflows = new NamedSet<Workflow>();
		dwb.setWorkflows(workflows);
		workflows.add(workflow);

		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dwb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dwb) && nlp.getFieldName().equals("mainWorkflow") && nlp.getValue().equals(workflow)) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

}
