/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.validation.ValidationProblem;

public class NegativeValueProblem extends ValidationProblem {
	private final String fieldName;
	private final Integer fieldValue;

	public NegativeValueProblem(WorkflowBean bean, String fieldName,
			Integer fieldValue) {
		super(bean);
				this.fieldName = fieldName;
				this.fieldValue = fieldValue;
		
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the fieldValue
	 */
	public Integer getFieldValue() {
		return fieldValue;
	}
	
	public String toString() {
		return (getBean() + " has " + fieldName + " of value " + fieldValue);
	}
}