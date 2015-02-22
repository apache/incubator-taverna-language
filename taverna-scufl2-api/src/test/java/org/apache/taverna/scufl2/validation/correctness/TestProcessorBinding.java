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

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
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
public class TestProcessorBinding {
	
	@Test
	public void testCorrectnessOfMissingBoundProcessor() {
		ProcessorBinding pb = new ProcessorBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingBoundProcessor() {
		ProcessorBinding pb = new ProcessorBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundProcessor")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedBoundProcessor() {
		ProcessorBinding pb = new ProcessorBinding();
		pb.setBoundProcessor(new Processor());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundProcessor")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

	@Test
	public void testCorrectnessOfMissingBoundActivity() {
		ProcessorBinding pb = new ProcessorBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingBoundActivity() {
		ProcessorBinding pb = new ProcessorBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundActivity")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedBoundActivity() {
		ProcessorBinding pb = new ProcessorBinding();
		pb.setBoundActivity(new Activity());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundActivity")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

	@Test
	public void testCorrectnessOfMissingInputPortBindings() {
		ProcessorBinding pb = new ProcessorBinding();
		pb.setInputPortBindings(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingInputPortBindings() {
		ProcessorBinding pb = new ProcessorBinding();
		pb.setInputPortBindings(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("inputPortBindings")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedInputPortBindings() {
		ProcessorBinding pb = new ProcessorBinding();
		// No need to specify as default constructor does it
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("inputPortBindings")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

	@Test
	public void testCorrectnessOfMissingOutputPortBindings() {
		ProcessorBinding pb = new ProcessorBinding();
		pb.setOutputPortBindings(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingOutputPortBindings() {
		ProcessorBinding pb = new ProcessorBinding();
		pb.setOutputPortBindings(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("outputPortBindings")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedOutputPortBindings() {
		ProcessorBinding pb = new ProcessorBinding();
		// No need to specify as default constructor does it
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("outputPortBindings")) {
				problem = true;
			}
		}
		assertFalse(problem);
	}
	
	@Test
	public void testCompletenessOfMissingActivityPosition() {
		// should be OK
		ProcessorBinding pb = new ProcessorBinding();
		pb.setActivityPosition(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("activityPosition")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}
	
	@Test
	public void testCorrectnessOfInvalidActivityPosition() {
		ProcessorBinding pb = new ProcessorBinding();
		Integer activityPosition = Integer.valueOf(-3);
		pb.setActivityPosition(activityPosition);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		boolean problem = false;
		for (NegativeValueProblem nlp : negativeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("activityPosition") && nlp.getFieldValue().equals(activityPosition)) {
				problem = true;
			}
		}
		assertTrue(problem);	
	}
	
	@Test
	public void testCorrectnessOfValidActivityPosition() {
		ProcessorBinding pb = new ProcessorBinding();
		Integer activityPosition = Integer.valueOf(3);
		pb.setActivityPosition(activityPosition);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
	}
	
	@Test
	public void testCorrectnessOfOutOfScopeBoundProcessor1() {
		WorkflowBundle wb = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(wb);
		ProcessorBinding pb = new ProcessorBinding();
		Processor orphanProcessor = new Processor();
		pb.setBoundProcessor(orphanProcessor);
		pb.setParent(profile);
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundProcessor") && nlp.getValue().equals(orphanProcessor)) {
				problem = true;
			}
		}
		assertTrue(problem);	
	}
	
	@Test
	public void testCorrectnessOfOutOfScopeBoundProcessor2() {
		WorkflowBundle wb = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(wb);
		ProcessorBinding pb = new ProcessorBinding();
		
		Workflow w = new Workflow();
		Processor processor = new Processor();
		processor.setParent(w);
		
		pb.setBoundProcessor(processor);
		pb.setParent(profile);
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundProcessor") && nlp.getValue().equals(processor)) {
				problem = true;
			}
		}
		assertTrue(problem);	
	}
	
	@Test
	public void testCorrectnessOfInScopeBoundProcessor() {
		WorkflowBundle wb = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(wb);
		ProcessorBinding pb = new ProcessorBinding();
		
		Workflow w = new Workflow();
		Processor processor = new Processor();
		processor.setParent(w);
		w.setParent(wb);
		
		pb.setBoundProcessor(processor);
		pb.setParent(profile);
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
//		assertFalse(outOfScopeValueProblems.isEmpty());
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundProcessor") && nlp.getValue().equals(processor)) {
				problem = true;
			}
		}
		assertFalse(problem);	
	}
	
	@Test
	public void testCorrectnessOfOutOfScopeBoundActivity() {
		WorkflowBundle wb = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(wb);
		ProcessorBinding pb = new ProcessorBinding();
		Activity orphanActivity = new Activity();
		pb.setBoundActivity(orphanActivity);
		pb.setParent(profile);
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundActivity") && nlp.getValue().equals(orphanActivity)) {
				problem = true;
			}
		}
		assertTrue(problem);	
	}
	
	@Test
	public void testCorrectnessOfInScopeBoundActivity1() {
		// Test when in same profile
		WorkflowBundle wb = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(wb);
		ProcessorBinding pb = new ProcessorBinding();
		Activity activity = new Activity();
		activity.setParent(profile); 
		pb.setBoundActivity(activity);
		pb.setParent(profile);
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundActivity") && nlp.getValue().equals(activity)) {
				problem = true;
			}
		}
		assertFalse(problem);	
	}
	
	@Test
	public void testCorrectnessOfInScopeBoundActivity2() {
		// Test when in same profile
		WorkflowBundle wb = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(wb);
		ProcessorBinding pb = new ProcessorBinding();
		
		Profile otherProfile = new Profile();
		otherProfile.setParent(wb);
		Activity activity = new Activity();
		activity.setParent(otherProfile); 
		pb.setBoundActivity(activity);
		pb.setParent(profile);
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pb) && nlp.getFieldName().equals("boundActivity") && nlp.getValue().equals(activity)) {
				problem = true;
			}
		}
		assertFalse(problem);	
	}
}
