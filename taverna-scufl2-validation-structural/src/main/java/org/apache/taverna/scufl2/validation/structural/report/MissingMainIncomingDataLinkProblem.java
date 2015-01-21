/**
 * 
 */
package org.apache.taverna.scufl2.validation.structural.report;

import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.validation.ValidationProblem;


/**
 * @author alanrw
 */
public class MissingMainIncomingDataLinkProblem extends ValidationProblem {
	public MissingMainIncomingDataLinkProblem(WorkflowBean bean) {
		super(bean);
	}
}
