package uk.org.taverna.scufl2.api.core;


import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;


/**
 * @author Alan R Williams
 *
 */
public class DataLink implements WorkflowBean, Child<Workflow>, Comparable {

	private ReceiverPort sendsTo;

	private SenderPort receivesFrom;

	private Integer mergePosition;

	private Workflow parent;

	public DataLink() {
		super();
	}

	public DataLink(Workflow parent, SenderPort senderPort,
			ReceiverPort receiverPort) {
		setReceivesFrom(senderPort);
		setSendsTo(receiverPort);
		setParent(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Object o) {
		if (! (o instanceof DataLink)) {
			return o.getClass().getCanonicalName().compareTo(getClass().getCanonicalName());
		}
		DataLink o1 = this;
		DataLink o2 = (DataLink) o;

		int senderCompare = o1.getReceivesFrom().compareTo(
				o2.getReceivesFrom());
		if (senderCompare != 0) {
			return senderCompare;
		}

		int receiverCompare = o1.getSendsTo().compareTo(o2.getSendsTo());
		if (receiverCompare != 0) {
			return receiverCompare;
		}

		if (o1.getMergePosition() == null) {
			if (o2.getMergePosition() == null) {
				return 0;
			}
		}
		if (o2.getMergePosition() == null) {
			return -1;
		}
		return o1.getMergePosition().compareTo(o2.getMergePosition());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DataLink other = (DataLink) obj;
		if (getSendsTo() == null) {
			if (other.getSendsTo() != null) {
				return false;
			}
		} else if (!getSendsTo().equals(other.getSendsTo())) {
			return false;
		}
		if (getReceivesFrom() == null) {
			if (other.getReceivesFrom() != null) {
				return false;
			}
		} else if (!getReceivesFrom().equals(other.getReceivesFrom())) {
			return false;
		}
		return true;
	}

	public Integer getMergePosition() {
		return mergePosition;
	}


	public Workflow getParent() {
		return parent;
	}

	public SenderPort getReceivesFrom() {
		return receivesFrom;
	}

	public ReceiverPort getSendsTo() {
		return sendsTo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ (getSendsTo() == null ? 0 : getSendsTo().hashCode());
		result = prime * result
		+ (getReceivesFrom() == null ? 0 : getReceivesFrom().hashCode());
		return result;
	}

	public void setMergePosition(Integer mergePosition) {
		this.mergePosition = mergePosition;
	}

	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getDatalinks().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getDatalinks().add(this);
		}
	}

	public void setReceivesFrom(SenderPort receivesFrom) {
		this.receivesFrom = receivesFrom;
	}

	public void setSendsTo(ReceiverPort sendsTo) {
		this.sendsTo = sendsTo;
	}

	@Override
	public String toString() {
		return getReceivesFrom() + "=>" + getSendsTo();
	}

}
