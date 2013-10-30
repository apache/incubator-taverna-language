/**
 * 
 */
package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.activity.Activity;

/**
 * An <code>InputActivityPort</code> is a <Port> that inputs data to an {@link Activity}.
 * 
 * @author Alan R Williams
 */
public class InputActivityPort extends AbstractDepthPort implements ActivityPort, InputPort {

	private Activity parent;

	/**
	 * Constructs an <code>InputActivityPort</code> with a random UUID as the name.
	 */
	public InputActivityPort() {
	}

	/**
	 * Constructs an <code>InputPort</code> for the specified <code>Activity</code> with the
	 * specified name.
	 * <p>
	 * The <code>InputPort</code> is added to the <code>Activity</code> (if the
	 * <code>Activity</code> is not <code>null</code>).
	 * 
	 * @param activity
	 *            the <code>Activity</code> to add this <code>Port</code> to. Can be
	 *            <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public InputActivityPort(Activity activity, String name) {
	    super(name);
		setParent(activity);
	}

	@Override
	public Activity getParent() {
		return parent;
	}

	@Override
	public void setParent(Activity parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPorts().add(this);
		}
	}

}
