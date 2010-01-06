package net.sf.taverna.scufl2.api.port;

import net.sf.taverna.scufl2.api.core.Workflow;


public class InputWorkflowPort extends AbstractDepthPort implements SenderPort,
		WorkflowPort {

	private Workflow parent;

	public InputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPorts().add(this);
		}
	}
	
	public Workflow getParent() {
		return parent;
	}

	
}
