/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness;
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


import java.net.URI;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.common.Root;
import org.apache.taverna.scufl2.api.common.Typed;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.AbstractDepthPort;
import org.apache.taverna.scufl2.api.port.AbstractGranularDepthPort;
import org.apache.taverna.scufl2.api.port.ActivityPort;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.api.port.ProcessorPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.api.port.WorkflowPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * @author alanrw
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
	
	public <T> T findAncestral(Child<?> bean, Class<T> class1) {
		T result = null;
		for (WorkflowBean parent = bean.getParent(); parent != null; parent = ((Child<?>) parent)
				.getParent()) {
			if (class1.isInstance(parent))
				return class1.cast(parent);
			if (!(parent instanceof Child))
				return null;
		}
		return result;
	}

	@Override
	public void visitAbstractDepthPort(AbstractDepthPort bean) {
		Integer depth = bean.getDepth();
		if (depth != null && depth < 0)
			listener.negativeValue(bean, "depth", depth);
		if (checkComplete && depth == null)
			listener.nullField(bean, "depth");
	}

	@Override
	public void visitAbstractGranularDepthPort(AbstractGranularDepthPort bean) {
		Integer granularDepth = bean.getGranularDepth();
		if (granularDepth != null) {
			if (granularDepth < 0)
				listener.negativeValue(bean, "granularDepth", granularDepth);
			Integer depth = bean.getDepth();
			if (depth != null)
				if (granularDepth > depth)
					listener.incompatibleGranularDepth(bean, depth,
							granularDepth);
		}
		if (checkComplete && granularDepth == null)
			listener.nullField(bean, "granularDepth");
	}

	@Override
	public void visitActivity(Activity bean) {
		// All checks are covered by those in Named, Typed, Child.
	}

	@Override
	public void visitActivityPort(ActivityPort bean) {
		// All checks are covered by those in Named and Child
	}

	@Override
	public void visitBlockingControlLink(BlockingControlLink bean) {
		// Also checks from Child
		
		Workflow parent = bean.getParent();
		Processor block = bean.getBlock();
		Processor untilFinished = bean.getUntilFinished();
		
		// Check the block and untilFinished processors are in the same workflow
		if (block != null) {
			Workflow blockParent = block.getParent();
			if ((parent == null) || !parent.equals(blockParent))
				listener.outOfScopeValue(bean, "block", block);
		}
		if (untilFinished != null) {
			Workflow untilFinishedParent = untilFinished.getParent();
			if ((parent == null) || !parent.equals(untilFinishedParent))
				listener.outOfScopeValue(bean, "untilFinished", untilFinished);
		}

		// Check the block and untilFinished processors are specified
		if (checkComplete) {
			if (block == null)
				listener.nullField(bean, "block");
			if (untilFinished == null)
				listener.nullField(bean, "untilFinished");
		}
	}

	@Override
	public void visitChild(Child<?> bean) {
		WorkflowBean p = bean.getParent();
		try {
			if (p != null) {
				WorkflowBean up = peekPath();
				if ((up != null) && (up != p))
					listener.wrongParent(bean);
			}
		} catch (EmptyStackException e) {
			// Nothing
		}
		if (checkComplete && p == null)
			listener.nullField(bean, "parent");
	}

	@Override
	public void visitConfigurable(Configurable bean) {
		// Are there any checks that it is actually configured?
	}

	@Override
	public void visitConfiguration(Configuration bean) {
		Configurable configures = bean.getConfigures();
		JsonNode json = bean.getJson();
		@SuppressWarnings("unused")
		URI type = null;
		if (configures != null && configures instanceof Typed)
			type = ((Typed) configures).getType();
		// Correct check cannot be completed unless property descriptions are available
//		URI configurationType = bean.getConfigurableType();
//		if ((configuresType != null) && (configurationType != null))
//			if (!configuresType.equals(configurationType))
//				listener.mismatchConfigurableType(bean, configures);

		if (checkComplete) {
			if (configures == null)
				listener.nullField(bean, "configures");
			if (json == null)
				listener.nullField(bean, "json");
			// TODO Check that the PropertyResource is complete
		}
	}

	@Override
	public void visitControlLink(ControlLink bean) {
		// All done in Child or BlockingControlLink
	}

	@Override
	public void visitCrossProduct(CrossProduct bean) {
		// All done in IterationStrategyTopNode and Child
	}

	@Override
	public void visitDataLink(DataLink bean) {
		ReceiverPort sendsTo = bean.getSendsTo();
		SenderPort receivesFrom = bean.getReceivesFrom();
		
		Workflow parent = bean.getParent();
		if (sendsTo != null) {
			Workflow sendsToWorkflow = findAncestral((Child<?>) sendsTo,
					Workflow.class);
			if ((parent == null) || !parent.equals(sendsToWorkflow))
				listener.outOfScopeValue(bean, "sendsTo", sendsTo);
		}
		if (receivesFrom != null) {
			Workflow receivesFromWorkflow = findAncestral((Child<?>) receivesFrom,
					Workflow.class);
			if ((parent == null) || !parent.equals(receivesFromWorkflow))
				listener.outOfScopeValue(bean, "receivesFrom", receivesFrom);
		}
		
		Integer mergePosition = bean.getMergePosition();
		if (mergePosition != null && mergePosition < 0)
			listener.negativeValue(bean, "mergePosition", mergePosition);
		
		// How to check mergePosition
		
		if (checkComplete) {
			if (sendsTo == null)
				listener.nullField(bean, "sendsTo");
			if (receivesFrom == null)
				listener.nullField(bean, "receivesFrom");
		}
	}

	@Override
	public void visitDotProduct(DotProduct bean) {
		// All done in IterationStrategyTopNode and Child
	}

	@Override
	public void visitInputActivityPort(InputActivityPort bean) {
		// All done in Child, Named and Configurable	
	}

	@Override
	public void visitInputPort(InputPort bean) {
		// All done in Named and Configurable
	}

	@Override
	public void visitInputProcessorPort(InputProcessorPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitInputWorkflowPort(InputWorkflowPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitIterationStrategyNode(IterationStrategyNode bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitIterationStrategyParent(IterationStrategyParent bean) {
		// Nothing to do
	}

	@Override
	public void visitIterationStrategyStack(IterationStrategyStack bean) {
		Processor parent = bean.getParent();
		if (parent != null && checkComplete) {
			Set<Port> mentionedPorts = new HashSet<>();
			for (IterationStrategyTopNode node : bean)
				mentionedPorts.addAll(getReferencedPorts(node));
			NamedSet<InputProcessorPort> inputPorts = parent.getInputPorts();
			if (inputPorts != null)
				for (Port p : inputPorts)
					if (!mentionedPorts.contains(p))
						listener.portMissingFromIterationStrategyStack(p, bean);
		}
	}

	@Override
	public void visitIterationStrategyTopNode(IterationStrategyTopNode bean) {
		if (checkComplete&&bean.isEmpty())
				listener.emptyIterationStrategyTopNode(bean);
		Map<Port, IterationStrategyNode> portsSoFar = new HashMap<>();
		for (IterationStrategyNode subNode : bean)
			if (subNode instanceof PortNode) {
				InputProcessorPort port = ((PortNode) subNode).getInputProcessorPort();
				if (port != null) {
					if (portsSoFar.containsKey(port))
						listener.portMentionedTwice(portsSoFar.get(port), subNode);
					else
						portsSoFar.put(port, subNode);
				}
			} else
				for (Port p : getReferencedPorts((IterationStrategyTopNode) subNode))
					if (portsSoFar.containsKey(p))
						listener.portMentionedTwice(portsSoFar.get(p), subNode);
					else
						portsSoFar.put(p, subNode);
	}
	
	private Set<Port> getReferencedPorts(IterationStrategyTopNode bean) {
		Set<Port> result = new HashSet<>();
		for (IterationStrategyNode subNode : bean)
			if (subNode instanceof PortNode) {
				InputProcessorPort port = ((PortNode) subNode).getInputProcessorPort();
				if (port != null)
					result.add(port);
			} else
				result.addAll(getReferencedPorts((IterationStrategyTopNode) subNode));
		return result;
	}

	@Override
	public void visitNamed(Named bean) {
		// What are the constraints upon the string used as the name?
		if (checkComplete) {
			String name = bean.getName();
			if ((name == null) || name.isEmpty())
				listener.nullField(bean, "name");
		}
	}

	@Override
	public void visitOutputActivityPort(OutputActivityPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitOutputPort(OutputPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitOutputProcessorPort(OutputProcessorPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitOutputWorkflowPort(OutputWorkflowPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitPort(Port bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitPortNode(PortNode bean) {
		InputProcessorPort inputProcessorPort = bean.getInputProcessorPort();
		Integer desiredDepth = bean.getDesiredDepth();
		if (desiredDepth != null && desiredDepth < 0)
			listener.negativeValue(bean, "desiredDepth", desiredDepth);
		
		if (inputProcessorPort != null) {
			Processor ancestralProcessor = findAncestral(bean, Processor.class);
			Processor portAncestralProcessor = findAncestral(
					inputProcessorPort, Processor.class);
			if ((ancestralProcessor == null)
					|| !ancestralProcessor.equals(portAncestralProcessor))
				listener.outOfScopeValue(bean, "inputProcessorPort",
						inputProcessorPort);
		}
		
		if (checkComplete) {
			if (inputProcessorPort == null)
				listener.nullField(bean, "inputProcessorPort");
			if (desiredDepth == null)
				listener.nullField(bean, "desiredDepth");
		}
	}

	@Override
	public void visitPorted(Ported bean) {
		if (checkComplete) {
			if (bean.getInputPorts() == null)
				listener.nullField(bean, "inputPorts");
			if (bean.getOutputPorts() == null)
				listener.nullField(bean, "outputPorts");
		}
	}

	@Override
	public void visitProcessor(Processor bean) {
		if (checkComplete && bean.getIterationStrategyStack() == null)
			listener.nullField(bean, "iterationStrategyStack");
	}

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
					|| !workflowBundle.equals(boundProcessorBundle))
				listener.outOfScopeValue(bean, "boundProcessor", boundProcessor);
		}
		if (boundActivity != null) {
			WorkflowBundle boundActivityBundle = findAncestral(boundActivity,
					WorkflowBundle.class);
			if ((workflowBundle == null)
					|| !workflowBundle.equals(boundActivityBundle))
				listener.outOfScopeValue(bean, "boundActivity", boundActivity);
		}

		Integer activityPosition = bean.getActivityPosition();
		if (activityPosition != null && activityPosition < 0)
			listener.negativeValue(bean, "activityPosition", activityPosition);

		if (checkComplete) {
			if (boundProcessor == null)
				listener.nullField(bean, "boundProcessor");
			if (boundActivity == null)
				listener.nullField(bean, "boundActivity");
			// ActivityPosition can be null
			if (bean.getInputPortBindings() == null)
				listener.nullField(bean, "inputPortBindings");
			if (bean.getOutputPortBindings() == null)
				listener.nullField(bean, "outputPortBindings");
		}
	}

	@Override
	public void visitProcessorInputPortBinding(ProcessorInputPortBinding bean) {
		ProcessorBinding parent = bean.getParent();
		InputProcessorPort boundProcessorPort = bean.getBoundProcessorPort();
		InputActivityPort boundActivityPort = bean.getBoundActivityPort();

		if (parent != null) {
			Processor boundProcessor = parent.getBoundProcessor();
			if (boundProcessorPort != null) {
				Processor boundPortProcessor = findAncestral(
						boundProcessorPort, Processor.class);
				if ((boundProcessor == null)
						|| !boundProcessor.equals(boundPortProcessor))
					listener.outOfScopeValue(bean, "boundProcessorPort",
							boundProcessorPort);
			}
			Activity boundActivity = parent.getBoundActivity();
			if (boundActivityPort != null) {
				Activity boundPortActivity = findAncestral(boundActivityPort,
						Activity.class);
				if ((boundActivity == null)
						|| !boundActivity.equals(boundPortActivity))
					listener.outOfScopeValue(bean, "boundActivityPort",
							boundActivityPort);
			}
		}
		if (checkComplete) {
			if (boundProcessorPort == null)
				listener.nullField(bean, "boundProcessorPort");
			if (boundActivityPort == null)
				listener.nullField(bean, "boundActivityPort");
		}
	}

	@Override
	public void visitProcessorOutputPortBinding(ProcessorOutputPortBinding bean) {
		ProcessorBinding parent = bean.getParent();
		OutputProcessorPort boundProcessorPort = bean.getBoundProcessorPort();
		OutputActivityPort boundActivityPort = bean.getBoundActivityPort();
		
		if (parent != null) {
			Processor boundProcessor = parent.getBoundProcessor();
			if (boundProcessorPort != null) {
				Processor boundPortProcessor = findAncestral(boundProcessorPort, Processor.class);
				if ((boundProcessor == null) || !boundProcessor.equals(boundPortProcessor))
					listener.outOfScopeValue(bean, "boundProcessorPort", boundProcessorPort);					
			}
			Activity boundActivity = parent.getBoundActivity();
			if (boundActivityPort != null) {
				Activity boundPortActivity = findAncestral(boundActivityPort, Activity.class);
				if ((boundActivity == null) || !boundActivity.equals(boundPortActivity))
					listener.outOfScopeValue(bean, "boundActivityPort", boundActivityPort);
			}
		}
		if (checkComplete) {
			if (boundProcessorPort == null)
				listener.nullField(bean, "boundProcessorPort");
			if (boundActivityPort == null)
				listener.nullField(bean, "boundActivityPort");
		}
	}

	@Override
	public void visitProcessorPort(ProcessorPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitProcessorPortBinding(ProcessorPortBinding<?,?> bean) {
		// Done in sub-classes
	}

	@Override
	public void visitProfile(Profile bean) {
		Integer profilePosition = bean.getProfilePosition();
		
		if (profilePosition != null && profilePosition < 0)
			listener.negativeValue(bean, "profilePosition", profilePosition);
		if (checkComplete) {
			if (bean.getProcessorBindings() == null)
				listener.nullField(bean, "processorBindings");
			if (bean.getConfigurations() == null)
				listener.nullField(bean, "configurations");
			// It may be OK for the profilePosition to be null
			if (bean.getActivities() == null)
				listener.nullField(bean, "activities");
		}
	}

	@Override
	public void visitReceiverPort(ReceiverPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitRoot(Root bean) {
		URI globalBaseURI = bean.getGlobalBaseURI();
		if (globalBaseURI != null) {
			if (!globalBaseURI.isAbsolute())
				listener.nonAbsoluteURI(bean, "globalBaseURI", globalBaseURI);
			else if (globalBaseURI.getScheme().equals("file"))
				listener.nonAbsoluteURI(bean, "globalBaseURI", globalBaseURI);
		}
		if (checkComplete && globalBaseURI == null)
			listener.nullField(bean, "globalBaseURI");
	}

	@Override
	public void visitSenderPort(SenderPort bean) {
		// All done in superclasses and interfaces
	}

	@Override
	public void visitTyped(Typed bean) {
		URI configurableType = bean.getType();
		if (configurableType != null) {
			if (!configurableType.isAbsolute())
				listener.nonAbsoluteURI(bean, "configurableType", configurableType);
			else if (configurableType.getScheme().equals("file"))
				listener.nonAbsoluteURI(bean, "configurableType", configurableType);
		}
		if (checkComplete && configurableType == null)
			listener.nullField(bean, "configurableType");
	}

	@Override
	public void visitWorkflow(Workflow bean) {
		Set<DataLink> dataLinks = bean.getDataLinks();
		Set<ControlLink> controlLinks = bean.getControlLinks();
		
		// ports are done in Ported
		
		NamedSet<Processor> processors = bean.getProcessors();
		URI workflowIdentifier = bean.getIdentifier();
		
		if (workflowIdentifier != null) {
			if (!workflowIdentifier.isAbsolute())
				listener.nonAbsoluteURI(bean, "workflowIdentifier", workflowIdentifier);
			else if (workflowIdentifier.getScheme().equals("file"))
				listener.nonAbsoluteURI(bean, "workflowIdentifier", workflowIdentifier);
		}
		
		if (checkComplete) {
			if (dataLinks == null)
				listener.nullField(bean, "dataLinks");
			if (controlLinks == null)
				listener.nullField(bean, "controlLinks");
			if (processors == null)
				listener.nullField(bean, "processors");
			if (workflowIdentifier == null)
				listener.nullField(bean, "workflowIdentifier");
		}
	}

	@Override
	public void visitWorkflowBundle(WorkflowBundle bean) {
		NamedSet<Profile> profiles = bean.getProfiles();
		NamedSet<Workflow> workflows = bean.getWorkflows();
		Workflow mainWorkflow = bean.getMainWorkflow();
		Profile mainProfile = bean.getMainProfile();
		
		if ((profiles != null) && (mainProfile != null)
				&& !profiles.contains(mainProfile))
			listener.outOfScopeValue(bean, "mainProfile", mainProfile);
		if ((workflows != null) && (mainWorkflow != null)
				&& !workflows.contains(mainWorkflow))
			listener.outOfScopeValue(bean, "mainWorkflow", mainWorkflow);
		
		if (checkComplete) {
			if (profiles == null)
				listener.nullField(bean, "profiles");
			if (workflows == null)
				listener.nullField(bean, "workflows");
		}
	}

	@Override
	public void visitWorkflowPort(WorkflowPort bean) {
		// All done in superclasses and interfaces
	}
}
