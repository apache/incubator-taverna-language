/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.validation.correctness.report.PortMissingFromIterationStrategyStackProblem;

/**
 * @author alanrw
 *
 */
public class TestIterationStrategyStack {
	
	@Test
	public void testValidIterationStrategyStack() {
		Processor p = new Processor();
		IterationStrategyStack iss = new IterationStrategyStack();
		iss.setParent(p);
		
		InputProcessorPort p1 = new InputProcessorPort();
		p1.setParent(p);
		InputProcessorPort p2 = new InputProcessorPort();
		p2.setParent(p);
		InputProcessorPort p3 = new InputProcessorPort();
		p3.setParent(p);	
		
		CrossProduct cp = new CrossProduct();
		PortNode pNode1 = new PortNode();
		pNode1.setInputProcessorPort(p1);
		cp.add(pNode1);
		iss.add(cp);
		
		DotProduct dp = new DotProduct();
		PortNode pNode2 = new PortNode();
		pNode2.setInputProcessorPort(p2);
		PortNode pNode3 = new PortNode();
		pNode3.setInputProcessorPort(p3);
		dp.add(pNode2);
		dp.add(pNode3);
		iss.add(dp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, true, rcvl);
		
		Set<PortMissingFromIterationStrategyStackProblem> problems = rcvl.getPortMissingFromIterationStrategyStackProblems();
		assertEquals(Collections.EMPTY_SET, problems);

	}

	@Test
	public void testInvalidIterationStrategyStack() {
		Processor p = new Processor();
		IterationStrategyStack iss = new IterationStrategyStack();
		iss.setParent(p);
		
		InputProcessorPort p1 = new InputProcessorPort();
		p1.setParent(p);
		InputProcessorPort p2 = new InputProcessorPort();
		p2.setParent(p);
		InputProcessorPort p3 = new InputProcessorPort();
		p3.setParent(p);	
		
		CrossProduct cp = new CrossProduct();
		PortNode pNode1 = new PortNode();
		pNode1.setInputProcessorPort(p1);
		cp.add(pNode1);
		iss.add(cp);
		
		DotProduct dp = new DotProduct();
		PortNode pNode2 = new PortNode();
		pNode2.setInputProcessorPort(p2);
		dp.add(pNode2);
		iss.add(dp);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(p, true, rcvl);
		
		Set<PortMissingFromIterationStrategyStackProblem> problems = rcvl.getPortMissingFromIterationStrategyStackProblems();
		assertFalse(problems.isEmpty());

		boolean problem = false;
		for (PortMissingFromIterationStrategyStackProblem nlp : problems) {
			if (nlp.getPort().equals(p3) && nlp.getBean().equals(iss)) {
				problem = true;
			}
		}
		assertTrue(problem);
	}

}
