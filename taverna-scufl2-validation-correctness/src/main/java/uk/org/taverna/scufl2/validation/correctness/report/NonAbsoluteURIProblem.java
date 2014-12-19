/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness.report;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.validation.ValidationProblem;

public class NonAbsoluteURIProblem extends ValidationProblem {
	private String fieldName;
	private URI fieldValue;

	public NonAbsoluteURIProblem(WorkflowBean bean, String fieldName,
			URI fieldValue) {
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
	public URI getFieldValue() {
		return fieldValue;
	}

	@Override
	public String toString() {
		return getBean() + "has a non-absolute URI in field " + fieldName
				+ " of value " + fieldValue;
	}
}