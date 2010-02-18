/**
 * 
 */
package net.sf.taverna.scufl2.api.bindings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.activity.InputActivityPort;
import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.port.ProcessorPort;
import net.sf.taverna.scufl2.api.reference.Reference;

/**
 * A ProcessorInputPortBinding specifies the InputActivityPort to which data
 * passed into an InputProcessorPort is sent.
 * 
 * Note that the InputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The InputActivityPort must be a port of the Activity
 * of the parent ProcessorBinding.
 * 
 * @author alanrw
 * 
 */
@XmlType (propOrder = {"boundProcessorPortReference", "boundActivityPortReference"})
public class ProcessorInputPortBinding implements Child<ProcessorBinding> {
	private ProcessorBinding parent;
	private InputProcessorPort boundProcessorPort;
	private InputActivityPort boundActivityPort;
	
	@XmlElement(required=true, nillable=false)
	public Reference<InputProcessorPort> getBoundProcessorPortReference() {
		return Reference.createReference(boundProcessorPort);
	}

	public void setBoundProcessorPortReference(Reference<InputProcessorPort> boundProcessorPortReference) {
		boundProcessorPort = boundProcessorPortReference.resolve();
	}
	
	@XmlElement(required=true, nillable=false)
	public Reference<InputActivityPort> getBoundActivityPortReference() {
		return Reference.createReference(boundActivityPort);
	}

	public void setBoundActivityPortReference(Reference<InputActivityPort> boundActivityPortReference) {
		boundActivityPort = boundActivityPortReference.resolve();
	}
	
	/**
	 * Returns the InputProcessorPort that the binding is for.
	 * 
	 * @return
	 */
	@XmlTransient
	public InputProcessorPort getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/**
	 * @param boundProcessorPort
	 */
	public void setBoundProcessorPort(InputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/**
	 * Returns the InputActivityPort to which data is actually sent when passed
	 * to the bound InputProcessorPort.
	 * 
	 * @return
	 */
	@XmlTransient
	public InputActivityPort getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * @param boundActivityPort
	 */
	public void setBoundActivityPort(InputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public ProcessorBinding getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		this.parent = parent;
	}

}
