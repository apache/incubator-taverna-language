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
import net.sf.taverna.scufl2.api.activity.OutputActivityPort;
import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.port.OutputProcessorPort;
import net.sf.taverna.scufl2.api.port.ProcessorPort;
import net.sf.taverna.scufl2.api.reference.Reference;

/**
 * 
 * A ProcessorOutputPortBinding specifies the OutputActivityPort from which data
 * is received for an OutputProcessorPort.
 * 
 * Note that the OutputProcessorPort must be a port of the Processor of the
 * parent ProcessorBinding. The OutputActivityPort must be a port of the Activity
 * of the parent ProcessorBinding.
 * 
 * @author alanrw
 *
 */
@XmlType (propOrder = {"boundProcessorPortReference", "boundActivityPortReference"})
public class ProcessorOutputPortBinding implements Child<ProcessorBinding> {
	private ProcessorBinding parent;
	private OutputProcessorPort boundProcessorPort;
	private OutputActivityPort boundActivityPort;

	@XmlElement(required=true, nillable=false)
	public Reference<OutputProcessorPort> getBoundProcessorPortReference() {
		return Reference.createReference(boundProcessorPort);
	}

	public void setBoundProcessorPortReference(Reference<OutputProcessorPort> boundProcessorPortReference) {
		boundProcessorPort = boundProcessorPortReference.resolve();
	}
	
	@XmlElement(required=true, nillable=false)
	public Reference<OutputActivityPort> getBoundActivityPortReference() {
		return Reference.createReference(boundActivityPort);
	}

	public void setBoundActivityPortReference(Reference<OutputActivityPort> boundActivityPortReference) {
		boundActivityPort = boundActivityPortReference.resolve();
	}
	
	/**
	 * Returns the OutputProcessorPort that the binding is for.
	 * 
	 * @return
	 */
	@XmlTransient
	public OutputProcessorPort getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/**
	 * @param boundProcessorPort
	 */
	public void setOutputBoundProcessorPort(OutputProcessorPort boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	/**
	 * 
	 * Returns the OutputActivityPort from which data is received for the bound OutputProcessorPort.
	 * @return
	 */
	@XmlTransient
	public OutputActivityPort getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * @param boundActivityPort
	 */
	public void setBoundActivityPort(OutputActivityPort boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public ProcessorBinding getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2.api.common.WorkflowBean)
	 */
	public void setParent(ProcessorBinding parent) {
		this.parent = parent;
	}

}
