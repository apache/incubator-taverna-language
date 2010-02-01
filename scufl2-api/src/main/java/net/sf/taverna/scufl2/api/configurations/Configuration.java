/**
 * 
 */
package net.sf.taverna.scufl2.api.configurations;

import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.scufl2.api.common.Configurable;
import net.sf.taverna.scufl2.api.common.WorkflowBean;

/**
 * @author alanrw
 *
 */
public class Configuration implements WorkflowBean {
	private Configurable configured;
	
	private Set<ConfigurablePropertyConfiguration> configurablePropertyConfigurations = new HashSet<ConfigurablePropertyConfiguration>();
	
	/**
	 * @return
	 */
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
