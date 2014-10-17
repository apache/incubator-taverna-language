/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.validation.correctness.report.NonAbsoluteURIProblem;
import uk.org.taverna.scufl2.validation.correctness.report.NullFieldProblem;

/**
 * @author alanrw
 *
 */
public class TestRoot {
	
	@Test
	public void testCorrectnessOfMissingGlobalBaseURI() {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingGlobalBaseURI() {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(wb) && nlp.getFieldName().equals("globalBaseURI")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfGlobalBaseURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(new URI("http://www.taverna.org.uk"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(wb) && nlp.getFieldName().equals("globalBaseURI")) {
				problem = true;
			}
		}
		assertFalse(problem);
	}
	
	@Test
	public void testNonAbsoluteURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		URI globalBaseURI = new URI("fred/soup");
		wb.setGlobalBaseURI(globalBaseURI);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(wb) && p.getFieldName().equals("globalBaseURI") && p.getFieldValue().equals(globalBaseURI)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testFileURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		URI globalBaseURI = new URI("file:///fred/soup");
		wb.setGlobalBaseURI(globalBaseURI);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(wb) && p.getFieldName().equals("globalBaseURI") && p.getFieldValue().equals(globalBaseURI)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
}
