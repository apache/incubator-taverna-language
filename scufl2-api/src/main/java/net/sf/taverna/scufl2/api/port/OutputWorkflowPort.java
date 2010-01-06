package net.sf.taverna.scufl2.api.port;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.core.Workflow;

public class OutputWorkflowPort extends AbstractNamed implements ReceiverPort,
		WorkflowPort {

	private Workflow parent;

	public OutputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}
	
	public Workflow getParent() {
		return parent;
	}

	
	
}
