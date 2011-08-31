/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;

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
		wb.setGlobalBaseURI(new URI("fred/soup"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<WorkflowBean> problems = rcvl.getNonAbsoluteURIs();
		assertTrue(problems.contains(wb));
		
	}
	
	@Test
	public void testFileURI() throws URISyntaxException {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setGlobalBaseURI(new URI("file:///fred/soup"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(wb, false, rcvl);
		
		Set<WorkflowBean> problems = rcvl.getNonAbsoluteURIs();
		assertTrue(problems.contains(wb));
		
	}
}
