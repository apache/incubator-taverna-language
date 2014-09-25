package uk.org.taverna.scufl2.validation.structural.report;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.validation.ValidationProblem;

public class MissingIterationStrategyStackProblem extends ValidationProblem {
	public MissingIterationStrategyStackProblem(WorkflowBean bean) {
		super(bean);
	}
}
