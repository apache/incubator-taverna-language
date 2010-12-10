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
Child<Profile> {
	private Configurable configures;
	private Profile parent;
	private ObjectProperty configurationObject = new ObjectProperty();

	public Configuration() {
		super();
	}

	public Configuration(String name) {
		super(name);
	}

	public URI getConfigurationType() {
		return configurationObject.getObjectClass();
	}
	public Configurable getConfigures() {
		return configures;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	public List<Property> getProperties() {
		return configurationObject.getObjectProperties();
	}

	public void setConfigurationType(URI configurationType) {
		configurationObject.setObjectClass(configurationType);
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
		configurationObject.setObjectProperties(properties);
	}

}
