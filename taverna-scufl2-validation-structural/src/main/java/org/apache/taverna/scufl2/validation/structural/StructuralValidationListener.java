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
import org.apache.taverna.scufl2.validation.ValidationReport;


public interface StructuralValidationListener extends ValidationReport {
	void passedProcessor(Processor p);

	void failedProcessorAdded(Processor p);

	void unresolvedProcessorAdded(Processor p);

	void missingMainIncomingLink(ReceiverPort owp);

	void unresolvedOutput(OutputWorkflowPort owp);

	void depthResolution(WorkflowBean owp, Integer portResolvedDepth);

	void missingIterationStrategyStack(Processor p);

	void emptyDotProduct(DotProduct dotProduct);

	void dotProductIterationMismatch(DotProduct dotProduct);

	void emptyCrossProduct(CrossProduct crossProduct);

	void dataLinkSender(DataLink dl);

	void dataLinkReceiver(DataLink dl);

	void unrecognizedIterationStrategyNode(
			IterationStrategyNode iterationStrategyNode);

	void incompleteWorkflow(Workflow w);
}
