/**
 * 
 */
package uk.org.taverna.scufl2.validation.structural;

import java.util.ArrayList;
import java.util.Collections;
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
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.validation.ValidationException;

/**
 * @author alanrw
 *
 */
public class ReportStructuralValidationListener extends
		DefaultStructuralValidationListener {
	
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

	/**
	 * @return the dotProductIterationMismatches
	 */
	public Set<DotProduct> getDotProductIterationMismatches() {
		return dotProductIterationMismatches;
	}

	/**
	 * @return the emptyCrossProducts
	 */
	public Set<CrossProduct> getEmptyCrossProducts() {
		return emptyCrossProducts;
	}

	/**
	 * @return the emptyDotProducts
	 */
	public Set<DotProduct> getEmptyDotProducts() {
		return emptyDotProducts;
	}

	/**
	 * @return the failedProcessors
	 */
	public Set<Processor> getFailedProcessors() {
		return failedProcessors;
	}

	/**
	 * @return the missingIterationStrategyStacks
	 */
	public Set<Processor> getMissingIterationStrategyStacks() {
		return missingIterationStrategyStacks;
	}

	/**
	 * @return the missingMainIncomingDataLinks
	 */
	public Set<ReceiverPort> getMissingMainIncomingDataLinks() {
		return missingMainIncomingDataLinks;
	}

	/**
	 * @return the unrecognizedIterationStrategyNodes
	 */
	public Set<IterationStrategyNode> getUnrecognizedIterationStrategyNodes() {
		return unrecognizedIterationStrategyNodes;
	}

	/**
	 * @return the unresolvedOutputs
	 */
	public Set<OutputWorkflowPort> getUnresolvedOutputs() {
		return unresolvedOutputs;
	}

	/**
	 * @return the unresolvedProcessors
	 */
	public Set<Processor> getUnresolvedProcessors() {
		return unresolvedProcessors;
	}
	
	@Override
	public boolean detectedProblems() {
		return (!
				(Collections.EMPTY_SET.equals(getDotProductIterationMismatches()) && 
				Collections.EMPTY_SET.equals(getEmptyCrossProducts()) && 
				Collections.EMPTY_SET.equals(getEmptyDotProducts()) && 
				Collections.EMPTY_SET.equals(getFailedProcessors()) && 
				Collections.EMPTY_SET.equals(getIncompleteWorkflows()) &&
				Collections.EMPTY_SET.equals(getMissingIterationStrategyStacks()) &&
				Collections.EMPTY_SET.equals(getMissingMainIncomingDataLinks()) &&
				Collections.EMPTY_SET.equals(getUnrecognizedIterationStrategyNodes()) &&
				Collections.EMPTY_SET.equals(getUnresolvedOutputs()) &&
				Collections.EMPTY_SET.equals(getUnresolvedProcessors())));
	}	

	@Override
	public ValidationException getException() {
		// TODO Needs to be improved;
		if (detectedProblems()) {
			return new ValidationException(this.toString());
		} else {
			return null;
		}
	}


}
