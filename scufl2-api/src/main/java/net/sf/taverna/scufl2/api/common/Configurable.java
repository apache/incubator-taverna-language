package net.sf.taverna.scufl2.api.common;

import java.util.Set;

public interface Configurable extends WorkflowBean {
	
	public Set<ConfigurableProperty> getConfigurableProperties();
	
}
