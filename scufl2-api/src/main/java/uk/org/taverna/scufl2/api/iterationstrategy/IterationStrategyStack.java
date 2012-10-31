package uk.org.taverna.scufl2.api.iterationstrategy;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;

@SuppressWarnings("serial")
public class IterationStrategyStack extends ArrayList<IterationStrategyTopNode>
		implements List<IterationStrategyTopNode>, Child<Processor>,
		IterationStrategyParent {

	private Processor parent;

	public IterationStrategyStack() {
	}

	public IterationStrategyStack(Processor parent) {
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (IterationStrategyTopNode strategy : this) {
				if (!strategy.accept(visitor)) {
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

	@Override
	public void setParent(Processor parent) {
		if (this.parent == parent) {
			return;
		}
		if (this.parent != null
				&& this.parent.getIterationStrategyStack() == this) {
			this.parent.setIterationStrategyStack(null);
		}
		this.parent = parent;
		if (parent != null && parent.getIterationStrategyStack() != this) {
			parent.setIterationStrategyStack(this);
		}
	}

	@Override
	public WorkflowBean clone() {
		return AbstractCloneable.cloneWorkflowBean(this);
	}

}
