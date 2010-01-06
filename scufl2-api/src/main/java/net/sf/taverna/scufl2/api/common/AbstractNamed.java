package net.sf.taverna.scufl2.api.common;

public class AbstractNamed implements Named {
	
	public AbstractNamed(String name) {
		setName(name);
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

	public String getName() {
		return name;
	}

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
