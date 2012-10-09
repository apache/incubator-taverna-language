package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.validation.ValidationProblem;

/**
 * @author alanrw
 *
 */
public class EmptyIterationStrategyTopNodeProblem extends ValidationProblem {
	
	public EmptyIterationStrategyTopNodeProblem(IterationStrategyTopNode bean) {
		super(bean);
	}

	public String toString() {
		return (getBean() + " is empty");
	}

}