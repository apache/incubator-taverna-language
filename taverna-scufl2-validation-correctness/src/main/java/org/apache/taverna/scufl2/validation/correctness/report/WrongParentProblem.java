/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness.report;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.validation.ValidationProblem;


public class WrongParentProblem extends ValidationProblem {
	public WrongParentProblem(WorkflowBean bean) {
		super(bean);
	}

	@Override
	public String toString() {
		return getBean() + " does not have the correct parent";
	}
}