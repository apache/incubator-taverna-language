/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Configuration of a {@link Configurable} workflow bean.
 * <p>
 * A configuration is activated by a {@link Profile}, and provides properties
 * (at {@link #getObjectProperties()}) to configure the bean, like an
 * {@link Activity}.
 * <p>
 * A configuration is of a certain type, {@link #getObjectClass()} - which
 * determines which properties are required and optional. For instance, the
 * object class
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/ConfigType</code> requires
 * the property
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/operation</code>. These
 * requirements are described in the {@link ConfigurationDefinition} which
 * {@link ConfigurationDefinition#getConfigurationType()} matches this
 * configuration's {@link #getObjectClass()}. <strong>TODO: Where are the
 * ConfigurationDefinitions found?</strong>
 * <p>
 * The list of properties in {@link #getObjectProperties()} can be either flat
 * values (strings/integers) using {@link DataProperty}s, or nested
 * {@link ObjectProperty} which themselves should have an
 * {@link ObjectProperty#getObjectClass()} and matching
 * {@link ObjectPropertyDefinition}.
 * <p>
 * Multiple properties entries for the same {@link Property#getPredicate()} is
 * allowed if the matching {@link DataPropertyDefinition#isMultiple()} is true.
 * 
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
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

	/**
	 * Get the underlying {@link ObjectProperty} which contains the properties
	 * and object class exposed by {@link #getObjectProperties()} and
	 * {@link #getObjectClass()}.
	 * <p>
	 * This can be useful as a top-level object for nested ObjectProperties, but
	 * this ObjectProperty does not have a {@link ObjectProperty#getPredicate()}
	 * or {@link ObjectProperty#getObjectUri()}.
	 * <p>
	 * The methods {@link #getObjectProperties()}/
	 * {@link #setObjectProperties(List)} and {@link #getObjectClass()}/
	 * {@link #setObjectClass(URI)} will proxy the changes to this underlying
	 * ObjectProperty.
	 * 
	 * @return The backing {@link ObjectProperty}.
	 */
	public ObjectProperty getConfigurationObjectProperty() {
		return configurationObjectProperty;
	}

	/**
	 * The {@link Configurable} workflow bean that is configured. Typically an
	 * {@link Activity} or {@link DispatchStackLayer}, but in theory also
	 * {@link Processor}s, {@link Workflow}s and {@link Port}s can be
	 * configured.
	 * <p>
	 * {@link Configurable#}
	 * 
	 * @return
	 */
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
