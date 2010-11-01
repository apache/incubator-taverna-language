/**
 * 
 */
package uk.org.taverna.scufl2.api.core;

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

	public Processor getParent() {
		return parent;
	}

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
