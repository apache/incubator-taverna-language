/**
 * 
 */
package uk.org.taverna.scufl2.api.profiles;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.activity.InputActivityPort;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.reference.Reference;


/**
 * A ProcessorInputPortBinding specifies the InputActivityPort to which data
 * passed into an InputProcessorPort is sent.
 * 
 * Note that the InputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The InputActivityPort must be a port of the Activity
 * of the parent ProcessorBinding.
 * 
 * @author Alan R Williams
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
	 * @see uk.org.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public ProcessorBinding getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.org.taverna.scufl2.api.common.Child#setParent(uk.org.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getInputPortBindings().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getInputPortBindings().add(this);
		}
	}

}
