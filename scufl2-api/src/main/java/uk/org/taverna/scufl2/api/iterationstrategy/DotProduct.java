package uk.org.taverna.scufl2.api.iterationstrategy;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.core.IterationStrategy;

/**
 * @author Stian Soiland-Reyes
 * 
 */
public class DotProduct extends ArrayList<IterationStrategyNode> implements
List<IterationStrategyNode>, IterationStrategyNode,
IterationStrategyParent, Child<IterationStrategyParent> {

	private IterationStrategyParent parent;

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

			if (parent instanceof IterationStrategy) {
				IterationStrategy iterationStrategy = (IterationStrategy) parent;
				if (iterationStrategy.getRootStrategyNode() == this) {
					iterationStrategy.setRootStrategyNode(null);
				}
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

		if (parent instanceof IterationStrategy) {
			IterationStrategy iterationStrategy = (IterationStrategy) parent;
			if (iterationStrategy.getRootStrategyNode() != this) {
				iterationStrategy.setRootStrategyNode(this);
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

}
