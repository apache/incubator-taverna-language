package net.sf.taverna.scufl2.api.port;

import javax.xml.bind.annotation.XmlElement;

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
	
	public AbstractDepthPort() {
		super();
	}

	private Integer depth;

	/**
	 * @return
	 */
	@XmlElement(required=true,nillable=false)
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
