/**
 * 
 */
package uk.org.taverna.scufl2.api.configurations;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.reference.Reference;


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
