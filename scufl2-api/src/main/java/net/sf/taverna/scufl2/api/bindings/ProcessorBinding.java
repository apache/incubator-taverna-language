package net.sf.taverna.scufl2.api.bindings;

import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.scufl2.api.activity.Activity;
import net.sf.taverna.scufl2.api.common.WorkflowBean;
import net.sf.taverna.scufl2.api.core.Processor;

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
 * @author alanrw
 * 
 */
public class ProcessorBinding implements WorkflowBean {

	private Processor boundProcessor;
	private Activity boundActivity;

	private Set<ProcessorInputPortBinding> inputPortBindings = new HashSet<ProcessorInputPortBinding>();
	private Set<ProcessorOutputPortBinding> outputPortBindings = new HashSet<ProcessorOutputPortBinding>();

	/**
	 * Returns the bindings for individual input ports of the bound Processor.
	 * 
	 * @return
	 */
	public Set<ProcessorInputPortBinding> getInputPortBindings() {
		return inputPortBindings;
	}

	/**
	 * @param inputPortBindings
	 */
	public void setInputPortBindings(
			Set<ProcessorInputPortBinding> inputPortBindings) {
		this.inputPortBindings = inputPortBindings;
	}

	/**
	 * Returns the bindings for individual output ports of the bound Procesor
	 * 
	 * @return
	 */
	public Set<ProcessorOutputPortBinding> getOutputPortBindings() {
		return outputPortBindings;
	}

	/**
	 * @param outputPortBindings
	 */
	public void setOutputPortBindings(
			Set<ProcessorOutputPortBinding> outputPortBindings) {
		this.outputPortBindings = outputPortBindings;
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
	 * @param boundActivity
	 */
	public void setBoundActivity(Activity boundActivity) {
		this.boundActivity = boundActivity;
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
	 * @param boundProcessor
	 */
	public void setBoundProcessor(Processor boundProcessor) {
		this.boundProcessor = boundProcessor;
	}

}
