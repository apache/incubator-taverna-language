/**
 * 
 */
package uk.org.taverna.scufl2.validation.structural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.validation.ValidationProblem;


/**
 * @author alanrw
 *
 */
public final class StructuralValidator {
	
	private static enum ProcessorCheckStatus { COULD_NOT_CHECK, PASSED, FAILED };
	
	protected ThreadLocal<ValidatorState> validatorState = new ThreadLocal<ValidatorState>() {
		@Override
		protected ValidatorState initialValue() {
			return new ValidatorState();
		};
	};
	
	public void checkStructure(WorkflowBundle bundle, StructuralValidationListener eventListener) {
		validatorState.get().setEventListener(eventListener);
		validatorState.get().setWorkflowBundle(bundle);
		for (Workflow w : bundle.getWorkflows()) {
			checkStructure(w);
		}
	}
	
	public ValidatorState getValidatorState() {
		return validatorState.get();
	}
	
	public void checkStructure(Workflow workflow, StructuralValidationListener eventListener) {
		validatorState.get().setEventListener(eventListener);
		checkStructure(workflow);
	}

	private void checkStructure(Workflow workflow) {
		validatorState.get().setWorkflow(workflow);
		validateWorkflow();
	}
	
	private void validateWorkflow() {
		clearWorkflowData();
		rememberDataLinkConnections();
		inheritDataLinkDepthsFromWorkflowInputPorts();
		checkProcessors();		
		checkWorkflowOutputPorts();
		checkCompleteness();
	}
	
	private void clearWorkflowData() {
		validatorState.get().clearWorkflowData();
	}

	private void checkCompleteness() {
		Workflow w = validatorState.get().getWorkflow();
		if (w.getProcessors().isEmpty() && w.getOutputPorts().isEmpty()) {
			validatorState.get().getEventListener().incompleteWorkflow(w);
//			validatorState.get().addIncompleteWorkflow(w);
		}
	}

	private void checkProcessors() {
		Workflow workflow = validatorState.get().getWorkflow();
		List<Processor> failedProcessors = new ArrayList<Processor>();
		List<Processor> unresolvedProcessors = new ArrayList<Processor>();
		unresolvedProcessors.addAll(workflow.getProcessors());
		
		boolean finished = false;
        while (!finished) {
            // We're finished unless something happens later
            finished = true;
            // Keep a list of processors to remove from the unresolved list
            // because they've been resolved properly
            List<Processor> removeValidated = new ArrayList<Processor>();
            // Keep another list of those that have failed
            List<Processor> removeFailed = new ArrayList<Processor>();
            
            
            for (Processor p : unresolvedProcessors) {

            				validatorState.get().setProcessor(p);
                            ProcessorCheckStatus entityValid = checkProcessor();
                            switch (entityValid) {
                            case PASSED:
                            	validatorState.get().getEventListener().passedProcessor(p);
//                            	validatorState.get().addPassedProcessor(p);
                            	removeValidated.add(p);
                            	break;
                            case FAILED:
                            	validatorState.get().getEventListener().failedProcessorAdded(p);
//                            	validatorState.get().failCurrentProcessor();
                            	removeFailed.add(p);
                            	break;
                            case COULD_NOT_CHECK:
                            	break;
                    }
            }

            /**
             * Remove validated and failed items from the pending lists. If
             * anything was removed because it validated okay then we're not
             * finished yet and should reset the boolean finished flag
             */
            if (!removeValidated.isEmpty()) {
            	unresolvedProcessors.removeAll(removeValidated);
            	finished = false;
            }
            unresolvedProcessors.removeAll(failedProcessors);

        }
        for (Processor p : unresolvedProcessors) {
        	validatorState.get().getEventListener().unresolvedProcessorAdded(p);
        }
 //       validatorState.get().addUnresolvedProcessors(unresolvedProcessors);
		
	}

	private void checkWorkflowOutputPorts() {
		for (OutputWorkflowPort owp : validatorState.get().getWorkflow().getOutputPorts()) {
			DataLink mainIncomingLink = validatorState.get().getMainIncomingDataLink(owp);
            if (mainIncomingLink == null) {
            	validatorState.get().getEventListener().missingMainIncomingLink(owp);
//            	validatorState.get().addMissingMainIncomingDataLink(owp);
            }
            Integer dataLinkResolvedDepth = validatorState.get().getDataLinkResolvedDepth(mainIncomingLink);
            if (dataLinkResolvedDepth == null) {
            	validatorState.get().getEventListener().unresolvedOutput(owp);
//            	validatorState.get().addUnresolvedOutput(owp);
            	return;
            }
			
//				int granularDepth = mainIncomingLink.getSource().getGranularDepth();
            Integer portResolvedDepth = dataLinkResolvedDepth + (validatorState.get().isMergedPort(owp) ? 1 : 0);
            validatorState.get().getEventListener().depthResolution(owp, portResolvedDepth);
				validatorState.get().setPortResolvedDepth(owp, portResolvedDepth);
//				dopi.setDepths(resolvedDepth, granularDepth);
		}
	}


