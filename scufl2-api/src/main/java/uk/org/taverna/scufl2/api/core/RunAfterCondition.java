package uk.org.taverna.scufl2.api.core;

/**
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 *
 */
public class RunAfterCondition implements Condition, Comparable {

	private Workflow parent;
	private Processor start;
	private Processor after;

	public RunAfterCondition(Processor start, Processor after) {
		setParent(start.getParent());
		setAfter(after);
		setStart(start);
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
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
		RunAfterCondition other = (RunAfterCondition) obj;
		if (getAfter() == null) {
			if (other.getAfter() != null) {
				return false;
			}
		} else if (!getAfter().equals(other.getAfter())) {
			return false;
		}
		if (getParent() == null) {
			if (other.getParent() != null) {
				return false;
			}
		} else if (!getParent().equals(other.getParent())) {
			return false;
		}
		if (getStart() == null) {
			if (other.getStart() != null) {
				return false;
			}
		} else if (!getStart().equals(other.getStart())) {
			return false;
		}
		return true;
	}

	public Processor getAfter() {
		return after;
	}

	public Workflow getParent() {
		return parent;
	}

	public Processor getStart() {
		return start;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getAfter() == null ? 0 : getAfter().hashCode());
		result = prime * result + (parent == null ? 0 : parent.hashCode());
		result = prime * result + (getStart() == null ? 0 : getStart().hashCode());
		return result;
	}

	public void setAfter(Processor after) {
		this.after = after;
	}

	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getConditions().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getConditions().add(this);
		}
	}

	public void setStart(Processor start) {
		this.start = start;
	}

	@Override
	public String toString() {
		return "after " + getAfter() + " start" + getStart();
	}

}
