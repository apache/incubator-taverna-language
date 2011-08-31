package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NegativeValueProblem;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;


public class TestAbstractDepthPort {
	
	@Test
	public void testCorrectnessOfDepthSpecifiedIncorrectly() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(new Integer(-3));
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), iap);
			assertEquals(problem.getFieldName(), "depth");
			assertEquals(problem.getFieldValue(), Integer.valueOf("-3"));
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}

	@Test
	public void testCompletenessOfDepthSpecifiedIncorrectly() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(new Integer(-3));
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), iap);
			assertEquals(problem.getFieldName(), "depth");
			assertEquals(problem.getFieldValue(), Integer.valueOf("-3"));
		}
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // parent
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(iap) && nlp.getFieldName().equals("depth")) {
				depthFieldProblem = true;
			}
		}
		assertFalse(depthFieldProblem);
	}

	@Test
	public void testCorrectnessOfMissingDepth() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(null);
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems); // only done when completeness check
	}
	
	@Test
	public void testCompletenessOfMissingDepth() {
		InputActivityPort iap = new InputActivityPort();
		iap.setDepth(null);
		iap.setName("fred");
		iap.setParent(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(iap, true, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(0, negativeValueProblems.size());
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // depth and parent
		boolean depthFieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(iap) && nlp.getFieldName().equals("depth")) {
				depthFieldProblem = true;
			}
		}
		assertTrue(depthFieldProblem);

	}
	
}
