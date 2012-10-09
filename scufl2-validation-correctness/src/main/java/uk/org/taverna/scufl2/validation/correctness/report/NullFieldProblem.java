/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.validation.ValidationProblem;

public class NullFieldProblem extends ValidationProblem {
	private final String fieldName;

	public NullFieldProblem(WorkflowBean bean, String fieldName) {
		super(bean);
		this.fieldName = fieldName;	
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	public String toString() {
		return (getBean() + " has a null " + fieldName);
	}
}