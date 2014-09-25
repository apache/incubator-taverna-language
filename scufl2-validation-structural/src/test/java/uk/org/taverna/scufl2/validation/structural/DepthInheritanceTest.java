/**
 * 
 */
package uk.org.taverna.scufl2.validation.structural;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * @author alanrw
 * 
 */
public class DepthInheritanceTest {

	@Test
	public void testDataLinkInheritanceFromInputWorkflowPorts() {
		WorkflowBundle wb = new WorkflowBundle();
		Workflow w = new Workflow();
		wb.setMainWorkflow(w);

		InputWorkflowPort a = new InputWorkflowPort(w, "a");
		a.setDepth(0);

		InputWorkflowPort b = new InputWorkflowPort(w, "b");
		b.setDepth(7);

		OutputWorkflowPort outA = new OutputWorkflowPort(w, "outA");
		OutputWorkflowPort outB = new OutputWorkflowPort(w, "outB");

		DataLink aLink = new DataLink(w, a, outA);
		DataLink bLink = new DataLink(w, b, outB);

		StructuralValidator sv = new StructuralValidator();
		ReportStructuralValidationListener l = new ReportStructuralValidationListener();
		sv.checkStructure(wb, l);
		ValidatorState vs = sv.getValidatorState();
		assertEquals(0, l.getIncompleteWorkflows().size());
		assertEquals(Integer.valueOf(0), vs.getDataLinkResolvedDepth(aLink));
		assertEquals(Integer.valueOf(7), vs.getDataLinkResolvedDepth(bLink));
		assertEquals(Integer.valueOf(0), vs.getPortResolvedDepth(outA));
		assertEquals(Integer.valueOf(7), vs.getPortResolvedDepth(outB));
	}

	@Test
	public void testMergingToOutputWorkflowPort() {
		WorkflowBundle wb = new WorkflowBundle();
		Workflow w = new Workflow();
		wb.setMainWorkflow(w);

		InputWorkflowPort a = new InputWorkflowPort(w, "a");
		a.setDepth(0);

		InputWorkflowPort b = new InputWorkflowPort(w, "b");
		b.setDepth(7);

		OutputWorkflowPort outA = new OutputWorkflowPort(w, "outA");
		OutputWorkflowPort outB = new OutputWorkflowPort(w, "outB");

		DataLink aLink = new DataLink(w, a, outA);
		aLink.setMergePosition(0);
		DataLink bLink = new DataLink(w, b, outA);
		bLink.setMergePosition(1);

		DataLink aLink2 = new DataLink(w, a, outB);
		aLink2.setMergePosition(1);
		DataLink bLink2 = new DataLink(w, b, outB);
		bLink2.setMergePosition(0);

		StructuralValidator sv = new StructuralValidator();
		ReportStructuralValidationListener l = new ReportStructuralValidationListener();
		sv.checkStructure(wb, l);
		ValidatorState vs = sv.getValidatorState();
		assertEquals(0, l.getIncompleteWorkflows().size());
		assertEquals(Integer.valueOf(0), vs.getDataLinkResolvedDepth(aLink));
		assertEquals(Integer.valueOf(7), vs.getDataLinkResolvedDepth(bLink));
		assertEquals(Integer.valueOf(1), vs.getPortResolvedDepth(outA));
		assertEquals(Integer.valueOf(8), vs.getPortResolvedDepth(outB));

	}

	@Test
	public void testSimpleIteration() {
		WorkflowBundle wb = new WorkflowBundle();
		Workflow w = new Workflow();
		wb.setMainWorkflow(w);

		InputWorkflowPort a = new InputWorkflowPort(w, "a");
		a.setDepth(1);

		Processor p = new Processor(w, "p");

		InputProcessorPort ipp = new InputProcessorPort(p, "in");
		ipp.setDepth(0);

		OutputProcessorPort opp = new OutputProcessorPort(p, "out");
		opp.setDepth(3);

		DataLink inLink = new DataLink(w, a, ipp);

		IterationStrategyStack iss = new IterationStrategyStack(p);
		CrossProduct cp = new CrossProduct();
		iss.add(cp);
		PortNode portNode = new PortNode(cp, ipp);
		portNode.setDesiredDepth(0);

		StructuralValidator sv = new StructuralValidator();
		ReportStructuralValidationListener l = new ReportStructuralValidationListener();
		sv.checkStructure(wb, l);
		ValidatorState vs = sv.getValidatorState();
		assertEquals(0, l.getIncompleteWorkflows().size());
		assertEquals(Integer.valueOf(1), vs.getPortResolvedDepth(a));
		assertEquals(Integer.valueOf(1), vs.getDataLinkResolvedDepth(inLink));
		assertEquals(Integer.valueOf(1), vs.getPortResolvedDepth(ipp));
		assertEquals(Integer.valueOf(4), vs.getPortResolvedDepth(opp));
	}

}
