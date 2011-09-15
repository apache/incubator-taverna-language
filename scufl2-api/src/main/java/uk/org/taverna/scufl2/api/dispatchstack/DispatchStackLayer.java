package uk.org.taverna.scufl2.api.dispatchstack;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.Visitor;

/**
 * A <code>DispatchStackLayer</code> adds functionality to a
 * <code>DispatchStack</code>.
 * <p>
 * e.g. a retry layer (of type
 * http://ns.taverna.org.uk/2010/scufl2/taverna#Failover) adds the functionality
 * to retry an Activity if an invocation fails.
 */
public class DispatchStackLayer implements Typed, Child<DispatchStack>,
		Configurable {

	private DispatchStack parent;
	private URI configurableType;

	/**
	 * Constructs a <code>DispatchStackLayer</code>.
	 */
	public DispatchStackLayer() {
	}

	/**
	 * Constructs a <code>DispatchStackLayer</code> for the specified
	 * <code>DispatchStack</code> with the specified type.
	 * 
	 * @param parent
	 *            the <code>DispatchStack</code> to add the
	 *            <code>DispatchStackLayer</code> to
	 * @param configurableType
	 *            the type of the <code>DispatchStackLayer</code>
	 */
	public DispatchStackLayer(DispatchStack parent, URI configurableType) {
		setParent(parent);
		setConfigurableType(configurableType);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the type of the <code>DispatchStackLayer</code>.
	 * 
	 * @return the type of the <code>DispatchStackLayer</code>
	 */
	@Override
	public URI getConfigurableType() {
		return configurableType;
	}

	@Override
	public DispatchStack getParent() {
		return parent;
	}

	/**
	 * Sets the type of the <code>DispatchStackLayer</code>.
	 * 
	 * @param type
	 *            the type of the <code>DispatchStackLayer</code>
	 */
	@Override
	public void setConfigurableType(URI type) {
		configurableType = type;
	}

	@Override
	public void setParent(DispatchStack parent) {
		if (this.parent == parent) {
			return; // No more to do!
		}
		if (this.parent != null) {
			this.parent.remove(this);
		}
		this.parent = parent;
		if (parent != null && !parent.contains(this)) {
			parent.add(this); // Just add to the end
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("DispatchStackLayer");
		if (getConfigurableType() != null) {
			sb.append(" ");
			sb.append(getConfigurableType());
		}
		if (getParent() != null && getParent().getParent() != null) {
			int index = getParent().indexOf(this);
			sb.append(" #");
			sb.append(index);
			sb.append(" in ");
			sb.append(getParent().getParent());
		}
		return sb.toString();
	}
	
}
