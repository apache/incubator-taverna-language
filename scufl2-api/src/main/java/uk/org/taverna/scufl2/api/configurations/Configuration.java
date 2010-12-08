/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.profiles.Profile;


/**
 * @author Alan R Williams
 *
 */
public class Configuration extends AbstractNamed implements WorkflowBean,
		Child<Profile> {
	private Configurable configures;
	private URI configurationType;
	private List<Property> properties = new ArrayList<Property>();
	private Profile parent;

	public Configuration() {
		super();
	}

	public Configuration(String name) {
		super(name);
	}

	public URI getConfigurationType() {
		return configurationType;
	}
	public Configurable getConfigures() {
		return configures;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setConfigurationType(URI configurationType) {
		this.configurationType = configurationType;
	}

	/**
	 * @param configurable
	 */
	public void setConfigures(Configurable configurable) {
		configures = configurable;
	}

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getConfigurations().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getConfigurations().add(this);
		}

	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
