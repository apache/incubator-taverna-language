package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.UUID;

/**
 * Abstract implementation of a {@link Named} {@link WorkflowBean}.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public abstract class AbstractNamed extends AbstractCloneable implements Named  {

	private String name;

	/**
	 * Constructs a {@link Named} {@link WorkflowBean} with a random UUID as the name.
	 */
	public AbstractNamed() {
		setName(UUID.randomUUID().toString());
	}

	/**
	 * Constructs a {@link Named} {@link WorkflowBean} with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Named</code> <code>WorkflowBean</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public AbstractNamed(String name) {
		setName(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof AbstractNamed)) {
			// Other comparables go first
			return 1;
		}
		AbstractNamed other = (AbstractNamed) o;
		if (other == this) {
			return 0;
		}
		if (this instanceof Child) {
			if (!(other instanceof Child)) {
				// He's orphan, he's sorted first
				return 1;
			}
			WorkflowBean parent = ((Child<?>) this).getParent();
			WorkflowBean otherParent = ((Child<?>) other).getParent();
			if (parent instanceof Comparable && otherParent instanceof Comparable) {
				int comparedParents = ((Comparable) parent).compareTo(otherParent);
				if (comparedParents != 0) {
					return comparedParents;
				}
			}
		} else {
			if (other instanceof Child) {
				// We're orphan, we're first
				return -1;
			}
		}
		if (getClass() != other.getClass()) {
			int classCompare = getClass().getCanonicalName().compareTo(
					other.getClass().getCanonicalName());
			if (classCompare != 0) {
				// Allow having say InputPorts and OutputPorts in the same sorted list
				return classCompare;
			}
		}
		// We're the same class, let's compare the names
		return getName().compareTo(other.getName());
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
			URI myId = ((Typed) this).getConfigurableType();
			URI otherId = ((Typed) obj).getConfigurableType();
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

	@SuppressWarnings({ "rawtypes" })
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.org.taverna.scufl2.api.common.Named#setName(java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		return getClass().getSimpleName() + " \"" + getName() + '"';
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		AbstractNamed namedClone = (AbstractNamed)clone;
		namedClone.setName(getName());
	}

}
