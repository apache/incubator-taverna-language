package net.sf.taverna.scufl2.api.port;

import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;
import net.sf.taverna.scufl2.api.core.Workflow;


/**
 * @author alanrw
 *
 */
public class InputWorkflowPort extends AbstractDepthPort implements SenderPort,
		WorkflowPort, Child<Workflow> {
	
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

	private Set<ConfigurableProperty> configurableProperties = new HashSet<ConfigurableProperty>();


	private Workflow parent;

	/**
	 * @param parent
	 * @param name
	 */
	public InputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Workflow parent) {
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
	public Workflow getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return getParent().getName() + ":" + getName();
	}


	
}
