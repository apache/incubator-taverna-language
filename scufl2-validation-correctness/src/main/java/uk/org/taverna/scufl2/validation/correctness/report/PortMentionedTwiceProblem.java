package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.validation.ValidationProblem;

/**
 * @author alanrw
 *
 */
public class PortMentionedTwiceProblem extends ValidationProblem {

	private final IterationStrategyNode duplicateNode;

	public PortMentionedTwiceProblem(IterationStrategyNode originalNode,
			IterationStrategyNode duplicateNode) {
		super(originalNode);
				this.duplicateNode = duplicateNode;
	}

	/**
	 * @return the iterationStrategyNode
	 */
	public IterationStrategyNode getDuplicateNode() {
		return duplicateNode;
	}
	
	public String toString() {
		return (getBean() + " and " + duplicateNode + " reference the same port");
	}

}