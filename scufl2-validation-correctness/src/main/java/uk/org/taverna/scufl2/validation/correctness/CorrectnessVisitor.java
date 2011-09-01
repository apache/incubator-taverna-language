/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.net.URI;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStack;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
import uk.org.taverna.scufl2.api.port.AbstractDepthPort;
import uk.org.taverna.scufl2.api.port.AbstractGranularDepthPort;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ProcessorPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.port.WorkflowPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.PropertyResource.PropertyVisit;

/**
 * @author alanrw
 *
 */
public class CorrectnessVisitor extends DispatchingVisitor {
	
	private final boolean checkComplete;
	private final CorrectnessValidationListener listener;

	public CorrectnessVisitor(boolean checkComplete, CorrectnessValidationListener listener) {
		this.checkComplete = checkComplete;
		this.listener = listener;
	}

	public boolean isCheckComplete() {
		return checkComplete;
	}
	
	private WorkflowBean peekPath() {
		return getCurrentPath().peek();
	}
	
	public <T> T findAncestral(Child bean,
			Class<T> class1) {
		T result = null;
		for (WorkflowBean parent = bean.getParent(); parent != null; parent = ((Child)parent).getParent()) {
			if (class1.isInstance(parent)) {
				return (T) parent;
			}
			if (!(parent instanceof Child)) {
				return null;
			}
		}
		return result;
		
	}

	@Override
	public void visitAbstractDepthPort(AbstractDepthPort bean) {
		Integer depth = bean.getDepth();
		if (depth != null) {
			if (depth < 0) {
				listener.negativeValue(bean, "depth", depth);
			}
		}
		if (checkComplete) {
			if (depth == null) {
				listener.nullField(bean, "depth");
			}
		}
	}

