package net.sf.taverna.scufl2.api.port;

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
	public Integer getGranularDepth() {
		return granularDepth;
	}

}
