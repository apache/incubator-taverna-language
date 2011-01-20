package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Visitor;


/**
 * @author Alan R Williams
 *
 */
public abstract class AbstractDepthPort extends AbstractNamedChild implements Port {

	private Integer depth;

	public AbstractDepthPort() {
		super();
	}

	/**
	 * @param name
	 */
	public AbstractDepthPort(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public Integer getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 */
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

}
