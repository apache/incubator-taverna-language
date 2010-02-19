/**
 * 
 */
package net.sf.taverna.scufl2.api.configurations;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.common.Configurable;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;
import net.sf.taverna.scufl2.api.common.WorkflowBean;
import net.sf.taverna.scufl2.api.core.Processor;
import net.sf.taverna.scufl2.api.reference.Reference;

/**
 * @author alanrw
 *
 */
@XmlType (propOrder = {"configuredReference", "configurablePropertyConfigurations"})
public class Configuration extends AbstractNamed implements WorkflowBean {
	private Configurable configured;
	
	private Set<ConfigurablePropertyConfiguration> configurablePropertyConfigurations = new HashSet<ConfigurablePropertyConfiguration>();
	
	public Reference<Configurable> getConfiguredReference() {
		return Reference.createReference(configured);
	}

	public void setConfiguredReference(Reference<Configurable> configuredReference) {
		configured = configuredReference.resolve();
	}
	
	/**
	 * @return
	 */
	@XmlTransient
	public Configurable getConfigured() {
		return configured;
	}
	/**
	 * @param configurable
	 */
	public void setConfigured(Configurable configurable) {
		this.configured = configurable;
	}
	/**
	 * @return
	 */
	@XmlElementWrapper( name="configurablePropertyConfigurations",nillable=false,required=true)
	@XmlElement( name="configurablePropertyConfiguration",nillable=false)
	public Set<ConfigurablePropertyConfiguration> getConfigurablePropertyConfigurations() {
		return configurablePropertyConfigurations;
	}
	/**
	 * @param configurablePropertyConfigurations
	 */
	public void setConfigurablePropertyConfigurations(
			Set<ConfigurablePropertyConfiguration> configurablePropertyConfigurations) {
		this.configurablePropertyConfigurations = configurablePropertyConfigurations;
	}
	

}
