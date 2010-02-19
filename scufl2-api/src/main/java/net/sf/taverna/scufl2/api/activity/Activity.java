package net.sf.taverna.scufl2.api.activity;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.common.Configurable;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;

/**
 * 
 * An Activity specifies a way of implementing a Processor within a Workflow.
 * When the Workflow is run, a particular Activity will be specified as bound to
 * the Processor and Configuration information will be specified for the
 * Activity.
 * 
 * @author alanrw
 * 
 */
@XmlType (propOrder = {"type", "inputPorts", "outputPorts", "configurableProperties"})
public class Activity extends AbstractNamed implements Configurable {

	private Set<ConfigurableProperty> configurableProperties = new HashSet<ConfigurableProperty>();
	
	private Set<InputActivityPort> inputPorts = new HashSet<InputActivityPort>();
	private Set<OutputActivityPort> outputPorts = new HashSet<OutputActivityPort>();
	
	@XmlElementWrapper( name="inputActivityPorts",nillable=false,required=true)
	@XmlElement( name="inputActivityPort",nillable=false)
	public Set<InputActivityPort> getInputPorts() {
		return inputPorts;
	}

	public void setInputPorts(Set<InputActivityPort> inputPorts) {
		this.inputPorts = inputPorts;
	}

	@XmlElementWrapper( name="outputActivityPorts",nillable=false,required=true)
	@XmlElement( name="outputActivityPort",nillable=false)
	public Set<OutputActivityPort> getOutputPorts() {
		return outputPorts;
	}

	public void setOutputPorts(Set<OutputActivityPort> outputPorts) {
		this.outputPorts = outputPorts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.scufl2.api.common.Configurable#getConfigurableProperties()
	 * 
	 * It should be noted that some of the set of ConfigurableProperty may be
	 * inferred from the ActivityType. This is yet to be decided.
	 */
	@XmlElementWrapper( name="configurableProperties",nillable=false,required=true)
	@XmlElement( name="configurableProperty",nillable=false)
	public Set<ConfigurableProperty> getConfigurableProperties() {
		return configurableProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.scufl2.api.common.Configurable#setConfigurableProperties
	 * (java.util.Set)
	 */
	public void setConfigurableProperties(
			Set<ConfigurableProperty> configurableProperties) {
		this.configurableProperties = configurableProperties;
	}

	/**
	 * @param name
	 */
	public Activity(String name) {
		super(name);
	}
	
	public Activity() {
		super();
	}

	private ActivityType type;

	/**
	 * getType returns the type of the Activity.
	 * 
	 * @return
	 */
	@XmlElement(required=true,nillable=false)
	public ActivityType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Activity " + getType().getName() + " \"" + getName() + '"';
	}

	/**
	 * @param type
	 */
	public void setType(ActivityType type) {
		this.type = type;
	}

}
