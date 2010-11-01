package uk.org.taverna.scufl2.api.dispatchstack;

import java.util.List;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;

public class DispatchStack extends List<DispatchStackLayer>,
Child<Processor>, WorkflowBean {
	private Processor parent;

	@Override
	public Processor getParent() {
		return parent;
	}

	@Override
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.setDispatchStack(null);
		}
		this.parent = parent;
		if (parent != null) {
			parent.setDispatchStack(this);
		}
	}
}
