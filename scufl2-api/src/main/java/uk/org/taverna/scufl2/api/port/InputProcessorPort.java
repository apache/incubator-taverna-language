package uk.org.taverna.scufl2.api.port;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.ConfigurableProperty;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;


/**
 * @author Alan R Williams
 *
 */
public class InputProcessorPort extends AbstractGranularDepthPort implements IterationStrategyNode,
		ReceiverPort, ProcessorPort, Child<Processor> {
	
	private NamedSet<ConfigurableProperty> configurableProperties = new NamedSet<ConfigurableProperty>();

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Configurable#getConfigurableProperties()
	 */
	@XmlElementWrapper( name="configurableProperties",nillable=false,required=true)
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

	private Processor parent;

	/**
	 * @param parent
	 * @param name
	 */
	public InputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}
	
	public InputProcessorPort() {
		super();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2.api.common.WorkflowBean)
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
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Processor getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return parent.getName() + ":" + getName();
	}


	
}
