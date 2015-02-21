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


import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.common.Root;
import org.apache.taverna.scufl2.api.common.Typed;
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

/**
 * @author alanrw
 */
public class DefaultDispatchingVisitor extends DispatchingVisitor {
	@Override
	public void visitActivity(Activity bean) {
	}

	@Override
	public void visitActivityPort(ActivityPort bean) {
	}

	@Override
	public void visitBlockingControlLink(BlockingControlLink bean) {
	}

	@Override
	public void visitChild(Child<?> bean) {
	}

	@Override
	public void visitConfigurable(Configurable bean) {
	}

	@Override
	public void visitConfiguration(Configuration bean) {
	}

	@Override
	public void visitControlLink(ControlLink bean) {
	}

	@Override
	public void visitCrossProduct(CrossProduct bean) {
	}

	@Override
	public void visitDataLink(DataLink bean) {
	}

	@Override
	public void visitDotProduct(DotProduct bean) {
	}

	@Override
	public void visitInputActivityPort(InputActivityPort bean) {
	}

	@Override
	public void visitInputPort(InputPort bean) {
	}

	@Override
	public void visitInputProcessorPort(InputProcessorPort bean) {
	}

	@Override
	public void visitInputWorkflowPort(InputWorkflowPort bean) {
	}

	@Override
	public void visitIterationStrategyNode(IterationStrategyNode bean) {
	}

	@Override
	public void visitIterationStrategyParent(IterationStrategyParent bean) {
	}

	@Override
	public void visitIterationStrategyStack(IterationStrategyStack bean) {
	}

	@Override
	public void visitIterationStrategyTopNode(IterationStrategyTopNode bean) {
	}

	@Override
	public void visitNamed(Named bean) {
	}

	@Override
	public void visitOutputActivityPort(OutputActivityPort bean) {
	}

	@Override
	public void visitOutputPort(OutputPort bean) {
	}

	@Override
	public void visitOutputProcessorPort(OutputProcessorPort bean) {
	}

	@Override
	public void visitOutputWorkflowPort(OutputWorkflowPort bean) {
	}

	@Override
	public void visitPort(Port bean) {
	}

	@Override
	public void visitPortNode(PortNode bean) {
	}

	@Override
	public void visitPorted(Ported bean) {
	}

	@Override
	public void visitProcessor(Processor bean) {
	}

	@Override
	public void visitProcessorBinding(ProcessorBinding bean) {
	}

	@Override
	public void visitProcessorInputPortBinding(ProcessorInputPortBinding bean) {
	}

	@Override
	public void visitProcessorOutputPortBinding(ProcessorOutputPortBinding bean) {
	}

	@Override
	public void visitProcessorPort(ProcessorPort bean) {
	}

	@Override
	public void visitProcessorPortBinding(ProcessorPortBinding<?, ?> bean) {
	}

	@Override
	public void visitProfile(Profile bean) {
	}

	@Override
	public void visitReceiverPort(ReceiverPort bean) {
	}

	@Override
	public void visitRoot(Root bean) {
	}

	@Override
	public void visitSenderPort(SenderPort bean) {
	}

	@Override
	public void visitTyped(Typed bean) {
	}

	@Override
	public void visitWorkflow(Workflow bean) {
	}

	@Override
	public void visitWorkflowBundle(WorkflowBundle bean) {
	}

	@Override
	public void visitWorkflowPort(WorkflowPort bean) {
	}

	@Override
	public void visitAbstractDepthPort(AbstractDepthPort bean) {
	}

	@Override
	public void visitAbstractGranularDepthPort(AbstractGranularDepthPort bean) {
	}
}