	private ProcessorCheckStatus checkProcessor() {
		ProcessorCheckStatus result = ProcessorCheckStatus.COULD_NOT_CHECK;
		
		Processor p = validatorState.get().getProcessor();
		Map<InputProcessorPort, Integer> inputDepths = new HashMap<InputProcessorPort, Integer>();
		// Check whether all our input ports have inbound links
        for (InputProcessorPort input : p.getInputPorts()) {
        	DataLink mainIncomingLink = validatorState.get().getMainIncomingDataLink(input);
                if (mainIncomingLink == null) {
                	validatorState.get().getEventListener().missingMainIncomingLink(input);
//                	validatorState.get().addMissingMainIncomingDataLink(input);
                        return ProcessorCheckStatus.FAILED;
                }
                Integer dataLinkResolvedDepth = validatorState.get().getDataLinkResolvedDepth(mainIncomingLink);
                if (dataLinkResolvedDepth == null) {
                	return ProcessorCheckStatus.COULD_NOT_CHECK;
                }

                Integer resolvedDepth = dataLinkResolvedDepth + (validatorState.get().isMergedPort(input) ? 1 : 0);
                validatorState.get().getEventListener().depthResolution(input, resolvedDepth);
				validatorState.get().setPortResolvedDepth(input, resolvedDepth);
                inputDepths.put(input, resolvedDepth);
        }
        
        	Integer resultWrappingDepth = calculateResultWrappingDepth(inputDepths);
        	if (resultWrappingDepth == null) {
        		return ProcessorCheckStatus.FAILED;
        	}
        	       	
        	for (OutputProcessorPort output : p.getOutputPorts()) {
        		Integer portDepth = output.getDepth();
       			Integer resolvedDepth = portDepth + resultWrappingDepth;
       			validatorState.get().getEventListener().depthResolution(output, resolvedDepth);
       			validatorState.get().setPortResolvedDepth(output, resolvedDepth);
        		for (DataLink dl : validatorState.get().getOutgoingDataLinks(output)) {
        			validatorState.get().getEventListener().depthResolution(dl, resolvedDepth);
         			validatorState.get().setDataLinkResolvedDepth(dl, resolvedDepth);
        		}
        	}

		return ProcessorCheckStatus.PASSED;
		
	}


	Integer calculateResultWrappingDepth(Map<InputProcessorPort, Integer> inputDepths) {
		Processor p = validatorState.get().getProcessor();
		IterationStrategyStack iss = p.getIterationStrategyStack();
		if (iss == null) {
			validatorState.get().getEventListener().missingIterationStrategyStack(p);
//			validatorState.get().addMissingIterationStrategyStack(p);
			validatorState.get().getEventListener().failedProcessorAdded(p);
//			validatorState.get().failCurrentProcessor();
			return null;
		}
		
		if (iss.isEmpty()) {
			return 0;
		}
		IterationStrategyTopNode iterationStrategyTopNode = iss.get(0);
		Integer depth = getIterationDepth(iterationStrategyTopNode, inputDepths);
		if (depth == null) {
			return null;
		}
		IterationStrategyTopNode previousNode = iterationStrategyTopNode;
		for (int index = 1; index < iss.size(); index++) {
			// Construct the input depths for the staged iteration strategies
			// after the first one by looking at the previous iteration
			// strategy's desired cardinalities on its input ports.
			Map<InputProcessorPort, Integer> stagedInputDepths = getDesiredCardinalities(previousNode);
			iterationStrategyTopNode = iss.get(index);
			Integer nodeDepth = getIterationDepth(iterationStrategyTopNode, stagedInputDepths);
			if (nodeDepth == null) {
				return null;
			}
			depth += nodeDepth;
			previousNode = iterationStrategyTopNode;
		}
		return depth;
	}
	
	private Map<InputProcessorPort, Integer> getDesiredCardinalities(
			IterationStrategyTopNode iterationStrategyTopNode) {
		Map<InputProcessorPort, Integer> desiredCardinalities = new HashMap<InputProcessorPort, Integer>();
		fillInDesiredCardinalities(iterationStrategyTopNode, desiredCardinalities);
		return desiredCardinalities;
	}

	private void fillInDesiredCardinalities(
			IterationStrategyNode iterationStrategyNode,
			Map<InputProcessorPort, Integer> desiredCardinalities) {
		if (iterationStrategyNode instanceof IterationStrategyTopNode) {
			for (IterationStrategyNode subNode : (IterationStrategyTopNode) iterationStrategyNode) {
				fillInDesiredCardinalities (subNode, desiredCardinalities);
			}
		} else if (iterationStrategyNode instanceof PortNode) {
			PortNode portNode = (PortNode) iterationStrategyNode;
			desiredCardinalities.put(portNode.getInputProcessorPort(), portNode.getDesiredDepth());
		}
	}

