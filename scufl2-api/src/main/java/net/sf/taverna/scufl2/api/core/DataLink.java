package net.sf.taverna.scufl2.api.core;

import net.sf.taverna.scufl2.api.common.Child;
import net.sf.taverna.scufl2.api.common.Configurable;
import net.sf.taverna.scufl2.api.common.WorkflowBean;
import net.sf.taverna.scufl2.api.port.ReceiverPort;
import net.sf.taverna.scufl2.api.port.SenderPort;
import net.sf.taverna.scufl2.api.reference.Reference;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * @author alanrw
 *
 */
@XmlRootElement
@XmlType(propOrder = {"senderPortReference", "receiverPortReference"})
public class DataLink implements WorkflowBean, Child<Workflow> {

	private ReceiverPort receiverPort;
	
	private SenderPort senderPort;
	
	private Workflow parent;
	
	@XmlTransient
	public Workflow getParent() {
		return parent;
	}

	public void setParent(Workflow parent) {
		this.parent = parent;
	}

	/**
	 * @param senderPort
	 * @param receiverPort
	 */
	public DataLink(SenderPort senderPort, ReceiverPort receiverPort) {
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
	}
	
	public DataLink() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((receiverPort == null) ? 0 : receiverPort.hashCode());
		result = prime * result
				+ ((senderPort == null) ? 0 : senderPort.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataLink other = (DataLink) obj;
		if (receiverPort == null) {
			if (other.receiverPort != null)
				return false;
		} else if (!receiverPort.equals(other.receiverPort))
			return false;
		if (senderPort == null) {
			if (other.senderPort != null)
				return false;
		} else if (!senderPort.equals(other.senderPort))
			return false;
		return true;
	}


	/**
	 * @param senderPort
	 */
	public void setSenderPort(SenderPort senderPort) {
		this.senderPort = senderPort;
	}

	/**
	 * @param receiverPort
	 */
	public void setReceiverPort(ReceiverPort receiverPort) {
		this.receiverPort = receiverPort;
	}

	public Reference<ReceiverPort> getReceiverPortReference() {
		return Reference.createReference(receiverPort);
	}

	public void setReceiverPortReference(Reference<ReceiverPort> receiverPortReference) {
		receiverPort = receiverPortReference.resolve();
	}
	
	public Reference<SenderPort> getSenderPortReference() {
		return Reference.createReference(senderPort);
	}

	public void setSenderPortReference(Reference<SenderPort> senderPortReference) {
		senderPort = senderPortReference.resolve();
	}
	
	/**
	 * @return
	 */
	@XmlTransient
	public ReceiverPort getReceiverPort() {
		return receiverPort;
	}

	/**
	 * @return
	 */
	@XmlTransient
	public SenderPort getSenderPort() {
		return senderPort;
	}
	
	@Override
	public String toString() {
		return getSenderPort() + "=>" + getReceiverPort();
	}

}
