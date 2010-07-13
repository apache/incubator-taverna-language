/**
 * 
 */
package uk.org.taverna.scufl2.api.core;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.Child;


/**
 * @author Alan R Williams
 *
 */
/**
 * @author Alan R Williams
 *
 */
public abstract class StartCondition implements Child<Processor> {
	
	private Processor parent;

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Processor getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getStartConditions().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getStartConditions().add(this);
		}
	}
	

}
