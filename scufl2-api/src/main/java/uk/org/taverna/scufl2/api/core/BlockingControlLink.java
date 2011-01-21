package uk.org.taverna.scufl2.api.core;

import java.text.MessageFormat;

import uk.org.taverna.scufl2.api.common.Visitor;

/**
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 *
 */
@SuppressWarnings("rawtypes")
public class BlockingControlLink implements ControlLink, Comparable {

	private Workflow parent;
	private Processor block;
	private Processor untilFinished;

	public BlockingControlLink(Processor block, Processor untilFinished) {
		setParent(block.getParent());
		setUntilFinished(untilFinished);
		setBlock(block);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof BlockingControlLink)) {
			return o.getClass().getCanonicalName()
					.compareTo(getClass().getCanonicalName());
		}
		BlockingControlLink o1 = this;
		BlockingControlLink o2 = (BlockingControlLink) o;

		int untilFinished = o1.getUntilFinished().compareTo(
				o2.getUntilFinished());
		if (untilFinished != 0) {
			return untilFinished;
		}

		int block = o1.getBlock().compareTo(o2.getBlock());
		return block;
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
		BlockingControlLink other = (BlockingControlLink) obj;
		if (getUntilFinished() == null) {
			if (other.getUntilFinished() != null) {
				return false;
			}
		} else if (!getUntilFinished().equals(other.getUntilFinished())) {
			return false;
		}
		if (getParent() == null) {
			if (other.getParent() != null) {
				return false;
			}
		} else if (!getParent().equals(other.getParent())) {
			return false;
		}
		if (getBlock() == null) {
			if (other.getBlock() != null) {
				return false;
			}
		} else if (!getBlock().equals(other.getBlock())) {
			return false;
		}
		return true;
	}

	public Processor getBlock() {
		return block;
	}

	public Workflow getParent() {
		return parent;
	}

	public Processor getUntilFinished() {
		return untilFinished;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getUntilFinished() == null ? 0 : getUntilFinished().hashCode());
		result = prime * result + (parent == null ? 0 : parent.hashCode());
		result = prime * result + (getBlock() == null ? 0 : getBlock().hashCode());
		return result;
	}

	public void setBlock(Processor block) {
		this.block = block;
	}

	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getControlLinks().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getControlLinks().add(this);
		}
	}

	public void setUntilFinished(Processor untilFinished) {
		this.untilFinished = untilFinished;
	}

	@Override
	public String toString() {
		return MessageFormat.format("block {0} until {1} is finished",
				getBlock(), getUntilFinished());
	}

}
