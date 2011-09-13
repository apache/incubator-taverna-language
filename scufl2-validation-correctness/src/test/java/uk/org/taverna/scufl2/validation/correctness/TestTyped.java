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

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NonAbsoluteURIProblem;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;

/**
 * @author alanrw
 *
 */
public class TestTyped {
	
	@Test
	public void testCorrectnessOfMissingConfigurableType() {
		Activity a = new Activity();
		a.setConfigurableType(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(a, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingConfigurableType() {
		Activity a = new Activity();
		a.setConfigurableType(null);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(a, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(a) && nlp.getFieldName().equals("configurableType")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfConfigurableType() throws URISyntaxException {
		Activity a = new Activity();
		a.setConfigurableType(new URI("http://www.taverna.org.uk"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(a, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(a) && nlp.getFieldName().equals("configurableType")) {
				problem = true;
			}
		}
		assertFalse(problem);
	}
	
	@Test
	public void testNonAbsoluteURI() throws URISyntaxException {
		Activity a = new Activity();
		URI type = new URI("fred/soup");
		a.setConfigurableType(type);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(a, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(a) && p.getFieldName().equals("configurableType") && p.getFieldValue().equals(type)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testFileURI() throws URISyntaxException {
		Activity a = new Activity();
		URI type = new URI("file:///fred/soup");
		a.setConfigurableType(type);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(a, false, rcvl);
		
		Set<NonAbsoluteURIProblem> problems = rcvl.getNonAbsoluteURIProblems();
		boolean problem = false;
		for (NonAbsoluteURIProblem p : problems) {
			if (p.getBean().equals(a) && p.getFieldName().equals("configurableType") && p.getFieldValue().equals(type)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
}
