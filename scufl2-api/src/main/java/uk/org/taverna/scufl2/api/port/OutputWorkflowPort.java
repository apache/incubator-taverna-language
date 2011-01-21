package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * @author Alan R Williams
 *
 */
public class OutputWorkflowPort extends AbstractNamedChild implements ReceiverPort,
 WorkflowPort, OutputPort {

	private Workflow parent;

	public OutputWorkflowPort() {
		super();
	}

	public OutputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
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
