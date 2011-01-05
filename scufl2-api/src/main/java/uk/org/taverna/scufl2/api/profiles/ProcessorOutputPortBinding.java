/**
 * 
 */
package uk.org.taverna.scufl2.api.profiles;


import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;

/**
 * 
 * A ProcessorOutputPortBinding specifies the OutputActivityPort from which data
 * is received for an OutputProcessorPort.
 * 
 * Note that the OutputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The OutputActivityPort must be a port of the
 * Activity of the parent ProcessorBinding.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 */
public class ProcessorOutputPortBinding implements Child<ProcessorBinding> {
	private ProcessorBinding parent;
	private OutputProcessorPort boundProcessorPort;
	private OutputActivityPort boundActivityPort;

	public ProcessorOutputPortBinding(ProcessorBinding processorBinding,
			OutputActivityPort activityPort, OutputProcessorPort processorPort) {
		setParent(processorBinding);
		setBoundActivityPort(activityPort);
		setBoundProcessorPort(processorPort);
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
	 * Returns the OutputProcessorPort that the binding is for.
	 * 
	 * @return
	 */
	public OutputProcessorPort getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	public ProcessorBinding getParent() {
		return parent;
	}

	/**
	 * @param boundActivityPort
	 */
	public void setBoundActivityPort(OutputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/**
	 * @param boundProcessorPort
	 */
	public void setBoundProcessorPort(OutputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPortBindings().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPortBindings().add(this);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getBoundActivityPort() + " -> " + getBoundProcessorPort();
	}

}
