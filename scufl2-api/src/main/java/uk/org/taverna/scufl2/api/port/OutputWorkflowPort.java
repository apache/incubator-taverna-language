package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * @author Alan R Williams
 * 
 */
public class OutputWorkflowPort extends AbstractNamed implements ReceiverPort,
WorkflowPort, Child<Workflow> {

	private Workflow parent;

	public OutputWorkflowPort() {
		super();
	}

	public OutputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	public Workflow getParent() {
		return parent;
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

	@Override
	public String toString() {
		return getParent().getName() + "." + getName();
	}

}
