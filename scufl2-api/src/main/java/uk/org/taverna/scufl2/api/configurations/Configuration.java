/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
	private List<Property> properties = new ArrayList<Property>();

	public Configuration() {
		super();
	}

	public Configuration(String name) {
		super(name);
	}

	public Configurable getConfigures() {
		return configures;
	}
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * @param configurable
	 */
	public void setConfigures(Configurable configurable) {
		configures = configurable;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public void setConfigurationType(URI configurationType) {
		this.configurationType = configurationType;
	}

	public URI getConfigurationType() {
		return configurationType;
	}


}
