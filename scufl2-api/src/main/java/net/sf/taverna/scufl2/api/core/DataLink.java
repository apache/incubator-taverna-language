package net.sf.taverna.scufl2.api.core;

import net.sf.taverna.scufl2.api.common.WorkflowBean;
import net.sf.taverna.scufl2.api.port.ReceiverPort;
import net.sf.taverna.scufl2.api.port.SenderPort;

/**
 * @author alanrw
 *
 */
public class DataLink implements WorkflowBean {

	/**
	 * @param senderPort
	 * @param receiverPort
	 */
	public DataLink(SenderPort senderPort, ReceiverPort receiverPort) {
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
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

	private ReceiverPort receiverPort;
	private SenderPort senderPort;

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

	/**
	 * @return
	 */
	public ReceiverPort getReceiverPort() {
		return receiverPort;
	}

	/**
	 * @return
	 */
	public SenderPort getSenderPort() {
		return senderPort;
	}
	
	@Override
	public String toString() {
		return getSenderPort() + "=>" + getReceiverPort();
	}

}
