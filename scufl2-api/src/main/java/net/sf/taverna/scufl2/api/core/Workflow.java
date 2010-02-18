package net.sf.taverna.scufl2.api.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.common.Configurable;
import net.sf.taverna.scufl2.api.common.ConfigurableProperty;
import net.sf.taverna.scufl2.api.port.InputWorkflowPort;
import net.sf.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * @author alanrw
 *
 */
public class Workflow extends AbstractNamed implements Configurable {
	
	/**
	 * 
	 */
	public Workflow() {
		super("wf-" + UUID.randomUUID().toString());
	}

	private Set<InputWorkflowPort> inputPorts = new HashSet<InputWorkflowPort>();
	private Set<DataLink> datalinks = new HashSet<DataLink>();
	private Set<Processor> processors = new HashSet<Processor>();
	private Set<OutputWorkflowPort> outputPorts = new HashSet<OutputWorkflowPort>();
	private Set<ConfigurableProperty> configurableProperties = new HashSet<ConfigurableProperty>();

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Configurable#setConfigurableProperties(java.util.Set)
	 */
	public void setConfigurableProperties(
			Set<ConfigurableProperty> configurableProperties) {
		this.configurableProperties = configurableProperties;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper( name="outputWorkflowPorts",nillable=false,required=true)
	@XmlElement( name="outputWorkflowPort",nillable=false)
	public Set<OutputWorkflowPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public String toString() {
		final int maxLen = 6;
		return "Workflow [getName()="
				+ getName()
				+ ", getDatalinks()="
				+ (getDatalinks() != null ? toString(getDatalinks(), maxLen)
						: null)
				+ ", getInputPorts()="
				+ (getInputPorts() != null ? toString(getInputPorts(), maxLen)
						: null)
				+ ", getOutputPorts()="
				+ (getOutputPorts() != null ? toString(getOutputPorts(), maxLen)
						: null)
				+ ", getProcessors()="
				+ (getProcessors() != null ? toString(getProcessors(), maxLen)
						: null) + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @param outputPorts
	 */
	public void setOutputPorts(Set<OutputWorkflowPort> outputPorts) {
		this.outputPorts = outputPorts;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper( name="inputWorkflowPorts",nillable=false,required=true)
	@XmlElement( name="inputWorkflowPort",nillable=false)
	public Set<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * @param inputPorts
	 */
	public void setInputPorts(Set<InputWorkflowPort> inputPorts) {
		this.inputPorts = inputPorts;
	}

	/**
	 * @param processors
	 */
	public void setProcessors(Set<Processor> processors) {
		this.processors = processors;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper( name="processors",nillable=false,required=true)
	@XmlElement( name="processor",nillable=false)
	public Set<Processor> getProcessors() {
		return processors;
	}

	/**
	 * @param datalinks
	 */
	public void setDatalinks(Set<DataLink> datalinks) {
		this.datalinks = datalinks;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper( name="datalinks",nillable=false,required=true)
	@XmlElement( name="datalink",nillable=false)
	public Set<DataLink> getDatalinks() {
		return datalinks;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Configurable#getConfigurableProperties()
	 */
	@XmlElementWrapper( name="configurableProperties",nillable=false,required=true)
	@XmlElement( name="configurableProperty",nillable=false)
	public Set<ConfigurableProperty> getConfigurableProperties() {
		return this.configurableProperties ;
	}

	/**
	 * @param portName
	 * @return
	 */
	public InputWorkflowPort addInputPort(String portName) {
		InputWorkflowPort port = new InputWorkflowPort(this, portName);
		getInputPorts().add(port);
		return port;
	}

	/**
	 * @param portName
	 * @return
	 */
	public OutputWorkflowPort addOutputPort(String portName) {
		OutputWorkflowPort port = new OutputWorkflowPort(this, portName);
		getOutputPorts().add(port);
		return port;
	}

	/**
	 * @param processorName
	 * @return
	 */
	public Processor addProcessor(String processorName) {
		Processor proc = new Processor(this, processorName);
		getProcessors().add(proc);
		return proc;
	}

}