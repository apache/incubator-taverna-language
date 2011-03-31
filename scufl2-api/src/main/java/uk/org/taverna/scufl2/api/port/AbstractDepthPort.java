package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Visitor;

/**
 * Abstract implementation of a <code>Port</code> that has a depth property.
 * <p>
 * The depth of a <code>Port </code> specifies whether the data is a list and how deep lists are
 * nested. A depth of 0 is a single element, depth 1 is a list, depth 2 is a list of lists and so
 * on.
 * 
 * @author Alan R Williams
 */
public abstract class AbstractDepthPort extends AbstractNamedChild implements Port {

	private Integer depth;

	/**
	 * Constructs an <code>AbstractDepthPort</code> with a random UUID as the name.
	 */
	public AbstractDepthPort() {
		super();
	}

	/**
	 * Constructs an <code>AbstractDepthPort</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be
	 *            <code>null</code> or an empty String.
	 */
	public AbstractDepthPort(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the depth of the <code>Port</code>.
	 * 
	 * @return the depth of the <code>Port</code>
	 */
	public Integer getDepth() {
		return depth;
	}

	/**
	 * Sets the depth of the <code>Port</code>.
	 * 
	 * @param depth
	 *            the depth of the <code>Port</code>
	 */
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

}
