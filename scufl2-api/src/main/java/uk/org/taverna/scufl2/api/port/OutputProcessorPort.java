package uk.org.taverna.scufl2.api.port;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.Processor;


public class OutputProcessorPort extends AbstractGranularDepthPort implements
SenderPort, ProcessorPort, OutputPort, Child<Processor> {



	private Processor parent;

	public OutputProcessorPort() {
		super();
	}

	/**
	 * @param parent
	 * @param name
	 */
	public OutputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Processor getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}

	@Override
	public String toString() {
		return getParent().getName() + "." + getName();
	}
}
