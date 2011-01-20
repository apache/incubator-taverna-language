package uk.org.taverna.scufl2.api.dispatchstack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;

public class DispatchStack extends ArrayList<DispatchStackLayer> implements
		List<DispatchStackLayer>, Child<Processor>, WorkflowBean {

	private URI type;

	private Processor parent;

	public DispatchStack() {
	}

	public DispatchStack(Processor parent) {
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (WorkflowBean bean : this) {
				if (!bean.accept(visitor)) {
					break;
				}
			}
		}
		return visitor.visitLeave(this);
	}

	@Override
	public Processor getParent() {
		return parent;
	}

	public URI getType() {
		return type;
	}


	@Override
	public void setParent(Processor parent) {
		if (this.parent == parent) {
			return;
		}
		if (this.parent != null && this.parent.getDispatchStack() == this) {
			this.parent.setDispatchStack(null);
		}
		this.parent = parent;
		if (parent != null) {
			parent.setDispatchStack(this);
		}
	}

	public void setType(URI type) {
		this.type = type;
	}

}
