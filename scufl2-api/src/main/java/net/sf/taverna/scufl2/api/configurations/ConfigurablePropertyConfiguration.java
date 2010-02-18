/**
 * 
 */
package net.sf.taverna.scufl2.api.configurations;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.reference.Reference;

/**
 * @author alanrw
 *
 */
public class ConfigurablePropertyConfiguration implements Child<Configuration> {
	
	private ConfigurableProperty configuredProperty;
	private Configuration parent;
	
	private Object value;
	
	public Reference<ConfigurableProperty> getConfiguredPropertyReference() {
		return Reference.createReference(configuredProperty);
	}

	public void setConfiguredPropertyReference(Reference<ConfigurableProperty> configuredPropertyReference) {
		configuredProperty = configuredPropertyReference.resolve();
	}
	


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
	@XmlTransient
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
	@XmlTransient
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
