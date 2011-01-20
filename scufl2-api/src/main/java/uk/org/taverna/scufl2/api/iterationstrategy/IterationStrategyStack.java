package uk.org.taverna.scufl2.api.iterationstrategy;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.IterationStrategy;
import uk.org.taverna.scufl2.api.core.Processor;

public class IterationStrategyStack extends ArrayList<IterationStrategy>
		implements List<IterationStrategy>, Child<Processor>, WorkflowBean {

	private Processor parent;

	public IterationStrategyStack() {
	}

	public IterationStrategyStack(Processor parent) {
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (IterationStrategy strategy : this) {
				if (!visitor.visit(strategy)) {
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

}
