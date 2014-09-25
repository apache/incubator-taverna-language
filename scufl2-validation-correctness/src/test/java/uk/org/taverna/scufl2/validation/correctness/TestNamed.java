/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.validation.correctness.report.NullFieldProblem;

/**
 * @author alanrw
 *
 */
public class TestNamed {
	
	@Test
	public void testValidName() {
		DummyWorkflow w = new DummyWorkflow();
		w.setName("fred");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		
	}
	
	@Test
	public void testCorrectnessOfInvalidName() {
		DummyWorkflow w = new DummyWorkflow();
		w.setName("");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
		
	}
	
	@Test
	public void testCorrectnessOfMissingName() {
		DummyWorkflow w = new DummyWorkflow();
		w.setName(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);	
	}
	
	@Test
	public void testCompletenessOfInvalidName() {
		DummyWorkflow w = new DummyWorkflow();
		w.setName("");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		
		Set<NullFieldProblem> problems = rcvl.getNullFieldProblems();
		assertFalse(problems.isEmpty());
		boolean problemDetected = false;
		for (NullFieldProblem problem : problems) {
			if (problem.getBean().equals(w) && problem.getFieldName().equals("name")) {
				problemDetected = true;
			}
		}
		assertTrue(problemDetected);
	}
	
	@Test
	public void testCompletenessOfMissingName() {
		DummyWorkflow w = new DummyWorkflow();
		w.setName(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		
		Set<NullFieldProblem> problems = rcvl.getNullFieldProblems();
		assertFalse(problems.isEmpty());
		boolean problemDetected = false;
		for (NullFieldProblem problem : problems) {
			if (problem.getBean().equals(w) && problem.getFieldName().equals("name")) {
				problemDetected = true;
			}
		}
		assertTrue(problemDetected);
	}

}
