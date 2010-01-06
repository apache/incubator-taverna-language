package net.sf.taverna.scufl2.api.port;

import net.sf.taverna.scufl2.api.common.AbstractNamed;

public abstract class AbstractDepthPort extends AbstractNamed implements Port {

	public AbstractDepthPort(String name) {
		super(name);
	}

	private Integer depth;

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

}
