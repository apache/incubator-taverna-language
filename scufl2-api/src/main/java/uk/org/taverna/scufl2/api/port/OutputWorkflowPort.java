package uk.org.taverna.scufl2.api.port;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.ConfigurableProperty;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.core.Workflow;


/**
 * @author Alan R Williams
 *
 */
public class OutputWorkflowPort extends AbstractNamed implements ReceiverPort,
		WorkflowPort, Child<Workflow> {
	private NamedSet<ConfigurableProperty> configurableProperties = new NamedSet<ConfigurableProperty>();

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Configurable#getConfigurableProperties()
	 */
	@XmlElementWrapper( name="configurableProperties",nillable=false,required=false)
	@XmlElement( name="configurableProperty",nillable=false)
	public NamedSet<ConfigurableProperty> getConfigurableProperties() {
		return configurableProperties;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Configurable#setConfigurableProperties(java.util.Set)
	 */
	public void setConfigurableProperties(
			Set<ConfigurableProperty> configurableProperties) {
		this.configurableProperties.clear();
		this.configurableProperties.addAll(configurableProperties);
	}

	private Workflow parent;

	/**
	 * @param parent
	 * @param name
	 */
	public OutputWorkflowPort(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}
	
	public OutputWorkflowPort() {
		super();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getOutputPorts().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getOutputPorts().add(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Workflow getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return getParent().getName() + "." + getName();
	}

	
	
}
