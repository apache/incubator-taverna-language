/**
 * 
 */
package uk.org.taverna.scufl2.validation.structural;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;

/**
 * @author alanrw
 *
 */
public class DefaultStructuralValidationListener implements
		StructuralValidationListener {

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#dataLinkReceiver(uk.org.taverna.scufl2.api.core.DataLink)
	 */
	@Override
	public void dataLinkReceiver(DataLink dl) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#dataLinkSender(uk.org.taverna.scufl2.api.core.DataLink)
	 */
	@Override
	public void dataLinkSender(DataLink dl) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#depthResolution(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.Integer)
	 */
	@Override
	public void depthResolution(WorkflowBean owp, Integer portResolvedDepth) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#dotProductIterationMismatch(uk.org.taverna.scufl2.api.iterationstrategy.DotProduct)
	 */
	@Override
	public void dotProductIterationMismatch(DotProduct dotProduct) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#emptyCrossProduct(uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct)
	 */
	@Override
	public void emptyCrossProduct(CrossProduct crossProduct) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#emptyDotProduct(uk.org.taverna.scufl2.api.iterationstrategy.DotProduct)
	 */
	@Override
	public void emptyDotProduct(DotProduct dotProduct) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#failedProcessorAdded(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void failedProcessorAdded(Processor p) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#incompleteWorkflow(uk.org.taverna.scufl2.api.core.Workflow)
	 */
	@Override
	public void incompleteWorkflow(Workflow w) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#missingIterationStrategyStack(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void missingIterationStrategyStack(Processor p) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#missingMainIncomingLink(uk.org.taverna.scufl2.api.port.ReceiverPort)
	 */
	@Override
	public void missingMainIncomingLink(ReceiverPort owp) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#passedProcessorAdded(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void passedProcessor(Processor p) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#unrecognizedIterationStrategyNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode)
	 */
	@Override
	public void unrecognizedIterationStrategyNode(
			IterationStrategyNode iterationStrategyNode) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#unresolvedOutput(uk.org.taverna.scufl2.api.port.OutputWorkflowPort)
	 */
	@Override
	public void unresolvedOutput(OutputWorkflowPort owp) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#unresolvedProcessorAdded(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void unresolvedProcessorAdded(Processor p) {
		// TODO Auto-generated method stub

	}

}
