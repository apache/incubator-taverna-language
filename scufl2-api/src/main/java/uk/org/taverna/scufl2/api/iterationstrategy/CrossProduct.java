package uk.org.taverna.scufl2.api.iterationstrategy;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;

/**
 * @author Stian Soiland-Reyes
 *
 */
@SuppressWarnings("serial")
public class CrossProduct extends ArrayList<IterationStrategyNode> implements
		IterationStrategyTopNode {

	private IterationStrategyParent parent;

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (IterationStrategyNode strategy : this) {
				if (!strategy.accept(visitor)) {
					break;
				}
			}
		}
		return visitor.visitLeave(this);
	}

	@Override
	public IterationStrategyParent getParent() {
		return parent;
	}

	@Override
	public void setParent(IterationStrategyParent newParent) {
		if (parent == newParent) {
			return;
		}

		if (parent != null) {
			// Remove from old parent

			if (parent instanceof IterationStrategyStack) {
				IterationStrategyStack stack = (IterationStrategyStack) parent;
				stack.remove(this);
			} else if (parent instanceof DotProduct
					|| parent instanceof CrossProduct) {
				@SuppressWarnings("unchecked")
				List<IterationStrategyNode> parentList = (List<IterationStrategyNode>) parent;
				parentList.remove(this);
			} else {
				throw new IllegalArgumentException(
						"Old parent must be a IterationStrategy, DotProduct or CrossProduct: "
								+ parent);
			}

		}

		parent = newParent;

		if (parent instanceof IterationStrategyStack) {
			IterationStrategyStack stack = (IterationStrategyStack) parent;
			if (!stack.contains(this)) {
				stack.add(this);
			}
		} else if (parent instanceof DotProduct
				|| parent instanceof CrossProduct) {
			@SuppressWarnings("unchecked")
			List<IterationStrategyNode> parentList = (List<IterationStrategyNode>) parent;
			if (!parentList.contains(this)) {
				parentList.add(this);
			}
		} else {
			throw new IllegalArgumentException(
					"Parent must be a IterationStrategy, DotProduct or CrossProduct: "
							+ parent);
		}

	}

	@Override
	public WorkflowBean clone() {
		return AbstractCloneable.cloneWorkflowBean(this);
	}
}
