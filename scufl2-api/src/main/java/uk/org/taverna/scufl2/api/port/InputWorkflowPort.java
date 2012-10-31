package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * An <code>InputWorkflowPort</code> is a <Port> that inputs data to a {@link Workflow}.
 * 
 * @author Alan R Williams
 */
public class InputWorkflowPort extends AbstractDepthPort implements
SenderPort, WorkflowPort, InputPort {

	private Workflow parent;

	/**
	 * Constructs an <code>InputWorkflowPort</code> with a random UUID as the name.
	 */
	public InputWorkflowPort() {
		super();
	}

	/**
	 * Constructs an <code>InputWorkflowPort</code> for the specified <code>Workflow</code> with the
	 * specified name.
	 * <p>
	 * The <code>InputPort</code> is added to the <code>Workflow</code> (if the
	 * <code>Workflow</code> is not <code>null</code>).
	 * 
	 * @param parent
	 *            the <code>Workflow</code> to add this <code>Port</code> to. Can be
	 *            <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public InputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public Workflow getParent() {
		return parent;
	}

	@Override
	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPorts().add(this);
		}
	}

}
