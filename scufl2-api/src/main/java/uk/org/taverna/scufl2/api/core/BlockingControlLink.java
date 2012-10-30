package uk.org.taverna.scufl2.api.core;

import java.text.MessageFormat;
import java.util.HashMap;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.impl.NullSafeComparator;

/**
 * A {@link ControlLink} that blocks a {@link Processor} from starting until another
 * <code>Processor</code> has finished.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
@SuppressWarnings("rawtypes")
public class BlockingControlLink extends AbstractCloneable implements ControlLink {

	private static NullSafeComparator nullSafeCompare = new NullSafeComparator();
	
	private Workflow parent;
	private Processor block;
	private Processor untilFinished;

	/**
	 * Constructs an unconnected <code>BlockingControlLink</code>.
	 */
	public BlockingControlLink() {
	}

	/**
	 * Constructs a <code>BlockingControlLink</code> with the specified blocked and control <code>Processor</code>s.
	 * <p>
	 * The parent {@link Workflow} is set to be the same as the parent of the block <code>Processor</code>.
	 * 
	 * @param block
	 *            the <code>Processor</code> that is blocked from starting. <strong>Must
	 *            not</strong> be <code>null</code>
	 * @param untilFinished
	 *            the <code>Processor</code> that controls the block. Can be <code>null</code>.
	 */
	public BlockingControlLink(Processor block, Processor untilFinished) {
		setUntilFinished(untilFinished);
		setBlock(block);
		setParent(block.getParent());
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof BlockingControlLink)) {
			return o.getClass().getCanonicalName().compareTo(getClass().getCanonicalName());
		}
		BlockingControlLink o1 = this;		
		BlockingControlLink o2 = (BlockingControlLink) o;		
		int untilFinishedCompare = nullSafeCompare.compare(o1.getUntilFinished(), o2.getUntilFinished());
		if (untilFinishedCompare != 0) {
			return untilFinishedCompare;
		}
		return nullSafeCompare.compare(o1.getBlock(), o2.getBlock());
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

	/**
	 * Returns the <code>Processor</code> that is blocked from starting.
	 * 
	 * @return the <code>Processor</code> that is blocked from starting
	 */
	public Processor getBlock() {
		return block;
	}

	@Override
	public Workflow getParent() {
		return parent;
	}

	/**
	 * Returns the <code>Processor</code> that controls the block.
	 * 
	 * @return the <code>Processor</code> that controls the block
	 */
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

	/**
	 * Sets the <code>Processor</code> that is blocked from starting.
	 * 
	 * @param block
	 *            the <code>Processor</code> that is blocked from starting. Can be <code>null</code>
	 */
	public void setBlock(Processor block) {
		this.block = block;
	}

	@Override
	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getControlLinks().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getControlLinks().add(this);
		}
	}

	/**
	 * Sets the <code>Processor</code> that controls the block.
	 * 
	 * @param untilFinished
	 *            the <code>Processor</code> that controls the block. Can be <code>null</code>
	 */
	public void setUntilFinished(Processor untilFinished) {
		this.untilFinished = untilFinished;
	}

	@Override
	public String toString() {
		String blockName = getBlock() != null ? getBlock().getName() : null;
		String untilName = getUntilFinished() != null ? getUntilFinished().getName() : null;
		
		return MessageFormat.format("{0}-blocks-{1}", untilName, blockName);
	}

	@Override
	protected void cloneInto(WorkflowBean clone,
			HashMap<WorkflowBean, WorkflowBean> cloned) {
		BlockingControlLink cloneLink = (BlockingControlLink)clone;
		cloneLink.setBlock((Processor) cloned.get(getBlock()));
		cloneLink.setUntilFinished((Processor) cloned.get(getUntilFinished()));
		
	}
	
}
