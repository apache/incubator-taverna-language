package uk.org.taverna.scufl2.api.core;

import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.WorkflowBean;


/**
 * @author alanrw
 *
 */
public class IterationStrategy implements WorkflowBean, Child<Processor> {
	
	private Processor parent;

	@XmlTransient
	public Processor getParent() {
		return parent;
	}

	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getIterationStrategyStack().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getIterationStrategyStack().add(this);
		}
	}

	public IterationStrategy() {
		super();
	}
}
