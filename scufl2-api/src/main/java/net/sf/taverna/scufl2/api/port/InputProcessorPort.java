package net.sf.taverna.scufl2.api.port;

import net.sf.taverna.scufl2.api.core.Processor;
import net.sf.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;

public class InputProcessorPort extends AbstractGranularDepthPort implements IterationStrategyNode,
		ReceiverPort, ProcessorPort {

	private Processor parent;

	public InputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPorts().add(this);
		}
	}
	
	public Processor getParent() {
		return parent;
	}

	
}
