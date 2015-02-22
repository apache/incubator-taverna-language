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

import java.util.Set;

import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.apache.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;
import org.junit.Test;



/**
 * @author alanrw
 *
 */
public class TestBlockingControlLink {
	
	@Test
	public void testCorrectnessOfMissingBlock() {
		BlockingControlLink bcl = new BlockingControlLink();
		Processor untilFinished = new Processor();
		bcl.setUntilFinished(untilFinished);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(bcl, false, rcvl);
		assertEquals(0, rcvl.getNullFieldProblems().size());

	}
	
	@Test
	public void testCompletenessOfMissingBlock() {
		BlockingControlLink bcl = new BlockingControlLink();
		Processor untilFinished = new Processor();
		bcl.setUntilFinished(untilFinished);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(bcl, true, rcvl);
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(2, rcvl.getNullFieldProblems().size()); // parent and block
		boolean nullFieldProblem = false;
		for (NullFieldProblem nfp : nullFieldProblems) {
			if (nfp.getBean().equals(bcl) && nfp.getFieldName().equals("block")) {
				nullFieldProblem = true;
			}
		}
		assertTrue(nullFieldProblem);
	}
	
	@Test
	public void testCorrectnessOfMissingUntilFinished() {
		BlockingControlLink bcl = new BlockingControlLink();
		Processor block = new Processor();
		bcl.setBlock(block);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(bcl, false, rcvl);
		assertEquals(0, rcvl.getNullFieldProblems().size());

	}
	
	@Test
	public void testCompletenessOfMissingUntilFinished() {
		BlockingControlLink bcl = new BlockingControlLink();
		Processor block = new Processor();
		bcl.setBlock(block);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(bcl, true, rcvl);
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(2, rcvl.getNullFieldProblems().size()); // parent and untilFinished
		boolean nullFieldProblem = false;
		for (NullFieldProblem nfp : nullFieldProblems) {
			if (nfp.getBean().equals(bcl) && nfp.getFieldName().equals("untilFinished")) {
				nullFieldProblem = true;
			}
		}
		assertTrue(nullFieldProblem);
	}
	
	@Test
	public void testWronglyScopedBlock() {
		Workflow wf = new Workflow();
		BlockingControlLink bcl = new BlockingControlLink();
		bcl.setParent(wf);
		Processor block = new Processor();
		Workflow otherWorkflow = new Workflow();
		block.setParent(otherWorkflow);
		bcl.setBlock(block);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(bcl, false, rcvl);
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertEquals(1, outOfScopeValueProblems.size());
		boolean outOfScopeValueProblem = false;
		for (OutOfScopeValueProblem oosvp : outOfScopeValueProblems) {
			if (oosvp.getBean().equals(bcl) && oosvp.getFieldName().equals("block") && oosvp.getValue().equals(block)) {
				outOfScopeValueProblem = true;
			}
		}
		assertTrue(outOfScopeValueProblem);
	}

	@Test
	public void testValidlyScopedBlock() {
		Workflow wf = new Workflow();
		BlockingControlLink bcl = new BlockingControlLink();
		bcl.setParent(wf);
		Processor block = new Processor();
		block.setParent(wf);
		bcl.setBlock(block);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(bcl, false, rcvl);
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertEquals(0, outOfScopeValueProblems.size());
	}
}
