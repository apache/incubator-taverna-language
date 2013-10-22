/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;

/**
 * @author alanrw
 *
 */
public class TestProcessor {
	
	@Test
	public void testCorrectnessOfMissingIterationStrategyStack() {
		Processor p = new Processor();
		p.setIterationStrategyStack(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}
	
	@Test
	public void testCompletenessOfMissingIterationStrategyStack() {
		Processor p = new Processor();
		p.setIterationStrategyStack(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(p) && nlp.getFieldName().equals("iterationStrategyStack")) {
				problem = true;
			}
		}
		assertTrue(problem);

	}
	
	@Test
	public void testCompletenessOfSpecifiedIterationStrategyStack() {
		Processor p = new Processor();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(p) && nlp.getFieldName().equals("iterationStrategyStack")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedDispatchStack() {
		Processor p = new Processor();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(p) && nlp.getFieldName().equals("dispatchStack")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

}
