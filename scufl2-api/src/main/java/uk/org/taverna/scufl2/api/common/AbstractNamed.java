package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.UUID;

/**
 * @author Alan R Williams
 *
 */
public abstract class AbstractNamed implements Named {

	private String name;

	public AbstractNamed() {
		setName(UUID.randomUUID().toString());
	}

	public AbstractNamed(String name) {
		setName(name);
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
		AbstractNamed other = (AbstractNamed) obj;
		if (!getName().equals(other.getName())) {
			return false;
		}
		if (this instanceof Child) {
			WorkflowBean parent = ((Child<?>) this).getParent();
			WorkflowBean otherParent = ((Child<?>) other).getParent();
			if (parent != null) {
				return parent.equals(otherParent);
			}
			if (parent == null && otherParent != null) {
				return false;
			}
		}
		if (this instanceof Typed) {
			URI myId = ((Typed) this).getType();
			URI otherId = ((Typed) obj).getType();
			if (myId != null) {
				return myId.equals(otherId);
			}
			if (myId == null && otherId != null) {
				return false;
			}
		}
		if (this instanceof Identified) {
			URI myId = ((Identified)this).getIdentifier();
			URI otherId = ((Identified)obj).getIdentifier();
			if (myId != null) {
				return myId.equals(otherId);
			}
			if (myId == null && otherId != null) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)AbstractNamed
	 * 
	 * @see uk.org.taverna.scufl2.api.common.Named#getName()
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());

		if (this instanceof Child) {
			WorkflowBean parent = ((Child) this).getParent();
			if (parent != null) {
				result = prime * result + parent.hashCode();
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.api.common.Named#setName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("Name can't be null");
		}
		if (name.length() == 0) {
			throw new IllegalArgumentException("Name can't be empty");
		}

		if (this instanceof Child) {
			Child child = (Child) this;
			WorkflowBean parent = child.getParent();
			if (parent != null) {
				child.setParent(null);
				this.name = name;
				// Might overwrite other Named object with same name
				child.setParent(parent);
			}
		}
		this.name = name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " \"" + getName()+ '"';
	}

}
