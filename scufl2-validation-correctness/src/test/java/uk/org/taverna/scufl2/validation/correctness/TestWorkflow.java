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
import java.util.TreeSet;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;

/**
 * @author alanrw
 *
 */
public class TestWorkflow {
	
	@Test
	public void testCorrectnessOfMissingFields() {
		DummyWorkflow dw = new DummyWorkflow();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingFields() {
		DummyWorkflow dw = new DummyWorkflow();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("dataLinks")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("controlLinks")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("processors")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
		problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("workflowIdentifier")) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testCompletenessOfSpecifiedDataLinks() {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setDataLinks(new TreeSet<DataLink>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("dataLinks")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	
	@Test
	public void testCompletenessOfSpecifiedControlLinks() {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setControlLinks(new TreeSet<ControlLink>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("controlLinks")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	
	@Test
	public void testCompletenessOfSpecifiedProcessors() {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setProcessors(new NamedSet<Processor>());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("processors")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	
	@Test
	public void testCompletenessOfSpecifiedWorkflowIdentifier() throws URISyntaxException {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setWorkflowIdentifier(new URI("http://www.mygrid.org.uk/fred/"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dw) && nlp.getFieldName().equals("workflowIdentifier")) {
				problem = true;
			}
		}
		assertFalse(problem);

	}

	@Test
	public void testNonAbsoluteURI() throws URISyntaxException {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setWorkflowIdentifier(new URI("fred/soup"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, false, rcvl);
		
		Set<WorkflowBean> problems = rcvl.getNonAbsoluteURIs();
		assertTrue(problems.contains(dw));
		
	}
	
	@Test
	public void testFileURI() throws URISyntaxException {
		DummyWorkflow dw = new DummyWorkflow();
		dw.setWorkflowIdentifier(new URI("file:///fred/soup"));
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dw, false, rcvl);
		
		Set<WorkflowBean> problems = rcvl.getNonAbsoluteURIs();
		assertTrue(problems.contains(dw));
		
	}
}
