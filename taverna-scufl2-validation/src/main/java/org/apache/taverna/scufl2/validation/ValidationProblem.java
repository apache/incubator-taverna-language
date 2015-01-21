/**
 * 
 */
package org.apache.taverna.scufl2.validation;

import org.apache.taverna.scufl2.api.common.WorkflowBean;

/**
 * @author alanrw
 */
public abstract class ValidationProblem {
	private final WorkflowBean bean;

	public ValidationProblem(WorkflowBean bean) {
		this.bean = bean;
	}

	/**
	 * @return the bean
	 */
	public WorkflowBean getBean() {
		return bean;
	}
}
