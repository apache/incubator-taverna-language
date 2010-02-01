/**
 * 
 */
package net.sf.taverna.scufl2.api.core;

import net.sf.taverna.scufl2.api.common.Child;

/**
 * @author alanrw
 *
 */
/**
 * @author alanrw
 *
 */
public class StartCondition implements Child<Processor> {
	
	private Processor parent;

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	public Processor getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Processor parent) {
		this.parent = parent;
	}
	

}
