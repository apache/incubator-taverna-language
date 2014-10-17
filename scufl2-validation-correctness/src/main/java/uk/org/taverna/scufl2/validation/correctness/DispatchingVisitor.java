/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
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
public abstract class DispatchingVisitor extends Visitor.VisitorWithPath {
	@Override
	public boolean visit() {
		WorkflowBean bean = this.getCurrentNode();

		// First, the interfaces
		if (bean instanceof AbstractDepthPort)
			visitAbstractDepthPort((AbstractDepthPort) bean);
		if (bean instanceof AbstractGranularDepthPort)
			visitAbstractGranularDepthPort((AbstractGranularDepthPort) bean);
		if (bean instanceof ActivityPort)
			visitActivityPort((ActivityPort) bean);
		if (bean instanceof Child)
			visitChild((Child<?>) bean);
		if (bean instanceof Configurable)
			visitConfigurable((Configurable) bean);
		if (bean instanceof ControlLink)
			visitControlLink((ControlLink) bean);
		if (bean instanceof InputPort)
			visitInputPort((InputPort) bean);
		if (bean instanceof IterationStrategyNode)
			visitIterationStrategyNode((IterationStrategyNode) bean);
		if (bean instanceof IterationStrategyParent)
			visitIterationStrategyParent((IterationStrategyParent) bean);
		if (bean instanceof IterationStrategyTopNode)
			visitIterationStrategyTopNode((IterationStrategyTopNode) bean);
		if (bean instanceof Named)
			visitNamed((Named) bean);
		if (bean instanceof OutputPort)
			visitOutputPort((OutputPort) bean);
		if (bean instanceof Port)
			visitPort((Port) bean);
		if (bean instanceof Ported)
			visitPorted((Ported) bean);
		if (bean instanceof ProcessorPort)
			visitProcessorPort((ProcessorPort) bean);
		if (bean instanceof ProcessorPortBinding)
			visitProcessorPortBinding((ProcessorPortBinding<?,?>) bean);
		if (bean instanceof ReceiverPort)
			visitReceiverPort((ReceiverPort) bean);
		if (bean instanceof Root)
			visitRoot((Root) bean);
		if (bean instanceof SenderPort)
			visitSenderPort((SenderPort) bean);
		if (bean instanceof Typed)
			visitTyped((Typed) bean);
		if (bean instanceof WorkflowPort)
			visitWorkflowPort((WorkflowPort) bean);
		
		// Now for the classes; these are mutually exclusive
		if (bean instanceof Activity)
			visitActivity((Activity) bean);
		else if (bean instanceof BlockingControlLink)
			visitBlockingControlLink((BlockingControlLink) bean);
		else if (bean instanceof Configuration)
			visitConfiguration((Configuration) bean);
		else if (bean instanceof CrossProduct)
			visitCrossProduct((CrossProduct) bean);
		else if (bean instanceof DataLink)
			visitDataLink((DataLink) bean);
		else if (bean instanceof DotProduct)
			visitDotProduct((DotProduct) bean);
		else if (bean instanceof InputActivityPort)
			visitInputActivityPort((InputActivityPort) bean);
		else if (bean instanceof InputProcessorPort)
			visitInputProcessorPort((InputProcessorPort) bean);
		else if (bean instanceof InputWorkflowPort)
			visitInputWorkflowPort((InputWorkflowPort) bean);
		else if (bean instanceof IterationStrategyStack)
			visitIterationStrategyStack((IterationStrategyStack) bean);
		else if (bean instanceof OutputActivityPort)
			visitOutputActivityPort((OutputActivityPort) bean);
		else if (bean instanceof OutputProcessorPort)
			visitOutputProcessorPort((OutputProcessorPort) bean);
		else if (bean instanceof OutputWorkflowPort)
			visitOutputWorkflowPort((OutputWorkflowPort) bean);
		else if (bean instanceof PortNode)
			visitPortNode((PortNode) bean);
		else if (bean instanceof Processor)
			visitProcessor((Processor) bean);
		else if (bean instanceof ProcessorBinding)
			visitProcessorBinding((ProcessorBinding) bean);
		else if (bean instanceof ProcessorInputPortBinding)
			visitProcessorInputPortBinding((ProcessorInputPortBinding) bean);
		else if (bean instanceof ProcessorOutputPortBinding)
			visitProcessorOutputPortBinding((ProcessorOutputPortBinding) bean);
		else if (bean instanceof Profile)
			visitProfile((Profile) bean);
		else if (bean instanceof Workflow)
			visitWorkflow((Workflow) bean);
		else if (bean instanceof WorkflowBundle)
			visitWorkflowBundle((WorkflowBundle) bean);
		return true;
	}

	protected abstract void visitAbstractGranularDepthPort(AbstractGranularDepthPort bean);

	protected abstract void visitAbstractDepthPort(AbstractDepthPort bean);

	protected abstract void visitWorkflowBundle(WorkflowBundle bean);

	protected abstract void visitWorkflow(Workflow bean);

	protected abstract void visitProfile(Profile bean);

	protected abstract void visitProcessorOutputPortBinding(ProcessorOutputPortBinding bean);

	protected abstract void visitProcessorInputPortBinding(ProcessorInputPortBinding bean);

	protected abstract void visitProcessorBinding(ProcessorBinding bean);

	protected abstract void visitProcessor(Processor bean);

	protected abstract void visitPortNode(PortNode bean);

	protected abstract void visitOutputWorkflowPort(OutputWorkflowPort bean);

	protected abstract void visitOutputProcessorPort(OutputProcessorPort bean);

	protected abstract void visitOutputActivityPort(OutputActivityPort bean);

	protected abstract void visitIterationStrategyStack(IterationStrategyStack bean);

	protected abstract void visitInputWorkflowPort(InputWorkflowPort bean);

	protected abstract void visitInputProcessorPort(InputProcessorPort bean);

	protected abstract void visitInputActivityPort(InputActivityPort bean);

	protected abstract void visitDotProduct(DotProduct bean);

	protected abstract void visitDataLink(DataLink bean);

	protected abstract void visitCrossProduct(CrossProduct bean);

	protected abstract void visitConfiguration(Configuration bean);

	protected abstract void visitBlockingControlLink(BlockingControlLink bean);

	protected abstract void visitActivity(Activity bean);

	protected abstract void visitWorkflowPort(WorkflowPort bean);

	protected abstract void visitTyped(Typed bean);

	protected abstract void visitSenderPort(SenderPort bean);

	protected abstract void visitRoot(Root bean);

	protected abstract void visitReceiverPort(ReceiverPort bean);

	protected abstract void visitProcessorPortBinding(ProcessorPortBinding<?,?> bean);

	protected abstract void visitProcessorPort(ProcessorPort bean);

	protected abstract void visitPorted(Ported bean);

	protected abstract void visitPort(Port bean);

	protected abstract void visitOutputPort(OutputPort bean);

	protected abstract void visitNamed(Named bean);

	protected abstract void visitIterationStrategyTopNode(IterationStrategyTopNode bean);

	protected abstract void visitIterationStrategyParent(IterationStrategyParent bean);

	protected abstract void visitIterationStrategyNode(IterationStrategyNode bean);

	protected abstract void visitInputPort(InputPort bean);

	protected abstract void visitControlLink(ControlLink bean);

	protected abstract void visitConfigurable(Configurable bean);

	protected abstract void visitChild(Child<?> bean);

	protected abstract void visitActivityPort(ActivityPort bean);
}
