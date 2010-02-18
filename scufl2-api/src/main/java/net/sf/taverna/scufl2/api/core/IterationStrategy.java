package net.sf.taverna.scufl2.api.core;

import javax.xml.bind.annotation.XmlTransient;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.WorkflowBean;

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
