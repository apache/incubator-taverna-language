/**
 * 
 */
package org.apache.taverna.scufl2.validation.structural;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.validation.ValidationException;


/**
 * @author alanrw
 */
public class ReportStructuralValidationListener extends
		DefaultStructuralValidationListener {
	private Map<SenderPort, List<DataLink>> senderDataLinkMap = new HashMap<>();
	private Map<ReceiverPort, List<DataLink>> receiverDataLinkMap = new HashMap<>();
	private Map<WorkflowBean, Integer> resolvedDepthMap = new HashMap<>();
	private Set<DotProduct> dotProductIterationMismatches = new HashSet<>();
	private Set<CrossProduct> emptyCrossProducts = new HashSet<>();
	private Set<DotProduct> emptyDotProducts = new HashSet<>();
	private Set<Processor> failedProcessors = new HashSet<>();
	private Set<Workflow> incompleteWorkflows = new HashSet<>();
	private Set<Processor> missingIterationStrategyStacks = new HashSet<>();
	private Set<ReceiverPort> missingMainIncomingDataLinks = new HashSet<>();
	private Set<Processor> passedProcessors = new HashSet<>();
	private Set<IterationStrategyNode> unrecognizedIterationStrategyNodes = new HashSet<>();
	private Set<OutputWorkflowPort> unresolvedOutputs = new HashSet<>();
	private Set<Processor> unresolvedProcessors = new HashSet<>();

	@Override
	public void dataLinkReceiver(DataLink dl) {
		ReceiverPort receiver = dl.getSendsTo();
		if (receiver != null) {
			if (!receiverDataLinkMap.containsKey(receiver))
				receiverDataLinkMap.put(receiver, new ArrayList<DataLink>());
			receiverDataLinkMap.get(receiver).add(dl);
		}
	}

	@Override
	public void dataLinkSender(DataLink dl) {
		SenderPort sender = dl.getReceivesFrom();
		if (sender != null) {
			if (!senderDataLinkMap.containsKey(sender))
				senderDataLinkMap.put(sender, new ArrayList<DataLink>());
			senderDataLinkMap.get(sender).add(dl);
		}
	}
	
	@Override
	public void depthResolution(WorkflowBean owp, Integer portResolvedDepth) {
		resolvedDepthMap.put(owp, portResolvedDepth);
	}

	@Override
	public void dotProductIterationMismatch(DotProduct dotProduct) {
		dotProductIterationMismatches.add(dotProduct);
	}

	@Override
	public void emptyCrossProduct(CrossProduct crossProduct) {
		emptyCrossProducts.add(crossProduct);
	}

	@Override
	public void emptyDotProduct(DotProduct dotProduct) {
		emptyDotProducts.add(dotProduct);
	}

	@Override
	public void failedProcessorAdded(Processor p) {
		failedProcessors.add(p);
	}

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

	@Override
	public void missingIterationStrategyStack(Processor p) {
		missingIterationStrategyStacks.add(p);
	}

	@Override
	public void missingMainIncomingLink(ReceiverPort owp) {
		missingMainIncomingDataLinks.add(owp);
	}

	@Override
	public void passedProcessor(Processor p) {
		passedProcessors.add(p);
	}

	@Override
	public void unrecognizedIterationStrategyNode(
			IterationStrategyNode iterationStrategyNode) {
		unrecognizedIterationStrategyNodes.add(iterationStrategyNode);
	}

	@Override
	public void unresolvedOutput(OutputWorkflowPort owp) {
		unresolvedOutputs.add(owp);
	}

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
		return !(dotProductIterationMismatches.isEmpty()
				&& emptyCrossProducts.isEmpty() && emptyDotProducts.isEmpty()
				&& failedProcessors.isEmpty() && incompleteWorkflows.isEmpty()
				&& missingIterationStrategyStacks.isEmpty()
				&& missingMainIncomingDataLinks.isEmpty()
				&& unrecognizedIterationStrategyNodes.isEmpty()
				&& unresolvedOutputs.isEmpty() && unresolvedProcessors
					.isEmpty());
	}	

	@Override
	public ValidationException getException() {
		if (!detectedProblems())
			return null;
		return new ValidationException(this.toString());
	}
}
