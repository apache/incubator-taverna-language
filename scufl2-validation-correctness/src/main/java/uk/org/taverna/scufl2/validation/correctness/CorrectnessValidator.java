/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import uk.org.taverna.scufl2.api.common.WorkflowBean;

/**
 * @author alanrw
 */
public class CorrectnessValidator {
	public void checkCorrectness(WorkflowBean bean, boolean checkComplete,
			CorrectnessValidationListener listener) {
		CorrectnessVisitor visitor = new CorrectnessVisitor(checkComplete,
				listener);
		bean.accept(visitor);
	}
}
