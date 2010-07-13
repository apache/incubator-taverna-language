/**
 * 
 */
package uk.org.taverna.scufl2.api.activity;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;


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
public class OutputActivityPort extends AbstractNamed implements Child<Activity> {
	private Activity parent;

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Activity getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean)
	 */
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
