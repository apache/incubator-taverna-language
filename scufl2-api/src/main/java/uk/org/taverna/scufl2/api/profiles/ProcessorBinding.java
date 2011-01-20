package uk.org.taverna.scufl2.api.profiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * A ProcessorBinding specifies that when enacting a Workflow, if this
 * particular ProcessorBinding is used, then the boundActivity will be used to
 * implement the boundProcessor.
 *
 * The ProcessorBinding specifies the sets of input and output port bindings for
 * the ports of the Processor. Note that there may not need to be a binding for
 * every Processor port, nor for every Activity port. However, the ports must be
 * of the bound Processor and Activity.
 *
 * It has not been decided if the binding must be unique for a given Processor
 * or Activity port within a ProcessorBinding.
 *
 * @author Alan R Williams
 *
 */
public class ProcessorBinding implements WorkflowBean, Child<Profile> {

	private Processor boundProcessor;
	private Activity boundActivity;

	private Set<ProcessorInputPortBinding> inputPortBindings = new HashSet<ProcessorInputPortBinding>();
	private Set<ProcessorOutputPortBinding> outputPortBindings = new HashSet<ProcessorOutputPortBinding>();

	private Integer activityPosition;
	private Profile parent;

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getInputPortBindings());
			children.add(getOutputPortBindings());
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
		}
		return visitor.visitLeave(this);
	}

	/**
	 * The relative position of this activity within the processor (for the
	 * purpose of Failover). Activities will be ordered by this position. Gaps
	 * will be ignored, overlapping activity positions will have an undetermined
	 * order.
	 *
	 * @return
	 */
	public final Integer getActivityPosition() {
		return activityPosition;
	}

	/**
	 * Returns the Activity that will be used to enact the Processor if this
	 * ProcessorBinding is used.
	 *
	 * @return
	 */
	public Activity getBoundActivity() {
		return boundActivity;
	}

	/**
	 * Returns the Processor for which a possible means of enactment is
	 * specified.
	 *
	 * @return
	 */
	public Processor getBoundProcessor() {
		return boundProcessor;
	}

	/**
	 * Returns the bindings for individual input ports of the bound Processor.
	 *
	 * @return
	 */
	public Set<ProcessorInputPortBinding> getInputPortBindings() {
		return inputPortBindings;
	}

	/**
	 * Returns the bindings for individual output ports of the bound Procesor
	 *
	 * @return
	 */
	public Set<ProcessorOutputPortBinding> getOutputPortBindings() {
		return outputPortBindings;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	public void setActivityPosition(Integer activityPosition) {
		this.activityPosition = activityPosition;
	}

	/**
	 * @param boundActivity
	 */
	public void setBoundActivity(Activity boundActivity) {
		this.boundActivity = boundActivity;
	}

	/**
	 * @param boundProcessor
	 */
	public void setBoundProcessor(Processor boundProcessor) {
		this.boundProcessor = boundProcessor;
	}

	/**
	 * @param inputPortBindings
	 */
	public void setInputPortBindings(
			Set<ProcessorInputPortBinding> inputPortBindings) {
		this.inputPortBindings = inputPortBindings;
	}

	/**
	 * @param outputPortBindings
	 */
	public void setOutputPortBindings(
			Set<ProcessorOutputPortBinding> outputPortBindings) {
		this.outputPortBindings = outputPortBindings;
	}

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getProcessorBindings().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getProcessorBindings().add(this);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getBoundProcessor() + " "
		+ getBoundActivity();
	}

}
