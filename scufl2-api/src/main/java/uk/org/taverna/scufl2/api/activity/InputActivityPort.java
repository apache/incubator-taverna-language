/**
 * 
 */
package uk.org.taverna.scufl2.api.activity;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;


/**
 * An InputActivityPort represents an input to an Activity.
 * 
 * Note that InputActivityPort is not part of the Port hierarchy as it does not
 * represent a workflow object but is instead a means of passing data to
 * whatever underlies the Activity.
 * 
 * @author alanrw
 * 
 */
public class InputActivityPort extends AbstractNamed implements Child<Activity> {

	private Activity parent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Activity getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	public void setParent(Activity parent) {
		this.parent = parent;
	}

}
