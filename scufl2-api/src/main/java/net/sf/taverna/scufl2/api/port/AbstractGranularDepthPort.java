package net.sf.taverna.scufl2.api.port;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author alanrw
 *
 */
public abstract class AbstractGranularDepthPort extends AbstractDepthPort implements
		Port {

	/**
	 * @param name
	 */
	public AbstractGranularDepthPort(String name) {
		super(name);
	}
	
	public AbstractGranularDepthPort() {
		super();
	}

	private Integer granularDepth;

	/**
	 * @param granularDepth
	 */
	public void setGranularDepth(Integer granularDepth) {
		this.granularDepth = granularDepth;
	}

	/**
	 * @return
	 */
	@XmlElement(required=true,nillable=false)
	public Integer getGranularDepth() {
		return granularDepth;
	}

}
