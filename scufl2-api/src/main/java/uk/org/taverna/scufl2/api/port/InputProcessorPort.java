package uk.org.taverna.scufl2.api.port;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * @author Alan R Williams
 * 
 */
public class InputProcessorPort extends AbstractGranularDepthPort implements
ReceiverPort, ProcessorPort, Child<Processor> {

	private Processor parent;

	public InputProcessorPort() {
		super();
	}

	/**
	 * @param parent
	 * @param name
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
