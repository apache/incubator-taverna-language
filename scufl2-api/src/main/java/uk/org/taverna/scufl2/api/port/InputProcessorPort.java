package uk.org.taverna.scufl2.api.port;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.core.Processor;

/**
 * An <code>InputProcessorPort</code> is a <Port> that inputs data to a {@link Processor}.
 * 
 * @author Alan R Williams
 */
public class InputProcessorPort extends AbstractDepthPort implements
ReceiverPort, ProcessorPort, InputPort {

	private Processor parent;

	/**
	 * Constructs an <code>InputProcessorPort</code> with a random UUID as the name.
	 */
	public InputProcessorPort() {
		super();
	}

	/**
	 * Constructs an <code>InputProcessorPort</code> for the specified <code>Processor</code> with the
	 * specified name.
	 * <p>
	 * The <code>InputPort</code> is added to the <code>Processor</code> (if the
	 * <code>Processor</code> is not <code>null</code>).
	 * 
	 * @param parent
	 *            the <code>Processor</code> to add this <code>Port</code> to. Can be
	 *            <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public InputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@Override
	@XmlTransient
	public Processor getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	@Override
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPorts().add(this);
		}
	}

	@Override
	public String toString() {
		return parent.getName() + ":" + getName();
	}

}
