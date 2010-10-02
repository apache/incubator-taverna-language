package uk.org.taverna.scufl2.api;

import javax.xml.bind.annotation.XmlRegistry;


import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;

@XmlRegistry
public class ObjectFactory {

	/**
	 * @return
	 */
	public WorkflowBundle newResearchObject() {
		return new WorkflowBundle();
	}

	/**
	 * @return
	 */
	public Workflow newWorkflow() {
		return new Workflow();
	}

}
