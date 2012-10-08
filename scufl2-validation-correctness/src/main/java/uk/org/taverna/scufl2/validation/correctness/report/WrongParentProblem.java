/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness.report;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.validation.ValidationProblem;

public class WrongParentProblem extends ValidationProblem {
	
	public WrongParentProblem(WorkflowBean bean) {
		super(bean);
	}
	
	public String toString() {
		return(getBean() + " does not have the correct parent");
	}

}