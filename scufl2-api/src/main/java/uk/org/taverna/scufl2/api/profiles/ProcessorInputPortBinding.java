/**
 *
 */
package uk.org.taverna.scufl2.api.profiles;


import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;

/**
 * A ProcessorInputPortBinding specifies the InputActivityPort to which data
 * passed into an InputProcessorPort is sent.
 *
 * Note that the InputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The InputActivityPort must be a port of the Activity
 * of the parent ProcessorBinding.
 *
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 *
 */
public class ProcessorInputPortBinding implements
		ProcessorPortBinding<InputActivityPort, InputProcessorPort> {
	private ProcessorBinding parent;
	private InputProcessorPort boundProcessorPort;
	private InputActivityPort boundActivityPort;

	public ProcessorInputPortBinding() {
	}

	public ProcessorInputPortBinding(ProcessorBinding processorBinding,
			InputProcessorPort processorPort, InputActivityPort activityPort) {
		setParent(processorBinding);
		setBoundProcessorPort(processorPort);
		setBoundActivityPort(activityPort);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the InputActivityPort to which data is actually sent when passed
	 * to the bound InputProcessorPort.
	 *
	 * @return
	 */
	@Override
	public InputActivityPort getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * Returns the InputProcessorPort that the binding is for.
	 *
	 * @return
	 */
	@Override
	public InputProcessorPort getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	public ProcessorBinding getParent() {
		return parent;
	}

	/**
	 * @param boundActivityPort
	 */
	@Override
	public void setBoundActivityPort(InputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/**
	 * @param boundProcessorPort
	 */
	@Override
	public void setBoundProcessorPort(InputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPortBindings().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPortBindings().add(this);
		}
	}

}
