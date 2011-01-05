/**
 * 
 */
package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.activity.Activity;

/**
 * An OutputActivityPort represents an output from an Activity.
 * 
 * Note that OutputActivityPort is not part of the Port hierarchy as it does not
 * represent a workflow object but is instead a means of receiving data from
 * whatever underlies the Activity.
 * 
 * @author Alan R Williams
 * 
 */
public class OutputActivityPort extends AbstractGranularDepthPort implements
ActivityPort {
	private Activity parent;

	public OutputActivityPort(Activity activity, String portName) {
		setParent(activity);
		setName(portName);
	}

	public Activity getParent() {
		return parent;
	}

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
