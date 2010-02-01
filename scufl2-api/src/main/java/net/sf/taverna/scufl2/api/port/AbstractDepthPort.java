package net.sf.taverna.scufl2.api.port;

import net.sf.taverna.scufl2.api.common.AbstractNamed;

/**
 * @author alanrw
 *
 */
public abstract class AbstractDepthPort extends AbstractNamed implements Port {

	/**
	 * @param name
	 */
	public AbstractDepthPort(String name) {
		super(name);
	}

	private Integer depth;

	/**
	 * @return
	 */
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
