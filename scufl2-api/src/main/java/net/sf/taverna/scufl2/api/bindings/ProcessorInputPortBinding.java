/**
 * 
 */
package net.sf.taverna.scufl2.api.bindings;

import net.sf.taverna.scufl2.api.activity.InputActivityPort;
import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.port.ProcessorPort;

/**
 * A ProcessorInputPortBinding specifies the InputActivityPort to which data
 * passed into an InputProcessorPort is sent.
 * 
 * Note that the InputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The InputActivityPort must be a port of the Activity
 * of the parent ProcessorBinding.
 * 
 * @author alanrw
 * 
 */
public class ProcessorInputPortBinding implements Child<ProcessorBinding> {
	private ProcessorBinding parent;
	private InputProcessorPort boundProcessorPort;
	private InputActivityPort boundActivityPort;

	/**
	 * Returns the InputProcessorPort that the binding is for.
	 * 
	 * @return
	 */
	public InputProcessorPort getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/**
	 * @param boundProcessorPort
	 */
	public void setBoundProcessorPort(InputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/**
	 * Returns the InputActivityPort to which data is actually sent when passed
	 * to the bound InputProcessorPort.
	 * 
	 * @return
	 */
	public InputActivityPort getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * @param boundActivityPort
	 */
	public void setBoundActivityPort(InputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	public ProcessorBinding getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		this.parent = parent;
	}

}
