package org.apache.taverna.scufl2.validation.structural.report;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.validation.ValidationProblem;


public class EmptyCrossProductProblem extends ValidationProblem {
	public EmptyCrossProductProblem(WorkflowBean bean) {
		super(bean);
	}
}
