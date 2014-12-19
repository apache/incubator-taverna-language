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
import uk.org.taverna.scufl2.validation.ValidationReport;

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
