package net.sf.taverna.scufl2.api.port;

import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;
import net.sf.taverna.scufl2.api.core.Processor;

public class OutputProcessorPort extends AbstractGranularDepthPort implements
		SenderPort, ProcessorPort, Child<Processor> {
	
	private Set<ConfigurableProperty> configurableProperties = new HashSet<ConfigurableProperty>();


	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Configurable#getConfigurableProperties()
	 */
	public Set<ConfigurableProperty> getConfigurableProperties() {
		return configurableProperties;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Configurable#setConfigurableProperties(java.util.Set)
	 */
	public void setConfigurableProperties(
			Set<ConfigurableProperty> configurableProperties) {
		this.configurableProperties = configurableProperties;
	}

	private Processor parent;

	/**
	 * @param parent
	 * @param name
	 */
	public OutputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	public Processor getParent() {
		return parent;
	}
}
