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
		this.parent = parent;
	}

	public IterationStrategy() {
		super();
	}
}
