package org.apache.taverna.scufl2.validation.structural.report;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.validation.ValidationProblem;


public class EmptyDotProductProblem extends ValidationProblem {
	public EmptyDotProductProblem(WorkflowBean bean) {
		super(bean);
	}
}
