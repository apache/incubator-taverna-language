package uk.org.taverna.scufl2.api.core;


import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;


/**
 * @author Alan R Williams
 *
 */
public class IterationStrategy implements WorkflowBean, Child<Processor>,
		IterationStrategyParent {

	private Processor parent;
	private IterationStrategyNode rootStrategyNode;

	public IterationStrategy() {
		super();
	}

	public Processor getParent() {
		return parent;
	}

	public IterationStrategyNode getRootStrategyNode() {
		return rootStrategyNode;
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

	public void setRootStrategyNode(IterationStrategyNode rootStrategyNode) {
		this.rootStrategyNode = rootStrategyNode;
	}

}
