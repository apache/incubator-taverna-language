package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.common.AbstractCloneable.Cloning;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * An <code>OutputWorkflowPort</code> is a <Port> that outputs data from a {@link Workflow}.
 * 
 * @author Alan R Williams
 */
public class OutputWorkflowPort extends AbstractNamedChild implements ReceiverPort, WorkflowPort,
OutputPort {

	private Workflow parent;

	/**
	 * Constructs an <code>OutputWorkflowPort</code> with a random UUID as the name.
	 */
	public OutputWorkflowPort() {
		super();
	}

	/**
	 * Constructs an <code>OutputWorkflowPort</code> for the specified <code>Workflow</code> with
	 * the specified name.
	 * <p>
	 * The <code>OutputPort</code> is added to the <code>Workflow</code> (if the
	 * <code>Workflow</code> is not <code>null</code>).
	 * 
	 * @param parent
	 *            the <code>Workflow</code> to add this <code>Port</code> to. Can be
	 *            <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public OutputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public Workflow getParent() {
		return parent;
	}

	@Override
	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}
	
}
