/**
 *
 */
package uk.org.taverna.scufl2.api.profiles;

import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;

/**
 * A <code>ProcessorOutputPortBinding</code> specifies the <code>OutputActivityPort</code> from
 * which data is received for an <code>OutputProcessorPort</code>.
 * 
 * Note that the <code>OutputProcessorPort</code> must be a
 * {@link uk.org.taverna.scufl2.api.port.Port Port} of the
 * {@link uk.org.taverna.scufl2.api.core.Processor Processor} of the parent
 * <code>ProcessorBinding</code>. The <code>OutputActivityPort</code> must be a <code>Port</code> of
 * the {@link uk.org.taverna.scufl2.api.activity.Activity Activity} of the parent
 * <code>ProcessorBinding</code>.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class ProcessorOutputPortBinding extends
		AbstractProcessorPortBinding<OutputActivityPort, OutputProcessorPort>
		implements ProcessorPortBinding<OutputActivityPort, OutputProcessorPort> {
	private ProcessorBinding parent;

	/**
	 * Constructs a <code>ProcessorOutputPortBinding</code> with no binding set.
	 */
	public ProcessorOutputPortBinding() {
	}

	/**
	 * Constructs a <code>ProcessorOutputPortBinding</code> for the specified
	 * <code>ProcessorBinding</code>.
	 * 
	 * @param processorBinding
	 *            the <code>ProcessorBinding</code> to add this
	 *            <code>ProcessorOutputPortBinding</code> to. Can be <code>null</code>
	 * @param activityPort
	 *            the bound <code>OutputActivityPort</code>. Can be <code>null</code>
	 * @param processorPort
	 *            the bound <code>OutputProcessorPort</code>. Can be <code>null</code>
	 */
	public ProcessorOutputPortBinding(ProcessorBinding processorBinding,
			OutputActivityPort activityPort, OutputProcessorPort processorPort) {
		setParent(processorBinding);
		setBoundActivityPort(activityPort);
		setBoundProcessorPort(processorPort);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the <code>OutputActivityPort</code> from which data is received for the bound
	 * <code>OutputProcessorPort</code>.
	 * 
	 * @return the <code>OutputActivityPort</code> from which data is received for the bound
	 *         <code>OutputProcessorPort</code>
	 */
	@Override
	public OutputActivityPort getBoundActivityPort() {
		return super.getBoundActivityPort();
	}

	/**
	 * Returns the <code>OutputProcessorPort</code> that the binding is for.
	 * 
	 * @return the <code>OutputProcessorPort</code> that the binding is for
	 */
	@Override
	public OutputProcessorPort getBoundProcessorPort() {
		return super.getBoundProcessorPort();
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
	 * Sets the <code>OutputActivityPort</code> from which data is received for the bound
	 * <code>OutputProcessorPort</code>.
	 * 
	 * @param boundActivityPort
	 *            the <code>OutputActivityPort</code> from which data is received for the bound
	 *            <code>OutputProcessorPort</code>
	 */
	@Override
	public void setBoundActivityPort(OutputActivityPort boundActivityPort) {
		super.setBoundActivityPort(boundActivityPort);
	}

	/**
	 * Sets the <code>OutputProcessorPort</code> that the binding is for.
	 * 
	 * @param boundProcessorPort
	 *            the <code>OutputProcessorPort</code> that the binding is for
	 */
	@Override
	public void setBoundProcessorPort(OutputProcessorPort boundProcessorPort) {
		super.setBoundProcessorPort(boundProcessorPort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean
	 * )
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
		return getClass().getSimpleName() + " " + getBoundActivityPort() + " -> "
		+ getBoundProcessorPort();
	}

}
