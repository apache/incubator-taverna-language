package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.validation.correctness.report.NegativeValueProblem;
import uk.org.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import uk.org.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;


public class TestDataLink {
	
	@Test
	public void testCorrectnessOfMissingSendsTo() {
		DataLink dataLink = new DataLink();
		InputWorkflowPort iwp = new InputWorkflowPort();
		dataLink.setReceivesFrom(iwp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dataLink, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check

	}	

	@Test
	public void testCompletenessOfMissingSendsTo() {
		DataLink dataLink = new DataLink();
		InputWorkflowPort iwp = new InputWorkflowPort();
		dataLink.setReceivesFrom(iwp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // only done when completeness check
		
		boolean fieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dataLink) && nlp.getFieldName().equals("sendsTo")) {
				fieldProblem = true;
			}
		}
		assertTrue(fieldProblem);
	}
	
	@Test
	public void testCorrectnessOfMissingReceivesFrom() {
		DataLink dataLink = new DataLink();
		OutputWorkflowPort owp = new OutputWorkflowPort();
		dataLink.setSendsTo(owp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(dataLink, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check

	}	

	@Test
	public void testCompletenessOfMissingReceivesFrom() {
		DataLink dataLink = new DataLink();
		OutputWorkflowPort owp = new OutputWorkflowPort();
		dataLink.setSendsTo(owp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // only done when completeness check
		
		boolean fieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(dataLink) && nlp.getFieldName().equals("receivesFrom")) {
				fieldProblem = true;
			}
		}
		assertTrue(fieldProblem);
	}

	@Test
	public void testInScopePorts() {
		Workflow w = new Workflow();
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		InputWorkflowPort iwp = new InputWorkflowPort();
		iwp.setParent(w);
		
		OutputWorkflowPort owp = new OutputWorkflowPort();
		owp.setParent(w);
		
		dataLink.setReceivesFrom(iwp);
		dataLink.setSendsTo(owp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertEquals(Collections.EMPTY_SET, outOfScopeValueProblems);
	}
	
	@Test
	public void testOutOfScopeReceivesFrom() {
		Workflow w = new Workflow();
		
		Workflow otherWorkflow = new Workflow();
		
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		InputWorkflowPort iwp = new InputWorkflowPort();
		iwp.setParent(otherWorkflow);
		
		OutputWorkflowPort owp = new OutputWorkflowPort();
		owp.setParent(w);
		
		dataLink.setReceivesFrom(iwp);
		dataLink.setSendsTo(owp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dataLink) && nlp.getFieldName().equals("receivesFrom") && nlp.getValue().equals(iwp)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testOutOfScopeSendsTo() {
		Workflow w = new Workflow();
		
		Workflow otherWorkflow = new Workflow();
		
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		InputWorkflowPort iwp = new InputWorkflowPort();
		iwp.setParent(w);
		
		OutputWorkflowPort owp = new OutputWorkflowPort();
		owp.setParent(otherWorkflow);
		
		dataLink.setReceivesFrom(iwp);
		dataLink.setSendsTo(owp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dataLink) && nlp.getFieldName().equals("sendsTo") && nlp.getValue().equals(owp)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testInScopeProcessorPorts() {
		Workflow w = new Workflow();
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		Processor p1 = new Processor();
		p1.setParent(w);
		
		InputProcessorPort ipp = new InputProcessorPort();
		ipp.setParent(p1);
		
		Processor p2 = new Processor();
		p2.setParent(w);
		
		OutputProcessorPort opp = new OutputProcessorPort();
		opp.setParent(p2);
		
		dataLink.setReceivesFrom(opp);
		dataLink.setSendsTo(ipp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertEquals(Collections.EMPTY_SET, outOfScopeValueProblems);
	}
	
	@Test
	public void testOutOfScopeReceivesFromProcessorPort() {
		Workflow w = new Workflow();
		Workflow otherWorkflow = new Workflow();
		
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		Processor p1 = new Processor();
		p1.setParent(w);
		
		InputProcessorPort ipp = new InputProcessorPort();
		ipp.setParent(p1);
		
		Processor p2 = new Processor();
		p2.setParent(otherWorkflow);
		
		OutputProcessorPort opp = new OutputProcessorPort();
		opp.setParent(p2);
		
		dataLink.setReceivesFrom(opp);
		dataLink.setSendsTo(ipp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dataLink) && nlp.getFieldName().equals("receivesFrom") && nlp.getValue().equals(opp)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testOutOfScopeSendsToProcessorPort() {
		Workflow w = new Workflow();
		Workflow otherWorkflow = new Workflow();
		
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		Processor p1 = new Processor();
		p1.setParent(otherWorkflow);
		
		InputProcessorPort ipp = new InputProcessorPort();
		ipp.setParent(p1);
		
		Processor p2 = new Processor();
		p2.setParent(w);
		
		OutputProcessorPort opp = new OutputProcessorPort();
		opp.setParent(p2);
		
		dataLink.setReceivesFrom(opp);
		dataLink.setSendsTo(ipp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, true, rcvl);
		
		Set<OutOfScopeValueProblem> outOfScopeValueProblems = rcvl.getOutOfScopeValueProblems();
		assertFalse(outOfScopeValueProblems.isEmpty());
		
		boolean problem = false;
		for (OutOfScopeValueProblem nlp : outOfScopeValueProblems) {
			if (nlp.getBean().equals(dataLink) && nlp.getFieldName().equals("sendsTo") && nlp.getValue().equals(ipp)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}
	
	@Test
	public void testCorrectnessOfMergePositionSpecifiedIncorrectly() {
		Workflow w = new Workflow();
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		InputWorkflowPort iwp = new InputWorkflowPort();
		iwp.setParent(w);
		
		OutputWorkflowPort owp = new OutputWorkflowPort();
		owp.setParent(w);
		
		dataLink.setReceivesFrom(iwp);
		dataLink.setSendsTo(owp);
		
		Integer mergePosition = Integer.valueOf(-3);
		dataLink.setMergePosition(mergePosition);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(1, negativeValueProblems.size());
		if (!negativeValueProblems.isEmpty()) {
			NegativeValueProblem problem = negativeValueProblems.iterator().next();
			assertEquals(problem.getBean(), dataLink);
			assertEquals(problem.getFieldName(), "mergePosition");
			assertEquals(problem.getFieldValue(), mergePosition);
		}
	}

	@Test
	public void testCorrectnessOfMergePositionSpecifiedCorrectly() {
		Workflow w = new Workflow();
		DataLink dataLink = new DataLink();
		dataLink.setParent(w);
		
		InputWorkflowPort iwp = new InputWorkflowPort();
		iwp.setParent(w);
		
		OutputWorkflowPort owp = new OutputWorkflowPort();
		owp.setParent(w);
		
		dataLink.setReceivesFrom(iwp);
		dataLink.setSendsTo(owp);
		
		Integer mergePosition = Integer.valueOf(3);
		dataLink.setMergePosition(mergePosition);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
				
		cv.checkCorrectness(dataLink, false, rcvl);
		
		Set<NegativeValueProblem> negativeValueProblems = rcvl.getNegativeValueProblems();
		assertEquals(Collections.EMPTY_SET, negativeValueProblems);
	}

}
