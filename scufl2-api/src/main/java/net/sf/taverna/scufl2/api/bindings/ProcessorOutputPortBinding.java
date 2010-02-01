/**
 * 
 */
package net.sf.taverna.scufl2.api.bindings;

import net.sf.taverna.scufl2.api.activity.OutputActivityPort;
import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.port.OutputProcessorPort;
import net.sf.taverna.scufl2.api.port.ProcessorPort;

/**
 * 
 * A ProcessorOutputPortBinding specifies the OutputActivityPort from which data
 * is received for an OutputProcessorPort.
 * 
 * Note that the OutputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The OutputActivityPort must be a port of the Activity
 * of the parent ProcessorBinding.
 * 
 * @author alanrw
 *
 */
public class ProcessorOutputPortBinding implements Child<ProcessorBinding> {
	private ProcessorBinding parent;
	private OutputProcessorPort boundProcessorPort;
	private OutputActivityPort boundActivityPort;

	/**
	 * Returns the OutputProcessorPort that the binding is for.
	 * 
	 * @return
	 */
	public OutputProcessorPort getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/**
	 * @param boundProcessorPort
	 */
	public void setOutputBoundProcessorPort(OutputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/**
	 * 
	 * Returns the OutputActivityPort from which data is received for the bound OutputProcessorPort.
	 * @return
	 */
	public OutputActivityPort getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * @param boundActivityPort
	 */
	public void setBoundActivityPort(OutputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	public ProcessorBinding getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		this.parent = parent;
	}

}
