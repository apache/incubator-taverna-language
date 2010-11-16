/**
 * 
 */
package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.activity.Activity;


/**
 * An InputActivityPort represents an input to an Activity.
 * 
 * Note that InputActivityPort is not part of the Port hierarchy as it does not
 * represent a workflow object but is instead a means of passing data to
 * whatever underlies the Activity.
 * 
 * @author Alan R Williams
 * 
 */
public class InputActivityPort extends AbstractDepthPort implements ActivityPort {

	private Activity parent;

	public Activity getParent() {
		return parent;
	}

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
