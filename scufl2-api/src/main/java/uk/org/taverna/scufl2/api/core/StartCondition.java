/**
 * 
 */
package uk.org.taverna.scufl2.api.core;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.Child;


/**
 * @author alanrw
 *
 */
/**
 * @author alanrw
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
		this.parent = parent;
	}
	

}
