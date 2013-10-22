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
 *
 */
public abstract class DispatchingVisitor extends Visitor.VisitorWithPath {

	@Override
	public boolean visit() {
		WorkflowBean bean = this.getCurrentNode();
		if (bean instanceof AbstractDepthPort) {
			visitAbstractDepthPort((AbstractDepthPort) bean);
		}
		if (bean instanceof AbstractGranularDepthPort) {
			visitAbstractGranularDepthPort((AbstractGranularDepthPort) bean);
		}
		if (bean instanceof ActivityPort) {
			visitActivityPort((ActivityPort) bean);
		}
		if (bean instanceof Child) {
			visitChild((Child) bean);
		}
		if (bean instanceof Configurable) {
			visitConfigurable((Configurable) bean);
		}
		if (bean instanceof ControlLink) {
			visitControlLink((ControlLink) bean);
		}
		if (bean instanceof InputPort) {
			visitInputPort((InputPort) bean);
		}
		if (bean instanceof IterationStrategyNode) {
			visitIterationStrategyNode((IterationStrategyNode) bean);
		}
		if (bean instanceof IterationStrategyParent) {
			visitIterationStrategyParent((IterationStrategyParent) bean);
		}
		if (bean instanceof IterationStrategyTopNode) {
			visitIterationStrategyTopNode((IterationStrategyTopNode) bean);
		}
		if (bean instanceof Named) {
			visitNamed((Named) bean);
		}
		if (bean instanceof OutputPort) {
			visitOutputPort((OutputPort) bean);
		}
		if (bean instanceof Port) {
			visitPort((Port) bean);
		}
		if (bean instanceof Ported) {
			visitPorted((Ported) bean);
		}
		if (bean instanceof ProcessorPort) {
			visitProcessorPort((ProcessorPort) bean);
		}
		if (bean instanceof ProcessorPortBinding) {
			visitProcessorPortBinding((ProcessorPortBinding) bean);
		}
		if (bean instanceof ReceiverPort) {
			visitReceiverPort((ReceiverPort) bean);
		}
		if (bean instanceof Root) {
			visitRoot((Root) bean);
		}
		if (bean instanceof SenderPort) {
			visitSenderPort((SenderPort) bean);
		}
		if (bean instanceof Typed) {
			visitTyped((Typed) bean);
		}
		if (bean instanceof WorkflowPort) {
			visitWorkflowPort((WorkflowPort) bean);
		}
		
		// Now for the classes
		if (bean instanceof Activity) {
			visitActivity((Activity) bean);
		} else if (bean instanceof BlockingControlLink) {
			visitBlockingControlLink((BlockingControlLink) bean);
		} else if (bean instanceof Configuration) {
			visitConfiguration((Configuration) bean);
		} else if (bean instanceof CrossProduct) {
			visitCrossProduct((CrossProduct) bean);
		}  else if (bean instanceof DataLink) {
			visitDataLink((DataLink) bean);
		} else if (bean instanceof DotProduct) {
			visitDotProduct((DotProduct) bean);
		} else if (bean instanceof InputActivityPort) {
			visitInputActivityPort((InputActivityPort) bean);
		}  else if (bean instanceof InputProcessorPort) {
			visitInputProcessorPort((InputProcessorPort) bean);
		}  else if (bean instanceof InputWorkflowPort) {
			visitInputWorkflowPort((InputWorkflowPort) bean);
		} else if (bean instanceof IterationStrategyStack) {
			visitIterationStrategyStack((IterationStrategyStack) bean);
		} else if (bean instanceof OutputActivityPort) {
			visitOutputActivityPort((OutputActivityPort) bean);
		} else if (bean instanceof OutputProcessorPort) {
			visitOutputProcessorPort((OutputProcessorPort) bean);
		} else if (bean instanceof OutputWorkflowPort) {
			visitOutputWorkflowPort((OutputWorkflowPort) bean);
		} else if (bean instanceof PortNode) {
			visitPortNode((PortNode) bean);
		} else if (bean instanceof Processor) {
			visitProcessor((Processor) bean);
		} else if (bean instanceof ProcessorBinding) {
			visitProcessorBinding((ProcessorBinding) bean);
		} else if (bean instanceof ProcessorInputPortBinding) {
			visitProcessorInputPortBinding((ProcessorInputPortBinding) bean);
		} else if (bean instanceof ProcessorOutputPortBinding) {
			visitProcessorOutputPortBinding((ProcessorOutputPortBinding) bean);
		} else if (bean instanceof Profile) {
			visitProfile((Profile) bean);
		} else if (bean instanceof Workflow) {
			visitWorkflow((Workflow) bean);
		} else if (bean instanceof WorkflowBundle) {
			visitWorkflowBundle((WorkflowBundle) bean);
		}
		return true;
	}

	public abstract void visitAbstractGranularDepthPort(AbstractGranularDepthPort bean);

	public abstract void visitAbstractDepthPort(AbstractDepthPort bean);

	public abstract void visitWorkflowBundle(WorkflowBundle bean);

	public abstract void visitWorkflow(Workflow bean);

	public abstract void visitProfile(Profile bean);

	public abstract void visitProcessorOutputPortBinding(ProcessorOutputPortBinding bean);

	public abstract void visitProcessorInputPortBinding(ProcessorInputPortBinding bean);

	public abstract void visitProcessorBinding(ProcessorBinding bean);

	public abstract void visitProcessor(Processor bean);

	public abstract void visitPortNode(PortNode bean);

	public abstract void visitOutputWorkflowPort(OutputWorkflowPort bean);

	public abstract void visitOutputProcessorPort(OutputProcessorPort bean);

	public abstract void visitOutputActivityPort(OutputActivityPort bean);

	public abstract void visitIterationStrategyStack(IterationStrategyStack bean);

	public abstract void visitInputWorkflowPort(InputWorkflowPort bean);

	public abstract void visitInputProcessorPort(InputProcessorPort bean);

	public abstract void visitInputActivityPort(InputActivityPort bean);

	public abstract void visitDotProduct(DotProduct bean);

	public abstract void visitDataLink(DataLink bean);

	public abstract void visitCrossProduct(CrossProduct bean);

	public abstract void visitConfiguration(Configuration bean);

	public abstract void visitBlockingControlLink(BlockingControlLink bean);

	public abstract void visitActivity(Activity bean);

	public abstract void visitWorkflowPort(WorkflowPort bean);

	public abstract void visitTyped(Typed bean);

	public abstract void visitSenderPort(SenderPort bean);

	public abstract void visitRoot(Root bean);

	public abstract void visitReceiverPort(ReceiverPort bean);

	public abstract void visitProcessorPortBinding(ProcessorPortBinding bean);

	public abstract void visitProcessorPort(ProcessorPort bean);

	public abstract void visitPorted(Ported bean);

	public abstract void visitPort(Port bean);

	public abstract void visitOutputPort(OutputPort bean);

	public abstract void visitNamed(Named bean);

	public abstract void visitIterationStrategyTopNode(IterationStrategyTopNode bean);

	public abstract void visitIterationStrategyParent(IterationStrategyParent bean);

	public abstract void visitIterationStrategyNode(IterationStrategyNode bean);

	public abstract void visitInputPort(InputPort bean);

	public abstract void visitControlLink(ControlLink bean);

	public abstract void visitConfigurable(Configurable bean);

	public abstract void visitChild(Child bean);

	public abstract void visitActivityPort(ActivityPort bean);

}