	public Integer getIterationDepth(IterationStrategyNode iterationStrategyNode, Map<InputProcessorPort, Integer> inputDepths) {
		if (iterationStrategyNode instanceof CrossProduct) {
			return getCrossProductIterationDepth ((CrossProduct) iterationStrategyNode, inputDepths);
		}
		if (iterationStrategyNode instanceof DotProduct) {
			return getDotProductIterationDepth ((DotProduct) iterationStrategyNode, inputDepths);
		}
		if (iterationStrategyNode instanceof PortNode) {
			return getPortNodeIterationDepth((PortNode) iterationStrategyNode, inputDepths);
		}
		validatorState.get().getEventListener().unrecognizedIterationStrategyNode(iterationStrategyNode);
//		validatorState.get().addUnrecognizedIterationStrategyNode(iterationStrategyNode);
		validatorState.get().getEventListener().failedProcessorAdded(validatorState.get().getProcessor());
//		validatorState.get().failCurrentProcessor();
		return null;
	}


	private Integer getPortNodeIterationDepth(PortNode portNode,
			Map<InputProcessorPort, Integer> inputDepths) {
		int myInputDepth = inputDepths.get(portNode.getInputProcessorPort());
		int depthMismatch = myInputDepth - portNode.getDesiredDepth();
		return (depthMismatch > 0 ? depthMismatch : 0);
	}

	public Integer getDotProductIterationDepth(
			DotProduct dotProduct,
			Map<InputProcessorPort, Integer> inputDepths) {
		if (dotProduct.isEmpty()) {
			validatorState.get().getEventListener().emptyDotProduct(dotProduct);
//			validatorState.get().addEmptyDotProduct(dotProduct);
			validatorState.get().getEventListener().failedProcessorAdded(validatorState.get().getProcessor());
//			validatorState.get().failCurrentProcessor();
			return null;			
		}
		Integer depth = getIterationDepth(dotProduct.get(0), inputDepths);
		if (depth == null) {
			return null;
		}
		for (IterationStrategyNode childNode : dotProduct) {
			Integer childNodeDepth = getIterationDepth(childNode, inputDepths);
			if (childNodeDepth == null) {
				return null;
			}
			if (!childNodeDepth.equals(depth)) {
				validatorState.get().getEventListener().dotProductIterationMismatch(dotProduct);
//				validatorState.get().addDotProductIterationMismatch(dotProduct);
				validatorState.get().getEventListener().failedProcessorAdded(validatorState.get().getProcessor());
//				validatorState.get().failCurrentProcessor();
				return null;
			}
		}
		return depth;
	}

	private Integer getCrossProductIterationDepth(
			CrossProduct crossProduct,
			Map<InputProcessorPort, Integer> inputDepths) {
		if (crossProduct.isEmpty()) {
			validatorState.get().getEventListener().emptyCrossProduct(crossProduct);
//			validatorState.get().addEmptyCrossProduct(crossProduct);
			validatorState.get().getEventListener().failedProcessorAdded(validatorState.get().getProcessor());
//			validatorState.get().failCurrentProcessor();
			return null;
		}
		int temp = 0;
		for (IterationStrategyNode child : crossProduct) {
			Integer childNodeDepth = getIterationDepth(child, inputDepths);
			if (childNodeDepth == null) {
				return null;
			}
			temp += childNodeDepth;
		}
		return temp;
	}

	private void rememberDataLinkConnections() {
		Workflow workflow = validatorState.get().getWorkflow();
		for (DataLink dl : workflow.getDataLinks()) {
			validatorState.get().getEventListener().dataLinkSender(dl);
			validatorState.get().rememberDataLinkSender(dl);
			validatorState.get().getEventListener().dataLinkReceiver(dl);		
			validatorState.get().rememberDataLinkReceiver(dl);
		}
	}

	private void inheritDataLinkDepthsFromWorkflowInputPorts() {
		Workflow workflow = validatorState.get().getWorkflow();
		for (InputWorkflowPort iwp : workflow.getInputPorts()) {
			Integer iwpDepth = iwp.getDepth();
			validatorState.get().getEventListener().depthResolution(iwp, iwpDepth);
			validatorState.get().setPortResolvedDepth(iwp, iwpDepth);
			for (DataLink dl : validatorState.get().getOutgoingDataLinks(iwp)) {
				validatorState.get().getEventListener().depthResolution(dl, iwpDepth);
				validatorState.get().setDataLinkResolvedDepth(dl, iwpDepth);
			}
		}
	}

}
