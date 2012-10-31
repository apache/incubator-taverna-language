package uk.org.taverna.scufl2.api.dispatchstack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * A <code>DispatchStack</code> controls how a {@link Processor} invokes an
 * {@link uk.org.taverna.scufl2.api.activity.Activity Activity}.
 * <p>
 * A <code>DispatchStack</code> consists of a list of <code>DispatchStackLayer</code>s. A typical
 * <code>DispatchStack</code> will contain <code>DispatchStackLayer</code>s for parallelization,
 * retry, failover and invocation. The order of the layers controls the behavior of the
 * <code>Processor</code>.
 */
public class DispatchStack extends ArrayList<DispatchStackLayer> implements
List<DispatchStackLayer>, Child<Processor> {

	private static final long serialVersionUID = 1L;

	private URI type;

	private Processor parent;

	/**
	 * Constructs a <code>DispatchStack</code>.
	 */
	public DispatchStack() {
	}

	/**
	 * Constructs a <code>DispatchStack</code> for the specified <code>Processor</code>.
	 */
	public DispatchStack(Processor parent) {
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (WorkflowBean bean : this) {
				if (!bean.accept(visitor)) {
					break;
				}
			}
		}
		return visitor.visitLeave(this);
	}

	@Override
	public Processor getParent() {
		return parent;
	}

	/**
	 * TODO find out what this is for
	 * 
	 * @return
	 */
	public URI getType() {
		return type;
	}

	@Override
	public void setParent(Processor parent) {
		if (this.parent == parent) {
			return;
		}
		if (this.parent != null && this.parent.getDispatchStack() == this) {
			this.parent.setDispatchStack(null);
		}
		this.parent = parent;
		if (parent != null) {
			parent.setDispatchStack(this);
		}
	}

	/**
	 * TODO find out what this is for
	 * 
	 * @param type
	 */
	public void setType(URI type) {
		this.type = type;
	}

	@Override
	public WorkflowBean clone() {
		return AbstractCloneable.cloneWorkflowBean(this);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + getParent();
	}

}
