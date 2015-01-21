package org.apache.taverna.scufl2.validation.correctness.report;

import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.validation.ValidationProblem;


/**
 * @author alanrw
 *
 */
public class EmptyIterationStrategyTopNodeProblem extends ValidationProblem {
	public EmptyIterationStrategyTopNodeProblem(IterationStrategyTopNode bean) {
		super(bean);
	}

	@Override
	public String toString() {
		return getBean() + " is empty";
	}
}