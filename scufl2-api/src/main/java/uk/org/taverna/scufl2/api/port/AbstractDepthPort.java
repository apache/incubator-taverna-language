package uk.org.taverna.scufl2.api.port;

import uk.org.taverna.scufl2.api.common.AbstractNamed;


/**
 * @author Alan R Williams
 *
 */
public abstract class AbstractDepthPort extends AbstractNamed implements Port {

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
