/**
 * 
 */
package uk.org.taverna.scufl2.validation.structural.report;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.validation.ValidationProblem;

/**
 * @author alanrw
 *
 */
public class IncompleteWorkflowProblem extends ValidationProblem {

	public IncompleteWorkflowProblem(WorkflowBean bean) {
		super(bean);
	}

}
