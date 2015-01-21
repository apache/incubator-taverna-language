package org.apache.taverna.scufl2.validation.correctness.report;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.validation.ValidationProblem;


/**
 * @author alanrw
 */
public class OutOfScopeValueProblem extends ValidationProblem {
	private final String fieldName;
	private final Object value;

	public OutOfScopeValueProblem(WorkflowBean bean, String fieldName,
			Object value) {
		super(bean);
		this.fieldName = fieldName;
		this.value = value;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getBean() + " has " + fieldName + " with out of scope value " + value;
	}
}