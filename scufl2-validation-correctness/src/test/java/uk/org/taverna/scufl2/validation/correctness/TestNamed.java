/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;

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
		assertTrue(nullFieldProblems.isEmpty());
		
	}
	
	@Test
	public void testCorrectnessOfMissingName() {
		DummyWorkflow w = new DummyWorkflow();
		w.setName(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertTrue(nullFieldProblems.isEmpty());	
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
	

	
	private static class DummyWorkflow extends Workflow {

		private String name2;

		@Override
		public String getName() {
			return name2;
		}

		@Override
		public void setName(String name) {
			name2 = name;
		}
		
	}

}
