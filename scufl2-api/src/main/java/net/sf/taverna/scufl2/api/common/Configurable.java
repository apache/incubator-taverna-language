package net.sf.taverna.scufl2.api.common;

import java.util.Set;

/**
 * @author alanrw
 *
 */
public interface Configurable extends WorkflowBean {
	
	/**
	 * @return
	 */
	public Set<ConfigurableProperty> getConfigurableProperties();
	
	/**
	 * @param configurableProperties
	 */
	public void setConfigurableProperties(Set<ConfigurableProperty> configurableProperties);
	
}
