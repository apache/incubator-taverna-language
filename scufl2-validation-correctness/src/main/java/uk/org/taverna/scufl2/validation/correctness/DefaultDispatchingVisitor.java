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

/**
 * 
 */

/**
 * @author alanrw
 *
 */
public class DefaultDispatchingVisitor extends DispatchingVisitor {

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitActivity(uk.org.taverna.scufl2.api.activity.Activity)
	 */
	@Override
	public void visitActivity(Activity bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitActivityPort(uk.org.taverna.scufl2.api.port.ActivityPort)
	 */
	@Override
	public void visitActivityPort(ActivityPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitBlockingControlLink(uk.org.taverna.scufl2.api.core.BlockingControlLink)
	 */
	@Override
	public void visitBlockingControlLink(BlockingControlLink bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitChild(uk.org.taverna.scufl2.api.common.Child)
	 */
	@Override
	public void visitChild(Child bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitConfigurable(uk.org.taverna.scufl2.api.common.Configurable)
	 */
	@Override
	public void visitConfigurable(Configurable bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitConfiguration(uk.org.taverna.scufl2.api.configurations.Configuration)
	 */
	@Override
	public void visitConfiguration(Configuration bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitControlLink(uk.org.taverna.scufl2.api.core.ControlLink)
	 */
	@Override
	public void visitControlLink(ControlLink bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitCrossProduct(uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct)
	 */
	@Override
	public void visitCrossProduct(CrossProduct bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDataLink(uk.org.taverna.scufl2.api.core.DataLink)
	 */
	@Override
	public void visitDataLink(DataLink bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDispatchStack(uk.org.taverna.scufl2.api.dispatchstack.DispatchStack)
	 */
	@Override
	public void visitDispatchStack(DispatchStack bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDispatchStackLayer(uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer)
	 */
	@Override
	public void visitDispatchStackLayer(DispatchStackLayer bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitDotProduct(uk.org.taverna.scufl2.api.iterationstrategy.DotProduct)
	 */
	@Override
	public void visitDotProduct(DotProduct bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputActivityPort(uk.org.taverna.scufl2.api.port.InputActivityPort)
	 */
	@Override
	public void visitInputActivityPort(InputActivityPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputPort(uk.org.taverna.scufl2.api.port.InputPort)
	 */
	@Override
	public void visitInputPort(InputPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputProcessorPort(uk.org.taverna.scufl2.api.port.InputProcessorPort)
	 */
	@Override
	public void visitInputProcessorPort(InputProcessorPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitInputWorkflowPort(uk.org.taverna.scufl2.api.port.InputWorkflowPort)
	 */
	@Override
	public void visitInputWorkflowPort(InputWorkflowPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode)
	 */
	@Override
	public void visitIterationStrategyNode(IterationStrategyNode bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyParent(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent)
	 */
	@Override
	public void visitIterationStrategyParent(IterationStrategyParent bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyStack(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack)
	 */
	@Override
	public void visitIterationStrategyStack(IterationStrategyStack bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitIterationStrategyTopNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode)
	 */
	@Override
	public void visitIterationStrategyTopNode(IterationStrategyTopNode bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitNamed(uk.org.taverna.scufl2.api.common.Named)
	 */
	@Override
	public void visitNamed(Named bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputActivityPort(uk.org.taverna.scufl2.api.port.OutputActivityPort)
	 */
	@Override
	public void visitOutputActivityPort(OutputActivityPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputPort(uk.org.taverna.scufl2.api.port.OutputPort)
	 */
	@Override
	public void visitOutputPort(OutputPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputProcessorPort(uk.org.taverna.scufl2.api.port.OutputProcessorPort)
	 */
	@Override
	public void visitOutputProcessorPort(OutputProcessorPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitOutputWorkflowPort(uk.org.taverna.scufl2.api.port.OutputWorkflowPort)
	 */
	@Override
	public void visitOutputWorkflowPort(OutputWorkflowPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPort(uk.org.taverna.scufl2.api.port.Port)
	 */
	@Override
	public void visitPort(Port bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPortNode(uk.org.taverna.scufl2.api.iterationstrategy.PortNode)
	 */
	@Override
	public void visitPortNode(PortNode bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitPorted(uk.org.taverna.scufl2.api.common.Ported)
	 */
	@Override
	public void visitPorted(Ported bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessor(uk.org.taverna.scufl2.api.core.Processor)
	 */
	@Override
	public void visitProcessor(Processor bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorBinding(uk.org.taverna.scufl2.api.profiles.ProcessorBinding)
	 */
	@Override
	public void visitProcessorBinding(ProcessorBinding bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorInputPortBinding(uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding)
	 */
	@Override
	public void visitProcessorInputPortBinding(ProcessorInputPortBinding bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorOutputPortBinding(uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding)
	 */
	@Override
	public void visitProcessorOutputPortBinding(ProcessorOutputPortBinding bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorPort(uk.org.taverna.scufl2.api.port.ProcessorPort)
	 */
	@Override
	public void visitProcessorPort(ProcessorPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProcessorPortBinding(uk.org.taverna.scufl2.api.profiles.ProcessorPortBinding)
	 */
	@Override
	public void visitProcessorPortBinding(ProcessorPortBinding bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitProfile(uk.org.taverna.scufl2.api.profiles.Profile)
	 */
	@Override
	public void visitProfile(Profile bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitReceiverPort(uk.org.taverna.scufl2.api.port.ReceiverPort)
	 */
	@Override
	public void visitReceiverPort(ReceiverPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitRoot(uk.org.taverna.scufl2.api.common.Root)
	 */
	@Override
	public void visitRoot(Root bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitSenderPort(uk.org.taverna.scufl2.api.port.SenderPort)
	 */
	@Override
	public void visitSenderPort(SenderPort bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitTyped(uk.org.taverna.scufl2.api.common.Typed)
	 */
	@Override
	public void visitTyped(Typed bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitWorkflow(uk.org.taverna.scufl2.api.core.Workflow)
	 */
	@Override
	public void visitWorkflow(Workflow bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitWorkflowBundle(uk.org.taverna.scufl2.api.container.WorkflowBundle)
	 */
	@Override
	public void visitWorkflowBundle(WorkflowBundle bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.DispatchingVisitor#visitWorkflowPort(uk.org.taverna.scufl2.api.port.WorkflowPort)
	 */
	@Override
	public void visitWorkflowPort(WorkflowPort bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitAbstractDepthPort(AbstractDepthPort bean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAbstractGranularDepthPort(AbstractGranularDepthPort bean) {
		// TODO Auto-generated method stub
		
	}

}
