/**
 * 
 */
package uk.org.taverna.scufl2.validation.structural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;

/**
 * @author alanrw
 *
 */
public class ReportStructuralValidationEventListener extends
		DefaultStructuralValidationEventListener {
	
	private Map<SenderPort, List<DataLink>> senderDataLinkMap = new HashMap<SenderPort, List<DataLink>>();
	private Map<ReceiverPort, List<DataLink>> receiverDataLinkMap = new HashMap<ReceiverPort, List<DataLink>>();
	private Map<WorkflowBean, Integer> resolvedDepthMap = new HashMap<WorkflowBean, Integer> ();
	private Set<DotProduct> dotProductIterationMismatches = new HashSet<DotProduct>();
	private Set<CrossProduct> emptyCrossProducts = new HashSet<CrossProduct>();
	private Set<DotProduct> emptyDotProducts = new HashSet<DotProduct>();
	private Set<Processor> failedProcessors = new HashSet<Processor>();
	private Set<Workflow> incompleteWorkflows = new HashSet<Workflow>();
	private Set<Processor> missingIterationStrategyStacks = new HashSet<Processor>();
	private Set<ReceiverPort> missingMainIncomingDataLinks = new HashSet<ReceiverPort> ();
	private Set<Processor> passedProcessors = new HashSet<Processor>();
	private Set<IterationStrategyNode> unrecognizedIterationStrategyNodes = new HashSet<IterationStrategyNode>();
	private Set<OutputWorkflowPort> unresolvedOutputs = new HashSet<OutputWorkflowPort>();
	private Set<Processor> unresolvedProcessors = new HashSet<Processor>();
	
	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#dataLinkReceiver(uk.org.taverna.scufl2.api.core.DataLink)
	 */
	@Override
	public void dataLinkReceiver(DataLink dl) {
		ReceiverPort receiver = dl.getSendsTo();
		if (receiver != null) {
			if (!receiverDataLinkMap.containsKey(receiver)) {
				receiverDataLinkMap.put(receiver, new ArrayList<DataLink>());
			}
			receiverDataLinkMap.get(receiver).add(dl);
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#dataLinkSender(uk.org.taverna.scufl2.api.core.DataLink)
	 */
	@Override
	public void dataLinkSender(DataLink dl) {
		SenderPort sender = dl.getReceivesFrom();
		if (sender != null) {
			if (!senderDataLinkMap.containsKey(sender)) {
				senderDataLinkMap.put(sender, new ArrayList<DataLink>());
			}
			senderDataLinkMap.get(sender).add(dl);
		}
	}
	

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#depthResolution(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.Integer)
	 */
	@Override
	public void depthResolution(WorkflowBean owp, Integer portResolvedDepth) {
		resolvedDepthMap.put(owp, portResolvedDepth);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#dotProductIterationMismatch(uk.org.taverna.scufl2.api.iterationstrategy.DotProduct)
	 */
	@Override
	public void dotProductIterationMismatch(DotProduct dotProduct) {
		dotProductIterationMismatches.add(dotProduct);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#emptyCrossProduct(uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct)
	 */
	@Override
	public void emptyCrossProduct(CrossProduct crossProduct) {
		emptyCrossProducts.add(crossProduct);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#emptyDotProduct(uk.org.taverna.scufl2.api.iterationstrategy.DotProduct)
	 */
	@Override
	public void emptyDotProduct(DotProduct dotProduct) {
		emptyDotProducts.add(dotProduct);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#failedProcessorAdded(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void failedProcessorAdded(Processor p) {
		failedProcessors.add(p);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#incompleteWorkflow(uk.org.taverna.scufl2.api.core.Workflow)
	 */
	@Override
	public void incompleteWorkflow(Workflow w) {
		incompleteWorkflows.add(w);
	}
	
	/**
	 * @return
	 */
	public Set<Workflow> getIncompleteWorkflows() {
		return incompleteWorkflows;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#missingIterationStrategyStack(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void missingIterationStrategyStack(Processor p) {
		missingIterationStrategyStacks.add(p);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#missingMainIncomingLink(uk.org.taverna.scufl2.api.port.ReceiverPort)
	 */
	@Override
	public void missingMainIncomingLink(ReceiverPort owp) {
		missingMainIncomingDataLinks.add(owp);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#passedProcessorAdded(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void passedProcessor(Processor p) {
		passedProcessors.add(p);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#unrecognizedIterationStrategyNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode)
	 */
	@Override
	public void unrecognizedIterationStrategyNode(
			IterationStrategyNode iterationStrategyNode) {
		unrecognizedIterationStrategyNodes.add(iterationStrategyNode);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#unresolvedOutput(uk.org.taverna.scufl2.api.port.OutputWorkflowPort)
	 */
	@Override
	public void unresolvedOutput(OutputWorkflowPort owp) {
		unresolvedOutputs.add(owp);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.structural.StructuralValidationEventListener#unresolvedProcessorAdded(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void unresolvedProcessorAdded(Processor p) {
		unresolvedProcessors.add(p);
	}


}
