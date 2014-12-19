package uk.org.taverna.scufl2.validation.correctness;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
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