	@Override
	public void visitAbstractGranularDepthPort(AbstractGranularDepthPort bean) {
		Integer granularDepth = bean.getGranularDepth();
		if (granularDepth != null) {
			if (granularDepth < 0) {
				listener.negativeValue(bean, "granularDepth", granularDepth);
			}
			Integer depth = bean.getDepth();
			if (depth != null) {
				if (granularDepth > depth) {
					listener.incompatibleGranularDepth(bean, depth, granularDepth);
				}
			}
		}
		if (checkComplete) {
			if (granularDepth == null) {
				listener.nullField(bean, "granularDepth");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitActivity(uk.org.taverna.scufl2.api.activity.Activity)
	 */
	@Override
	public void visitActivity(Activity bean) {
		// All checks are covered by those in Named, Typed, Child.
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitActivityPort(uk.org.taverna.scufl2.api.port.ActivityPort)
	 */
	@Override
	public void visitActivityPort(ActivityPort bean) {
		// All checks are covered by those in Named and Child
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitBlockingControlLink(uk.org.taverna.scufl2.api.core.BlockingControlLink)
	 */
	@Override
	public void visitBlockingControlLink(BlockingControlLink bean) {
		// Also checks from Child
		
		Workflow parent = bean.getParent();
		Processor block = bean.getBlock();
		Processor untilFinished = bean.getUntilFinished();
		
		// Check the block and untilFinished processors are in the same workflow
		if (block != null) {
			Workflow blockParent = block.getParent();
			if ((parent == null) || !parent.equals(blockParent)) {
				listener.outOfScopeValue(bean, "block", block);
			}
		}
		if (untilFinished != null) {
			Workflow untilFinishedParent = untilFinished.getParent();
			if ((parent == null) || !parent.equals(untilFinishedParent)) {
				listener.outOfScopeValue(bean, "untilFinished", untilFinished);
			}
		}

		// Check the block and untilFinished processors are specified
		if (checkComplete) {
			if (block == null) {
				listener.nullField(bean, "block");
			}
			if (untilFinished == null) {
				listener.nullField(bean, "untilFinished");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitChild(uk.org.taverna.scufl2.api.common.Child)
	 */
	@Override
	public void visitChild(Child bean) {
		WorkflowBean p = bean.getParent();
		if (p != null) {
			try {
				WorkflowBean up = peekPath();
				if ((up != null) && (up != p)) {
					listener.wrongParent(bean);
				}
			} catch (EmptyStackException e) {
				// Nothing
			}
		}
		if (checkComplete) {
			if (p == null) {
				listener.nullField(bean, "parent");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitConfigurable(uk.org.taverna.scufl2.api.common.Configurable)
	 */
	@Override
	public void visitConfigurable(Configurable bean) {
		// Are there any checks that it is actually configured?
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitConfiguration(uk.org.taverna.scufl2.api.configurations.Configuration)
	 */
	@Override
	public void visitConfiguration(Configuration bean) {
		Configurable configures = bean.getConfigures();
		PropertyResource propertyResource = bean.getPropertyResource();
		URI configuresType = null;
		if (configures != null) {
			if (configures instanceof Typed) {
				configuresType = ((Typed) configures).getConfigurableType();
			}
		}
		// Correct check cannot be completed unless property descriptions are available
//		URI configurationType = bean.getConfigurableType();
//		if ((configuresType != null) && (configurationType != null)) {
//			if (!configuresType.equals(configurationType)) {
//				listener.mismatchConfigurableType(bean, configures);
//			}
//		}
		
		// TODO Check that the PropertyResource is correct
		
		if (checkComplete) {
			if (configures == null) {
				listener.nullField(bean, "configures");
			}
			if (propertyResource == null) {
				listener.nullField(bean, "propertyResource");
			}
			// TODO Check that the PropertyResource is complete
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitControlLink(uk.org.taverna.scufl2.api.core.ControlLink)
	 */
	@Override
	public void visitControlLink(ControlLink bean) {
		// All done in Child or BlockingControlLink
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitCrossProduct(uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct)
	 */
	@Override
	public void visitCrossProduct(CrossProduct bean) {
		// All done in IterationStrategyTopNode and Child
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDataLink(uk.org.taverna.scufl2.api.core.DataLink)
	 */
	@Override
	public void visitDataLink(DataLink bean) {
		ReceiverPort sendsTo = bean.getSendsTo();
		SenderPort receivesFrom = bean.getReceivesFrom();
		
		Workflow parent = bean.getParent();
		if (sendsTo != null) {
			Workflow sendsToWorkflow = findAncestral((Child) sendsTo,
					Workflow.class);
			if ((parent == null) || !parent.equals(sendsToWorkflow)) {
				listener.outOfScopeValue(bean, "sendsTo", sendsTo);
			}
		}
		if (receivesFrom != null) {
			Workflow receivesFromWorkflow = findAncestral((Child) receivesFrom,
					Workflow.class);
			if ((parent == null) || !parent.equals(receivesFromWorkflow)) {
				listener.outOfScopeValue(bean, "receivesFrom", receivesFrom);
			}
		}
		
		Integer mergePosition = bean.getMergePosition();
		if (mergePosition != null) {
			if (mergePosition < 0) {
				listener.negativeValue(bean, "mergePosition", mergePosition);
			}
		}
		
		// How to check mergePosition
		
		if (checkComplete) {
			if (sendsTo == null) {
				listener.nullField(bean, "sendsTo");
			}
			if (receivesFrom == null) {
				listener.nullField(bean, "receivesFrom");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDispatchStack(uk.org.taverna.scufl2.api.dispatchstack.DispatchStack)
	 */
	@Override
	public void visitDispatchStack(DispatchStack bean) {
		// All done in Child
		
		// The type needs sorting out. Is it mandatory?
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDispatchStackLayer(uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer)
	 */
	@Override
	public void visitDispatchStackLayer(DispatchStackLayer bean) {
		// All done in Typed and Child
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDotProduct(uk.org.taverna.scufl2.api.iterationstrategy.DotProduct)
	 */
	@Override
	public void visitDotProduct(DotProduct bean) {
		// All done in IterationStrategyTopNode and Child
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputActivityPort(uk.org.taverna.scufl2.api.port.InputActivityPort)
	 */
	@Override
	public void visitInputActivityPort(InputActivityPort bean) {
		// All done in Child, Named and Configurable	
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputPort(uk.org.taverna.scufl2.api.port.InputPort)
	 */
	@Override
	public void visitInputPort(InputPort bean) {
		// All done in Named and Configurable
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputProcessorPort(uk.org.taverna.scufl2.api.port.InputProcessorPort)
	 */
	@Override
	public void visitInputProcessorPort(InputProcessorPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputWorkflowPort(uk.org.taverna.scufl2.api.port.InputWorkflowPort)
	 */
	@Override
	public void visitInputWorkflowPort(InputWorkflowPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode)
	 */
	@Override
	public void visitIterationStrategyNode(IterationStrategyNode bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyParent(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent)
	 */
	@Override
	public void visitIterationStrategyParent(IterationStrategyParent bean) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyStack(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack)
	 */
	@Override
	public void visitIterationStrategyStack(IterationStrategyStack bean) {
		Processor parent = bean.getParent();
		Set<Port> mentionedPorts = new HashSet<Port>();
		if (parent != null) {
			if (checkComplete) {
				for (IterationStrategyTopNode node : bean) {
					mentionedPorts.addAll(getReferencedPorts(node));
				}
				NamedSet<InputProcessorPort> inputPorts = parent.getInputPorts();
				if (inputPorts != null) {
					for (Port p : inputPorts) {
						if (!mentionedPorts.contains(p)) {
							listener.portMissingFromIterationStrategyStack(p,
									bean);
						}
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyTopNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode)
	 */
	@Override
	public void visitIterationStrategyTopNode(IterationStrategyTopNode bean) {
		if (checkComplete) {
			if (bean.isEmpty()) {
				listener.emptyIterationStrategyTopNode(bean);
			}
		}
		Map<Port, IterationStrategyNode> portsSoFar = new HashMap<Port, IterationStrategyNode>();
		for (IterationStrategyNode subNode : bean) {
			if (subNode instanceof PortNode) {
				InputProcessorPort port = ((PortNode) subNode).getInputProcessorPort();
				if (port != null) {
					if (portsSoFar.containsKey(port)) {
						listener.portMentionedTwice(portsSoFar.get(port), subNode);
					} else {
						portsSoFar.put(port, subNode);
					}
				}
			} else {
				Set<Port> portsForSubNode = getReferencedPorts((IterationStrategyTopNode) subNode);
				for (Port p : portsForSubNode) {
					if (portsSoFar.containsKey(p)) {
						listener.portMentionedTwice(portsSoFar.get(p), subNode);
					} else {
						portsSoFar.put(p, subNode);
					}
				}
			}
			
		}
	}
	
	private Set<Port> getReferencedPorts(IterationStrategyTopNode bean) {
		Set<Port> result = new HashSet<Port>();
		for (IterationStrategyNode subNode : bean) {
			if (subNode instanceof PortNode) {
				InputProcessorPort port = ((PortNode) subNode).getInputProcessorPort();
				if (port != null) {
					result.add(port);
				}
			} else {
				result.addAll(getReferencedPorts((IterationStrategyTopNode) subNode));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitNamed(uk.org.taverna.scufl2.api.common.Named)
	 */
	@Override
	public void visitNamed(Named bean) {
		// What are the constraints upon the string used as the name?
		
		if (checkComplete) {
			String name = bean.getName();
			if ((name == null) || name.isEmpty()) {
				listener.nullField(bean, "name");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputActivityPort(uk.org.taverna.scufl2.api.port.OutputActivityPort)
	 */
	@Override
	public void visitOutputActivityPort(OutputActivityPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputPort(uk.org.taverna.scufl2.api.port.OutputPort)
	 */
	@Override
	public void visitOutputPort(OutputPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputProcessorPort(uk.org.taverna.scufl2.api.port.OutputProcessorPort)
	 */
	@Override
	public void visitOutputProcessorPort(OutputProcessorPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputWorkflowPort(uk.org.taverna.scufl2.api.port.OutputWorkflowPort)
	 */
	@Override
	public void visitOutputWorkflowPort(OutputWorkflowPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPort(uk.org.taverna.scufl2.api.port.Port)
	 */
	@Override
	public void visitPort(Port bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPortNode(uk.org.taverna.scufl2.api.iterationstrategy.PortNode)
	 */
	@Override
	public void visitPortNode(PortNode bean) {
		
		InputProcessorPort inputProcessorPort = bean.getInputProcessorPort();
		Integer desiredDepth = bean.getDesiredDepth();
		if (desiredDepth != null) {
			if (desiredDepth < 0) {
				listener.negativeValue(bean, "desiredDepth", desiredDepth);
			}
		}
		
		if (inputProcessorPort != null) {
			Processor ancestralProcessor = findAncestral(bean, Processor.class);
			Processor portAncestralProcessor = findAncestral(
					inputProcessorPort, Processor.class);
			if ((ancestralProcessor == null)
					|| !ancestralProcessor.equals(portAncestralProcessor)) {
				listener.outOfScopeValue(bean, "inputProcessorPort",
						inputProcessorPort);
			}
		}
		
		if (checkComplete) {
			if (inputProcessorPort == null) {
				listener.nullField(bean, "inputProcessorPort");
			}
			if (desiredDepth == null) {
				listener.nullField(bean, "desiredDepth");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPorted(uk.org.taverna.scufl2.api.common.Ported)
	 */
	@Override
	public void visitPorted(Ported bean) {
		if (checkComplete) {
			if (bean.getInputPorts() == null) {
				listener.nullField(bean, "inputPorts");
			}
			if (bean.getOutputPorts() == null) {
				listener.nullField(bean, "outputPorts");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessor(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void visitProcessor(Processor bean) {
		if (checkComplete) {
			if (bean.getIterationStrategyStack() == null) {
				listener.nullField(bean, "iterationStrategyStack");
			}
			if (bean.getDispatchStack() == null) {
				listener.nullField(bean, "dispatchStack");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorBinding(uk.org.taverna.scufl2.api.profiles.ProcessorBinding)
	 */
	@Override
	public void visitProcessorBinding(ProcessorBinding bean) {
		Processor boundProcessor = bean.getBoundProcessor();
		Activity boundActivity = bean.getBoundActivity();
		WorkflowBundle workflowBundle = findAncestral(bean,
				WorkflowBundle.class);

		if (boundProcessor != null) {
			WorkflowBundle boundProcessorBundle = findAncestral(boundProcessor,
					WorkflowBundle.class);
			if ((workflowBundle == null)
					|| !workflowBundle.equals(boundProcessorBundle)) {
				listener.outOfScopeValue(bean, "boundProcessor", boundProcessor);
			}
		}
		if (boundActivity != null) {
			WorkflowBundle boundActivityBundle = findAncestral(boundActivity,
					WorkflowBundle.class);
			if ((workflowBundle == null)
					|| !workflowBundle.equals(boundActivityBundle)) {
				listener.outOfScopeValue(bean, "boundActivity", boundActivity);
			}
		}

		Integer activityPosition = bean.getActivityPosition();
		if (activityPosition != null) {
			if (activityPosition < 0) {
				listener.negativeValue(bean, "activityPosition", activityPosition);
			}
		}
		if (checkComplete) {
			if (boundProcessor == null) {
				listener.nullField(bean, "boundProcessor");
			}
			if (boundActivity == null) {
				listener.nullField(bean, "boundActivity");
			}
			// ActivityPosition can be null
			if (bean.getInputPortBindings() == null) {
				listener.nullField(bean, "inputPortBindings");
			}
			if (bean.getOutputPortBindings() == null) {
				listener.nullField(bean, "outputPortBindings");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorInputPortBinding(uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding)
	 */
	@Override
	public void visitProcessorInputPortBinding(ProcessorInputPortBinding bean) {
		ProcessorBinding parent = bean.getParent();
		InputProcessorPort boundProcessorPort = bean.getBoundProcessorPort();
		InputActivityPort boundActivityPort = bean.getBoundActivityPort();
		
		if (parent != null) {
			Processor boundProcessor = parent.getBoundProcessor();
			if (boundProcessorPort != null) {
				Processor boundPortProcessor = findAncestral(boundProcessorPort, Processor.class);
				if ((boundProcessor == null) || !boundProcessor.equals(boundPortProcessor)) {
					listener.outOfScopeValue(bean, "boundProcessorPort", boundProcessorPort);					
				}
			}
			Activity boundActivity = parent.getBoundActivity();
			if (boundActivityPort != null) {
				Activity boundPortActivity = findAncestral(boundActivityPort, Activity.class);
				if ((boundActivity == null) || !boundActivity.equals(boundPortActivity)) {
					listener.outOfScopeValue(bean, "boundActivityPort", boundActivityPort);
				}
			}
		}
		if (checkComplete) {
			if (boundProcessorPort == null) {
				listener.nullField(bean, "boundProcessorPort");
			}
			if (boundActivityPort == null) {
				listener.nullField(bean, "boundActivityPort");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorOutputPortBinding(uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding)
	 */
	@Override
	public void visitProcessorOutputPortBinding(ProcessorOutputPortBinding bean) {
		ProcessorBinding parent = bean.getParent();
		OutputProcessorPort boundProcessorPort = bean.getBoundProcessorPort();
		OutputActivityPort boundActivityPort = bean.getBoundActivityPort();
		
		if (parent != null) {
			Processor boundProcessor = parent.getBoundProcessor();
			if (boundProcessorPort != null) {
				Processor boundPortProcessor = findAncestral(boundProcessorPort, Processor.class);
				if ((boundProcessor == null) || !boundProcessor.equals(boundPortProcessor)) {
					listener.outOfScopeValue(bean, "boundProcessorPort", boundProcessorPort);					
				}
			}
			Activity boundActivity = parent.getBoundActivity();
			if (boundActivityPort != null) {
				Activity boundPortActivity = findAncestral(boundActivityPort, Activity.class);
				if ((boundActivity == null) || !boundActivity.equals(boundPortActivity)) {
					listener.outOfScopeValue(bean, "boundActivityPort", boundActivityPort);
				}
			}
		}
		if (checkComplete) {
			if (boundProcessorPort == null) {
				listener.nullField(bean, "boundProcessorPort");
			}
			if (boundActivityPort == null) {
				listener.nullField(bean, "boundActivityPort");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorPort(uk.org.taverna.scufl2.api.port.ProcessorPort)
	 */
	@Override
	public void visitProcessorPort(ProcessorPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorPortBinding(uk.org.taverna.scufl2.api.profiles.ProcessorPortBinding)
	 */
	@Override
	public void visitProcessorPortBinding(ProcessorPortBinding bean) {
		// Done in sub-classes

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProfile(uk.org.taverna.scufl2.api.profiles.Profile)
	 */
	@Override
	public void visitProfile(Profile bean) {
		Integer profilePosition = bean.getProfilePosition();
		
		if (profilePosition != null) {
			if (profilePosition < 0) {
				listener.negativeValue(bean, "profilePosition", profilePosition);
			}
		}
		if (checkComplete) {
			if (bean.getProcessorBindings() == null) {
				listener.nullField(bean, "processorBindings");
			}
			if (bean.getConfigurations() == null) {
				listener.nullField(bean, "configurations");
			}
			// It may be OK for the profilePosition to be null
			if (bean.getActivities() == null) {
				listener.nullField(bean, "activities");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPropertyObject(uk.org.taverna.scufl2.api.property.PropertyObject)
	 */
	@Override
	public void visitPropertyObject(PropertyObject bean) {
		// TODO
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPropertyVisit(uk.org.taverna.scufl2.api.property.PropertyResource.PropertyVisit)
	 */
	@Override
	public void visitPropertyVisit(PropertyVisit bean) {
		// TODO
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitReceiverPort(uk.org.taverna.scufl2.api.port.ReceiverPort)
	 */
	@Override
	public void visitReceiverPort(ReceiverPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitRoot(uk.org.taverna.scufl2.api.common.Root)
	 */
	@Override
	public void visitRoot(Root bean) {
		URI globalBaseURI = bean.getGlobalBaseURI();
		if (globalBaseURI != null) {
			if (!globalBaseURI.isAbsolute()) {
				listener.nonAbsoluteURI(bean);
			}
			else if (globalBaseURI.getScheme().equals("file")) {
				listener.nonAbsoluteURI(bean);
			}
		}
		if (checkComplete) {
			if (globalBaseURI == null) {
				listener.nullField(bean, "globalBaseURI");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitSenderPort(uk.org.taverna.scufl2.api.port.SenderPort)
	 */
	@Override
	public void visitSenderPort(SenderPort bean) {
		// All done in superclasses and interfaces
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitTyped(uk.org.taverna.scufl2.api.common.Typed)
	 */
	@Override
	public void visitTyped(Typed bean) {
		URI configurableType = bean.getConfigurableType();
		if (configurableType != null) {
			if (!configurableType.isAbsolute()) {
				listener.nonAbsoluteURI(bean);
			}
			else if (configurableType.getScheme().equals("file")) {
				listener.nonAbsoluteURI(bean);
			}
		}
		if (checkComplete) {
			if (configurableType == null) {
				listener.nullField(bean, "configurableType");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitWorkflow(uk.org.taverna.scufl2.api.core.Workflow)
	 */
	@Override
	public void visitWorkflow(Workflow bean) {
		Set<DataLink> dataLinks = bean.getDataLinks();
		Set<ControlLink> controlLinks = bean.getControlLinks();
		
		// ports are done in Ported
		
		NamedSet<Processor> processors = bean.getProcessors();
		URI workflowIdentifier = bean.getWorkflowIdentifier();
		
		if (workflowIdentifier != null) {
			if (!workflowIdentifier.isAbsolute()) {
				listener.nonAbsoluteURI(bean);
			}
			else if (workflowIdentifier.getScheme().equals("file")) {
				listener.nonAbsoluteURI(bean);
			}
		}
		
		if (checkComplete) {
			if (dataLinks == null) {
				listener.nullField(bean, "dataLinks");
			}
			if (controlLinks == null) {
				listener.nullField(bean, "controlLinks");
			}
			if (processors == null) {
				listener.nullField(bean, "processors");
			}
			if (workflowIdentifier == null) {
				listener.nullField(bean, "workflowIdentifier");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitWorkflowBundle(uk.org.taverna.scufl2.api.container.WorkflowBundle)
	 */
	@Override
	public void visitWorkflowBundle(WorkflowBundle bean) {
		NamedSet<Profile> profiles = bean.getProfiles();
		NamedSet<Workflow> workflows = bean.getWorkflows();
		Workflow mainWorkflow = bean.getMainWorkflow();
		Profile mainProfile = bean.getMainProfile();
		
		if ((profiles != null) && (mainProfile != null)) {
			if (!profiles.contains(mainProfile)) {
				listener.outOfScopeValue(bean, "mainProfile", mainProfile);
			}
		}
		if ((workflows != null) && (mainWorkflow != null)) {
			if (!workflows.contains(mainWorkflow)) {
				listener.outOfScopeValue(bean, "mainWorkflow", mainWorkflow);
			}
		}
		
		if (checkComplete) {
			if (profiles == null) {
				listener.nullField(bean, "profiles");
			}
			if (workflows == null) {
				listener.nullField(bean, "workflows");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitWorkflowPort(uk.org.taverna.scufl2.api.port.WorkflowPort)
	 */
	@Override
	public void visitWorkflowPort(WorkflowPort bean) {
		// All done in superclasses and interfaces
	}



}
