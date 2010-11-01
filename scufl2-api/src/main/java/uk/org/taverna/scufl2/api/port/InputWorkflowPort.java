package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * @author Alan R Williams
 * 
 */
public class InputWorkflowPort extends AbstractGranularDepthPort implements
		SenderPort, WorkflowPort, Child<Workflow> {

	private Workflow parent;

	public InputWorkflowPort() {
		super();
	}

	public InputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	public Workflow getParent() {
		return parent;
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

	@Override
	public String toString() {
		return getParent().getName() + ":" + getName();
	}

}
