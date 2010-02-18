package net.sf.taverna.scufl2.api.common;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author alanrw
 *
 */
public class AbstractNamed implements Named {
	
	public AbstractNamed(String name) {
		setName(name);
	}
	
	public AbstractNamed() {
		setName("missing");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AbstractNamed other = (AbstractNamed) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	private String name;

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Named#getName()
	 */
	@XmlElement(required=true,nillable=false)
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.scufl2.api.common.Named#setName(java.lang.String)
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("Name can't be null");
		}
		if (name.length() == 0) {
			throw new IllegalArgumentException("Name can't be empty");
		}
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " \"" + getName()+ '"';
	}
	
}
