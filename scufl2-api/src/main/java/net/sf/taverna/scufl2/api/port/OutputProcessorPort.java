package net.sf.taverna.scufl2.api.port;

import net.sf.taverna.scufl2.api.core.Processor;

public class OutputProcessorPort extends AbstractGranularDepthPort implements
		SenderPort, ProcessorPort {

	private Processor parent;

	public OutputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}
	
	public Processor getParent() {
		return parent;
	}
}
