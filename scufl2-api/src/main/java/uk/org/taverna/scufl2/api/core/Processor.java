package uk.org.taverna.scufl2.api.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * A <code>Processor</code> is a {@link Workflow} component that controls the invocation of
 * activities.
 * <p>
 * When a <code>Workflow</code> is run, a particular {@link Activity} will be specified as bound to
 * the <code>Processor</code> by the {@link Profile}.
 * <p>
 * A <code>Processor</code> contains an {@link IterationStrategyStack} and a {@link DispatchStack}
 * and may have {@link InputProcessorPort input} and {@link OutputProcessorPort output} ports.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Processor extends AbstractNamed implements Child<Workflow>, Ported {

	private final NamedSet<OutputProcessorPort> outputPorts = new NamedSet<OutputProcessorPort>();
	private final NamedSet<InputProcessorPort> inputPorts = new NamedSet<InputProcessorPort>();
	private IterationStrategyStack iterationStrategyStack = new IterationStrategyStack(this);
	private DispatchStack dispatchStack = new DispatchStack(this);
	private Workflow parent;

	/**
	 * Constructs a <code>Processor</code> with a random UUID as the name and no parent
	 * {@link Workflow}.
	 */
	public Processor() {
		super();
	}

	/**
	 * Constructs a <code>Processor</code> with the specified parent {@link Workflow} and name.
	 * 
	 * @param parent
	 *            the <code>Workflow</code> to set as the <code>Processor</code>'s parent. Can be
	 *            <code>null</code>.
	 * @param name
	 *            the name of the <code>Processor</code>. <strong>Must not</strong> be
	 *            <code>null</code> or an empty String.
	 */
	public Processor(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getInputPorts());
			children.add(getOutputPorts());
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
			if (getIterationStrategyStack() != null) {
				getIterationStrategyStack().accept(visitor);
			}
			if (getDispatchStack() != null) {
				getDispatchStack().accept(visitor);
			}
		}
		return visitor.visitLeave(this);
	}

	/**
	 * Returns the <code>DispatchStack</code> or <code>null</code> if there is no
	 * <code>DispatchStack</code>.
	 * 
	 * @return the <code>DispatchStack</code> or <code>null</code> if there is no
	 *         <code>DispatchStack</code>
	 */
	public DispatchStack getDispatchStack() {
		return dispatchStack;
	}

	/**
	 * Returns the <code>NamedSet</code> of input ports.
	 * 
	 * Returns an empty <code>NamedSet</code> if there are no input ports.
	 * 
	 * @return the input ports
	 */
	@Override
	public NamedSet<InputProcessorPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * Returns the <code>IterationStrategyStack</code> or <code>null</code> if there is no
	 * <code>IterationStrategyStack</code>.
	 * 
	 * @return the <code>IterationStrategyStack</code> or <code>null</code> if there is no
	 *         <code>IterationStrategyStack</code>
	 */
	public IterationStrategyStack getIterationStrategyStack() {
		return iterationStrategyStack;
	}

	/**
	 * Returns the <code>NamedSet</code> of output ports.
	 * 
	 * Returns an empty <code>NamedSet</code> if there are no output ports.
	 * 
	 * @return the output ports
	 */
	@Override
	public NamedSet<OutputProcessorPort> getOutputPorts() {
		return outputPorts;
	}

	/**
	 * Returns the parent <code>Workflow</code> of null if this <code>Processor</code> is an orphan.
	 * 
	 * @return the parent <code>Workflow</code> of null if this <code>Processor</code> is an orphan
	 */
	@Override
	public Workflow getParent() {
		return parent;
	}

	/**
	 * Sets the <code>DispatchStack</code>.
	 * 
	 * @param dispatchStack
	 *            the <code>DispatchStack</code>. Can be <code>null</code>
	 */
	public void setDispatchStack(DispatchStack dispatchStack) {
		this.dispatchStack = dispatchStack;
		if (dispatchStack != null) {
			dispatchStack.setParent(this);
		}
	}

	/**
	 * Sets the input ports.
	 * 
	 * @return the input ports. <strong>Must not</strong> be <code>null</code>
	 */
	public void setInputPorts(Set<InputProcessorPort> inputPorts) {
		this.inputPorts.clear();
		this.inputPorts.addAll(inputPorts);
	}

	/**
	 * Sets the <code>IterationStrategyStack</code>.
	 * 
	 * @param iterationStrategyStack the <code>IterationStrategyStack</code>. Can be <code>null</code>
	 */
	public void setIterationStrategyStack(IterationStrategyStack iterationStrategyStack) {
		this.iterationStrategyStack = iterationStrategyStack;
		if (iterationStrategyStack != null) {
			iterationStrategyStack.setParent(this);
		}
	}

	/**
	 * Sets the output ports.
	 * 
	 * @return the output ports. <strong>Must not</strong> be <code>null</code>
	 */
	public void setOutputPorts(Set<OutputProcessorPort> outputPorts) {
		this.outputPorts.clear();
		this.outputPorts.addAll(outputPorts);
	}

	@Override
	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getProcessors().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getProcessors().add(this);
		}
	}

}
