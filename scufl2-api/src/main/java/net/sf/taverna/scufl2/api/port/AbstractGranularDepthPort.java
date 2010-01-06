package net.sf.taverna.scufl2.api.port;

public abstract class AbstractGranularDepthPort extends AbstractDepthPort implements
		Port {

	public AbstractGranularDepthPort(String name) {
		super(name);
	}

	private Integer granularDepth;

	public void setGranularDepth(Integer granularDepth) {
		this.granularDepth = granularDepth;
	}

	public Integer getGranularDepth() {
		return granularDepth;
	}

}
