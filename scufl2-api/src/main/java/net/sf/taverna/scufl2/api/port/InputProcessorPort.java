package net.sf.taverna.scufl2.api.port;

import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;
import net.sf.taverna.scufl2.api.core.Processor;
import net.sf.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;

/**
 * @author alanrw
 *
 */
public class InputProcessorPort extends AbstractGranularDepthPort implements IterationStrategyNode,
		ReceiverPort, ProcessorPort, Child<Processor> {
	
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
	public InputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPorts().add(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	public Processor getParent() {
		return parent;
	}

	
}
