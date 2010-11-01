/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;


/**
 * @author Alan R Williams
 *
 */
public class Configuration extends AbstractNamed implements WorkflowBean {
	private Configurable configures;
	private URI configurationType;
	private Set<Property> properties;

	public Configuration() {
		super();
	}

	public Configuration(String name) {
		super(name);
	}

	public Configurable getConfigures() {
		return configures;
	}
	/**
	 * @param configurable
	 */
	public void setConfigures(Configurable configurable) {
		configures = configurable;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}

	public Set<Property> getProperties() {
		return properties;
	}


}
