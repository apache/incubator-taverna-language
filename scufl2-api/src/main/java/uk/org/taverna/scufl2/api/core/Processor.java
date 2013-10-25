package uk.org.taverna.scufl2.api.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * A <code>Processor</code> is a {@link Workflow} component that controls the
 * invocation of activities.
 * <p>
 * When a <code>Workflow</code> is run, a particular {@link Activity} will be
 * specified as bound to the <code>Processor</code> by the {@link Profile}.
 * <p>
 * A <code>Processor</code> contains an {@link IterationStrategyStack} to
 * describe iterations, and may have {@link InputProcessorPort input} and
 * {@link OutputProcessorPort output} ports. The <code>Processor</code> can be
 * configured with a {@link Configuration} within a {@link Profile} to specify
 * execution details such as retries or parallel jobs.
 * <p>
 * The {@link #getType()} of a Processor is normally fixed to the value given by
 * {@value #PROCESSOR_TYPE}. The configuration of a processor should
 * correspondingly be of the type given by the constant {@value #CONFIG_TYPE}.
 * <p>
 * The default (implied) configuration of a Processor is as of Taverna 3.0 alpha
 * 2:
 * 
 * <pre>
 * { 
 *  "retry": {
 *     "maxRetries": 0,
 *     "initialDelay": 1000,
 *     "maxDelay": 5000,
 *     "backoffFactor": 1.0
 *  }, 
 *  "parallelize": {
 *      "maxJobs": 1
 *  }
 * </pre>
 * 
 * These defaults mean that the processor would not retry the operation, and
 * would only perform one concurrent invocation within a given workflow run.
 * <p>
 * You can provide a partial configuration, e.g. to activate 5 retries using the
 * default initialDelay, add a {@link Configuration} of this {@link Processor}
 * to the {@link Profile} with the keys:
 * 
 * <pre>
 * { 
 *   "retry": {
 *     "maxRetries": 5
 *   }
 * }
 * </pre>
 * <p>
 * Additionally, do..while-style looping can be configured using the key "loop":
 * 
 * <pre>
 * {
 *   "loop": {
 *     "comparePort": "outputB",
 *     "comparison": "EqualTo",
 *     "compareValue": "15",
 *     "delay": 0.5,
 *     "runFirst": true,
 *     "isFeedBack": false,
 *     "conditionActivity": "looping-loop"
 *   }
 * }
 * </pre>
 * <p>
 * Note that this is different from looping over incoming values, which happens
 * according to the {@link #getIterationStrategyStack()}..
 * </p>
 * The above loop configuration would repeat calling of the activity bound to
 * this processor until its output "outputB" is equal to the string value "15" -
 * with a delay of 0.5 seconds between each try.
 * <p>
 * if <code>"isFeedBack": true</code>, then outputs from the processor will on
 * repeated invocations replace the original input values where the port names
 * match. Note that the processor ports should be connected in the containing
 * workflow.
 * <p>
 * <code>"runFirst": true</code> means that the activity is called at least
 * once, which is generally needed to get a port value to compare.
 * <p>
 * The <code>"conditionActivity"</code> indicates the name of an
 * {@link Activity} within the {@link Profile}, here called "looping-loop". This
 * activity is invoked with the current processor output values as input ports,
 * and if its output port "loop" contains the string value "true", then the
 * processor is invoked again. If the condition output port matches a processor
 * input port, then the returned values are used instead of the original
 * processor inputs. The conditionActivity is then repeated on the new output
 * values, repeated until "loop" no longer is "true".
 * <p>
 * As of Taverna 3 alpha 2, the "conditionActivity" is called directly without
 * consideration of the other parameters. This typically contains a beanshell
 * script generated from the above parameters and performs the actual
 * comparisons.
 * <p>
 * FIXME: This conditionActivity currently has to be regenerated when the other
 * parameters have changed. This now happens within the Taverna 3 workbench user
 * interface when configuring looping. The ideal SCUFL2 behaviour would be to EITHER
 * provide a custom conditionActivity OR the parameters.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Processor extends AbstractNamed implements Child<Workflow>, Ported, Configurable {

	public static final URI PROCESSOR_TYPE = URI.create("http://ns.taverna.org.uk/2010/scufl2#Processor");
	public static final URI CONFIG_TYPE = URI.create("http://ns.taverna.org.uk/2010/scufl2#ProcessorConfig");

    private final NamedSet<OutputProcessorPort> outputPorts = new NamedSet<OutputProcessorPort>();
	private final NamedSet<InputProcessorPort> inputPorts = new NamedSet<InputProcessorPort>();
	private IterationStrategyStack iterationStrategyStack = new IterationStrategyStack(this);
	private Workflow parent;
    private URI type = PROCESSOR_TYPE;

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
		}
		return visitor.visitLeave(this);
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

    @Override
    public URI getType() {
        return type;
    }

    @Override
    public void setType(URI type) {
        this.type = type;
    }

}
