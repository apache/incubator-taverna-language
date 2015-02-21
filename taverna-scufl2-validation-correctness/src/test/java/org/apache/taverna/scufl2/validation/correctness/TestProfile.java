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
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NegativeValueProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.junit.Test;


/**
 * @author alanrw
 *
 */
public class TestProfile {
	
	@Test
	public void testCompletenessOfMissingProfilePosition() {
		// should be OK
		Profile profile = new Profile();
		profile.setProfilePosition(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("profilePosition")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}
	
	@Test
	public void testCorrectnessOfInvalidProfilePosition() {
		Profile profile = new Profile();
		Integer profilePosition = Integer.valueOf(-3);
		profile.setProfilePosition(profilePosition);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		boolean problem = false;
		for (NegativeValueProblem nlp : negativeValueProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("profilePosition") && nlp.getFieldValue().equals(profilePosition)) {
				problem = true;
			}
		}
		assertTrue(problem);	
	}
	
	@Test
	public void testCorrectnessOfValidProfilePosition() {
		Profile profile = new Profile();
		Integer profilePosition = Integer.valueOf(3);
		profile.setProfilePosition(profilePosition);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
	}
	
	@Test
	public void testCorrectnessOfMissingFieldss() {
		DummyProfile profile = new DummyProfile();
		// The fields will default to null
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingFields() {
		DummyProfile profile = new DummyProfile();
		// The fields will default to null
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("processorBindings")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("configurations")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("activities")) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testCompletenessOfSpecifiedProcessorBindings() {
		DummyProfile profile = new DummyProfile();
		// The fields will default to null
		profile.setProcessorBindings(new NamedSet<ProcessorBinding>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("processorBindings")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

	@Test
	public void testCompletenessOfSpecifiedConfigurations() {
		DummyProfile profile = new DummyProfile();
		// The fields will default to null
		profile.setConfigurations(new NamedSet<Configuration>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("configurations")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}


	@Test
	public void testCompletenessOfSpecifiedActivities() {
		DummyProfile profile = new DummyProfile();
		// The fields will default to null
		profile.setActivities(new NamedSet<Activity>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(profile, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(profile) && nlp.getFieldName().equals("activities")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

}
