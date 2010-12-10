/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * @author Alan R Williams
 * 
 */
public class Configuration extends AbstractNamedChild implements WorkflowBean,
Child<Profile>, ObjectProperties {
	private Configurable configures;
	private Profile parent;
	private ObjectProperty configurationObjectProperty = new ObjectProperty();

	public Configuration() {
		super();
	}

	public Configuration(String name) {
		super(name);
	}

	public ObjectProperty getConfigurationObjectProperty() {
		return configurationObjectProperty;
	}

	public Configurable getConfigures() {
		return configures;
	}

	@Override
	public URI getObjectClass() {
		return getConfigurationObjectProperty().getObjectClass();
	}

	@Override
	public List<Property> getObjectProperties() {
		return getConfigurationObjectProperty().getObjectProperties();
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	public void setConfigurationObjectProperty(
			ObjectProperty configurationObjectProperty) {
		this.configurationObjectProperty = configurationObjectProperty;
	}

	/**
	 * @param configurable
	 */
	public void setConfigures(Configurable configurable) {
		configures = configurable;
	}

	@Override
	public void setObjectClass(URI configurationType) {
		getConfigurationObjectProperty().setObjectClass(configurationType);
	}

	@Override
	public void setObjectProperties(List<Property> properties) {
		getConfigurationObjectProperty().setObjectProperties(properties);
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


}
