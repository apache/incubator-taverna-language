/**
 *
 */
package uk.org.taverna.scufl2.api.configurations;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 * Configuration of a {@link Configurable} workflow bean.
 * <p>
 * A configuration is activated by a {@link Profile}, and provides a link to the
 * {@link #getPropertyResource()} containing the properties to configure the
 * bean, like an {@link Activity}.
 * <p>
 * A configuration is of a certain (RDF) <strong>type</strong>, as defined by
 * {@link PropertyResource#getTypeURI()} on the - which determines which
 * properties are required and optional. For instance, the type
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/ConfigType</code> requires
 * the property
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/operation</code>.
 * <p>
 * These requirements are described in the {@link ConfigurationDefinition} which
 * {@link ConfigurationDefinition#getConfigurationType()} matches this
 * configuration's {@link #getTypeURI()}. <strong>TODO: Where are the
 * ConfigurationDefinitions found?</strong>
 *
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 *
 */
public class Configuration extends AbstractNamedChild implements
Child<Profile> {
	private Configurable configures;
	private Profile parent;
	private PropertyResource propertyResource = new PropertyResource();

	public Configuration() {
		super();
	}

	public Configuration(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this) && getPropertyResource() != null) {
			getPropertyResource().accept(visitor);
		}
		return visitor.visitLeave(this);
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
	public Profile getParent() {
		return parent;
	}

	/**
	 * Get the underlying {@link PropertyResource} which contains the properties
	 * set by this configuration.
	 * <p>
	 *
	 * @return The backing {@link PropertyResource}.
	 */
	public PropertyResource getPropertyResource() {
		return propertyResource;
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

	public void setPropertyResource(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

}
