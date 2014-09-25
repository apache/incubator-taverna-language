package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import uk.org.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;

public class TestProcessorInputPortBinding {
	
	@Test
	public void testCorrectnessOfMissingBoundProcessorPort() {
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingBoundProcessorPort() {
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundProcessorPort")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedBoundProcessorPort() {
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setBoundProcessorPort(new InputProcessorPort());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundProcessorPort")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

	@Test
	public void testCorrectnessOfMissingBoundActivityPort() {
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingBoundActivityPort() {
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty());
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundActivityPort")) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCompletenessOfSpecifiedBoundActivityPort() {
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setBoundActivityPort(new InputActivityPort());
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		boolean problem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundActivityPort")) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}
	
	@Test
	public void testCorrectnessOfOutOfScopeProcessorPort1() {
		ProcessorBinding pb = new ProcessorBinding();
		Processor processor = new Processor();
		pb.setBoundProcessor(processor);
		
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setParent(pb);
		
		InputProcessorPort orphanPort = new InputProcessorPort();
		pipb.setBoundProcessorPort(orphanPort);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundProcessorPort") && nlp.getValue().equals(orphanPort)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCorrectnessOfOutOfScopeProcessorPort2() {
		ProcessorBinding pb = new ProcessorBinding();
		Processor processor = new Processor();
		pb.setBoundProcessor(processor);
		
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setParent(pb);
		
		Processor otherProcessor = new Processor();
		InputProcessorPort elsewherePort = new InputProcessorPort();
		elsewherePort.setParent(otherProcessor);
		
		pipb.setBoundProcessorPort(elsewherePort);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundProcessorPort") && nlp.getValue().equals(elsewherePort)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCorrectnessOfInScopeProcessorPort() {
		ProcessorBinding pb = new ProcessorBinding();
		Processor processor = new Processor();
		pb.setBoundProcessor(processor);
		
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setParent(pb);
		
		InputProcessorPort port = new InputProcessorPort();
		port.setParent(processor);
		
		pipb.setBoundProcessorPort(port);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundProcessorPort") && nlp.getValue().equals(port)) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

	@Test
	public void testCorrectnessOfOutOfScopeActivityPort1() {
		ProcessorBinding pb = new ProcessorBinding();
		Processor processor = new Processor();
		pb.setBoundProcessor(processor);
		
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setParent(pb);
		
		InputActivityPort orphanPort = new InputActivityPort();
		pipb.setBoundActivityPort(orphanPort);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundActivityPort") && nlp.getValue().equals(orphanPort)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCorrectnessOfOutOfScopeActivityPort2() {
		ProcessorBinding pb = new ProcessorBinding();
		Activity activity = new Activity();
		pb.setBoundActivity(activity);
		
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setParent(pb);
		
		Activity otherActivity = new Activity();
		InputActivityPort elsewherePort = new InputActivityPort();
		elsewherePort.setParent(otherActivity);
		
		pipb.setBoundActivityPort(elsewherePort);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundActivityPort") && nlp.getValue().equals(elsewherePort)) {
				problem = true;
			}
		}
		assertTrue(problem);
		
	}
	
	@Test
	public void testCorrectnessOfInScopeActivityPort() {
		ProcessorBinding pb = new ProcessorBinding();
		Activity activity = new Activity();
		pb.setBoundActivity(activity);
		
		ProcessorInputPortBinding pipb = new ProcessorInputPortBinding();
		pipb.setParent(pb);
		
		InputActivityPort port = new InputActivityPort();
		port.setParent(activity);
		
		pipb.setBoundActivityPort(port);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(pipb) && nlp.getFieldName().equals("boundActivityPort") && nlp.getValue().equals(port)) {
				problem = true;
			}
		}
		assertFalse(problem);
		
	}

}
