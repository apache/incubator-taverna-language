/**
 *
 */
package uk.org.taverna.scufl2.api.profiles;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;

/**
 * A <code>ProcessorInputPortBinding</code> specifies the <code>InputActivityPort</code> to which
 * data passed into an <code>InputProcessorPort</code> is sent.
 * 
 * Note that the <code>InputProcessorPort</code> must be a
 * {@link uk.org.taverna.scufl2.api.port.Port Port} of the
 * {@link uk.org.taverna.scufl2.api.core.Processor Processor} of the parent
 * <code>ProcessorBinding</code>. The <code>InputActivityPort</code> must be a <code>Port</code> of
 * the {@link uk.org.taverna.scufl2.api.activity.Activity Activity} of the parent
 * <code>ProcessorBinding</code>.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class ProcessorInputPortBinding extends AbstractCloneable implements
ProcessorPortBinding<InputActivityPort, InputProcessorPort> {
	private ProcessorBinding parent;
	private InputProcessorPort boundProcessorPort;
	private InputActivityPort boundActivityPort;

	/**
	 * Constructs a <code>ProcessorInputPortBinding</code> with no binding set.
	 */
	public ProcessorInputPortBinding() {
	}

	/**
	 * Constructs a <code>ProcessorInputPortBinding</code> for the specified
	 * <code>ProcessorBinding</code>.
	 * 
	 * @param processorBinding
	 *            the <code>ProcessorBinding</code> to add this
	 *            <code>ProcessorInputPortBinding</code> to. Can be <code>null</code>
	 * @param processorPort
	 *            the bound <code>InputProcessorPort</code>. Can be <code>null</code>
	 * @param activityPort
	 *            the bound <code>InputActivityPort</code>. Can be <code>null</code>
	 */
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
	 * Returns the <code>InputActivityPort</code> to which data is actually sent when passed to the
	 * bound <code>InputProcessorPort</code>.
	 * 
	 * @return the <code>InputActivityPort</code> to which data is actually sent when passed to the
	 *         bound <code>InputProcessorPort</code>
	 */
	@Override
	public InputActivityPort getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * Returns the <code>InputProcessorPort</code> that the binding is for.
	 * 
	 * @return the <code>InputProcessorPort</code> that the binding is for
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
	@Override
	public ProcessorBinding getParent() {
		return parent;
	}

	/**
	 * Sets the <code>InputActivityPort</code> to which data is actually sent when passed to the
	 * bound <code>InputProcessorPort</code>.
	 * 
	 * @param boundActivityPort
	 *            the <code>InputActivityPort</code> to which data is actually sent when passed to
	 *            the bound <code>InputProcessorPort</code>
	 */
	@Override
	public void setBoundActivityPort(InputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/**
	 * Sets the InputProcessorPort that the binding is for.
	 * 
	 * @param boundProcessorPort
	 *            the InputProcessorPort that the binding is for
	 */
	@Override
	public void setBoundProcessorPort(InputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	@Override
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
