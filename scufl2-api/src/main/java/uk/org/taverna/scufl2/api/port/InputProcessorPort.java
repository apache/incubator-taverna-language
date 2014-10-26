package uk.org.taverna.scufl2.api.port;

import java.util.List;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * An <code>InputProcessorPort</code> is a <Port> that inputs data to a
 * {@link Processor}.
 * 
 * @author Alan R Williams
 */
public class InputProcessorPort extends AbstractDepthPort implements
		ReceiverPort, ProcessorPort, InputPort {
	private Processor parent;

	/**
	 * Constructs an <code>InputProcessorPort</code> with a random UUID as the
	 * name.
	 */
	public InputProcessorPort() {
		super();
	}

	/**
	 * Constructs an <code>InputProcessorPort</code> for the specified
	 * <code>Processor</code> with the specified name.
	 * <p>
	 * The <code>InputPort</code> is added to the <code>Processor</code> (if the
	 * <code>Processor</code> is not <code>null</code>).
	 * 
	 * @param parent
	 *            the <code>Processor</code> to add this <code>Port</code> to.
	 *            Can be <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong>
	 *            be <code>null</code> or an empty String.
	 */
	public InputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public Processor getParent() {
		return parent;
	}

	@Override
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getInputPorts().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getInputPorts().add(this);
	}

	// Derived operations, implemented via Scufl2Tools

	/**
	 * Get the datalinks leading to this port.
	 * 
	 * @return the collection of links.
	 * @see Scufl2Tools#datalinksFrom(ReceiverPort)
	 */
	public List<DataLink> getDatalinksTo() {
		return getTools().datalinksTo(this);
	}
}
