/**
 * 
 */
package net.sf.taverna.scufl2.api.configurations;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;

/**
 * @author alanrw
 *
 */
public class ConfigurablePropertyConfiguration implements Child<Configuration> {
	
	private ConfigurableProperty configuredProperty;
	private Configuration parent;
	
	private Object value;

	/**
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	public Configuration getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Configuration parent) {
		this.parent = parent;
	}

	/**
	 * @return
	 */
	public ConfigurableProperty getConfiguredProperty() {
		return configuredProperty;
	}

	/**
	 * @param configuredProperty
	 */
	public void setConfiguredProperty(ConfigurableProperty configuredProperty) {
		this.configuredProperty = configuredProperty;
	}

}
