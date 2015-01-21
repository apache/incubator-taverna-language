/**
 * 
 */
package org.apache.taverna.scufl2.validation.structural;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.validation.ValidationException;


/**
 * @author alanrw
 */
public class DefaultStructuralValidationListener implements
		StructuralValidationListener {
	@Override
	public void dataLinkReceiver(DataLink dl) {
	}

	@Override
	public void dataLinkSender(DataLink dl) {
	}

	@Override
	public void depthResolution(WorkflowBean owp, Integer portResolvedDepth) {
	}

	@Override
	public void dotProductIterationMismatch(DotProduct dotProduct) {
	}

	@Override
	public void emptyCrossProduct(CrossProduct crossProduct) {
	}

	@Override
	public void emptyDotProduct(DotProduct dotProduct) {
	}

	@Override
	public void failedProcessorAdded(Processor p) {
	}

	@Override
	public void incompleteWorkflow(Workflow w) {
	}

	@Override
	public void missingIterationStrategyStack(Processor p) {
	}

	@Override
	public void missingMainIncomingLink(ReceiverPort owp) {
	}

	@Override
	public void passedProcessor(Processor p) {
	}

	@Override
	public void unrecognizedIterationStrategyNode(
			IterationStrategyNode iterationStrategyNode) {
	}

	@Override
	public void unresolvedOutput(OutputWorkflowPort owp) {
	}

	@Override
	public void unresolvedProcessorAdded(Processor p) {
	}

	@Override
	public boolean detectedProblems() {
		return false;
	}

	@Override
	public ValidationException getException() {
		return null;
	}
}
