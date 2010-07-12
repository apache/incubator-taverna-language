package uk.org.taverna.scufl2.api;

import javax.xml.bind.annotation.XmlRegistry;


import uk.org.taverna.scufl2.api.container.TavernaResearchObject;
import uk.org.taverna.scufl2.api.core.Workflow;

@XmlRegistry
public class ObjectFactory {

	/**
	 * @return
	 */
	public TavernaResearchObject newResearchObject() {
		return new TavernaResearchObject();
	}

	/**
	 * @return
	 */
	public Workflow newWorkflow() {
		return new Workflow();
	}

}
