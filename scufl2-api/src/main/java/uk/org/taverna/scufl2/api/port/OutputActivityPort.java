/**
 * 
 */
package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.activity.Activity;

/**
 * An <code>OutputActivityPort</code> is a <Port> that outputs data from an {@link Activity}.
 * 
 * @author Alan R Williams
 */
public class OutputActivityPort extends AbstractGranularDepthPort implements ActivityPort,
OutputPort, GranularDepthPort {
	private Activity parent;

	/**
	 * Constructs an <code>OutputActivityPort</code> with a random UUID as the name.
	 */
	public OutputActivityPort() {
	}

	/**
	 * Constructs an <code>OutputPort</code> for the specified <code>Activity</code> with the
	 * specified name.
	 * <p>
	 * The <code>OutputPort</code> is added to the <code>Activity</code> (if the
	 * <code>Activity</code> is not <code>null</code>).
	 * 
	 * @param activity
	 *            the <code>Activity</code> to add this <code>Port</code> to. Can be
	 *            <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public OutputActivityPort(Activity activity, String name) {
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
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}

}
