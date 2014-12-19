package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import uk.org.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;

public class TestProcessorOutputPortBinding {
	
	@Test
	public void testCorrectnessOfMissingBoundProcessorPort() {
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingBoundProcessorPort() {
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		
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
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setBoundProcessorPort(new OutputProcessorPort());
		
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
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(pipb, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(Collections.EMPTY_SET, nullFieldProblems);
	}
	
	@Test
	public void testCompletenessOfMissingBoundActivityPort() {
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		
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
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setBoundActivityPort(new OutputActivityPort());
		
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
		
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setParent(pb);
		
		OutputProcessorPort orphanPort = new OutputProcessorPort();
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
		
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setParent(pb);
		
		Processor otherProcessor = new Processor();
		OutputProcessorPort elsewherePort = new OutputProcessorPort();
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
		
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setParent(pb);
		
		OutputProcessorPort port = new OutputProcessorPort();
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
		
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setParent(pb);
		
		OutputActivityPort orphanPort = new OutputActivityPort();
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
		
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setParent(pb);
		
		Activity otherActivity = new Activity();
		OutputActivityPort elsewherePort = new OutputActivityPort();
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
		
		ProcessorOutputPortBinding pipb = new ProcessorOutputPortBinding();
		pipb.setParent(pb);
		
		OutputActivityPort port = new OutputActivityPort();
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
