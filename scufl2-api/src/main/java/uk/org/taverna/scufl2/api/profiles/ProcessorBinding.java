package uk.org.taverna.scufl2.api.profiles;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.reference.Reference;

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
@XmlType(propOrder = { "boundProcessorReference", "boundActivityReference",
		"activityPosition", "inputPortBindings", "outputPortBindings" })
		public class ProcessorBinding implements WorkflowBean {

	private Processor boundProcessor;
	private Activity boundActivity;

	private Set<ProcessorInputPortBinding> inputPortBindings = new HashSet<ProcessorInputPortBinding>();
	private Set<ProcessorOutputPortBinding> outputPortBindings = new HashSet<ProcessorOutputPortBinding>();

	private Integer activityPosition;

	/**
	 * The relative position of this activity within the processor (for the
	 * purpose of Failover). Activities will be ordered by this position. Gaps
	 * will be ignored, overlapping activity positions will have an undetermined
	 * order.
	 * 
	 * @return
	 */
	@XmlElement(required = false, nillable = false)
	public final Integer getActivityPosition() {
		return activityPosition;
	}

	/**
	 * Returns the Activity that will be used to enact the Processor if this
	 * ProcessorBinding is used.
	 * 
	 * @return
	 */
	@XmlTransient
	public Activity getBoundActivity() {
		return boundActivity;
	}

	@XmlElement(required = true, nillable = false)
	public Reference<Activity> getBoundActivityReference() {
		return Reference.createReference(boundActivity);
	}

	/**
	 * Returns the Processor for which a possible means of enactment is
	 * specified.
	 * 
	 * @return
	 */
	@XmlTransient
	public Processor getBoundProcessor() {
		return boundProcessor;
	}

	@XmlElement(required = true, nillable = false)
	public Reference<Processor> getBoundProcessorReference() {
		return Reference.createReference(boundProcessor);
	}

	/**
	 * Returns the bindings for individual input ports of the bound Processor.
	 * 
	 * @return
	 */
	@XmlElementWrapper(name = "inputPortBindings", nillable = false, required = true)
	@XmlElement(name = "inputPortBinding", nillable = false)
	public Set<ProcessorInputPortBinding> getInputPortBindings() {
		return inputPortBindings;
	}

	/**
	 * Returns the bindings for individual output ports of the bound Procesor
	 * 
	 * @return
	 */
	@XmlElementWrapper(name = "outputPortBindings", nillable = false, required = true)
	@XmlElement(name = "outputPortBinding", nillable = false)
	public Set<ProcessorOutputPortBinding> getOutputPortBindings() {
		return outputPortBindings;
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

	public void setBoundActivityReference(
			Reference<Activity> boundActivityReference) {
		boundActivity = boundActivityReference.resolve();
	}

	/**
	 * @param boundProcessor
	 */
	public void setBoundProcessor(Processor boundProcessor) {
		this.boundProcessor = boundProcessor;
	}

	public void setBoundProcessorReference(
			Reference<Processor> boundProcessorReference) {
		boundProcessor = boundProcessorReference.resolve();
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
	public String toString() {
		return getClass().getSimpleName() + " " + getBoundProcessor() + " "
		+ getBoundActivity();
	}

}
