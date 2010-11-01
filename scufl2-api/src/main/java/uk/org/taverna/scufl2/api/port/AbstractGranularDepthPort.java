package uk.org.taverna.scufl2.api.port;


/**
 * @author Alan R Williams
 *
 */
public abstract class AbstractGranularDepthPort extends AbstractDepthPort implements
		Port {

	private Integer granularDepth;
	
	public AbstractGranularDepthPort() {
		super();
	}

	/**
	 * @param name
	 */
	public AbstractGranularDepthPort(String name) {
		super(name);
	}

	/**
	 * @return
	 */
	public Integer getGranularDepth() {
		return granularDepth;
	}

	/**
	 * @param granularDepth
	 */
	public void setGranularDepth(Integer granularDepth) {
		this.granularDepth = granularDepth;
	}

}
