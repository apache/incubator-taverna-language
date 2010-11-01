/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;


/**
 * @author Alan R Williams
 *
 */
public class Configuration extends AbstractNamed implements WorkflowBean {
	private Configurable configured;
	
	public Configuration() {
		super();
	}
	
	public Configuration(String name) {
		super(name);
	}

	public Configurable getConfigured() {
		return configured;
	}
	/**
	 * @param configurable
	 */
	public void setConfigured(Configurable configurable) {
		configured = configurable;
	}
	

}
