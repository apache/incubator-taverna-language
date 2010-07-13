package uk.org.taverna.scufl2.api.activity;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.ConfigurableProperty;
import uk.org.taverna.scufl2.api.common.NamedSet;


/**
 * 
 * An Activity specifies a way of implementing a Processor within a Workflow.
 * When the Workflow is run, a particular Activity will be specified as bound to
 * the Processor and Configuration information will be specified for the
 * Activity.
 * 
 * @author Alan R Williams
 * 
 */
@XmlType (propOrder = {"type", "inputPorts", "outputPorts", "configurableProperties"})
public class Activity extends AbstractNamed implements Configurable {

	private NamedSet<ConfigurableProperty> configurableProperties = new NamedSet<ConfigurableProperty>();
	
	private NamedSet<InputActivityPort> inputPorts = new NamedSet<InputActivityPort>();
	private NamedSet<OutputActivityPort> outputPorts = new NamedSet<OutputActivityPort>();
	
	@XmlElementWrapper( name="inputActivityPorts",nillable=false,required=true)
	@XmlElement( name="inputActivityPort",nillable=false)
	public NamedSet<InputActivityPort> getInputPorts() {
		return inputPorts;
	}

	public void setInputPorts(Set<InputActivityPort> inputPorts) {
		this.inputPorts.clear();
		this.inputPorts.addAll(inputPorts);		
	}

	@XmlElementWrapper( name="outputActivityPorts",nillable=false,required=true)
	@XmlElement( name="outputActivityPort",nillable=false)
	public NamedSet<OutputActivityPort> getOutputPorts() {
		return outputPorts;
	}

	public void setOutputPorts(Set<OutputActivityPort> outputPorts) {
		this.outputPorts.clear();
		this.outputPorts.addAll(outputPorts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.org.taverna.scufl2.api.common.Configurable#getConfigurableProperties()
	 * 
	 * It should be noted that some of the set of ConfigurableProperty may be
	 * inferred from the ActivityType. This is yet to be decided.
	 */
	@XmlElementWrapper( name="configurableProperties",nillable=false,required=false)
	@XmlElement( name="configurableProperty",nillable=false)
	public NamedSet<ConfigurableProperty> getConfigurableProperties() {
		return configurableProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.org.taverna.scufl2.api.common.Configurable#setConfigurableProperties
	 * (java.util.Set)
	 */
	public void setConfigurableProperties(
			Set<ConfigurableProperty> configurableProperties) {
		this.configurableProperties.clear();
		this.configurableProperties.addAll(configurableProperties);
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
