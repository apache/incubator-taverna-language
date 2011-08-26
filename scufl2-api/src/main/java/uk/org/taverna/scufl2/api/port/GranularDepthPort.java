package uk.org.taverna.scufl2.api.port;

public interface GranularDepthPort extends DepthPort {
	/**
	 * Returns the granular depth of the <code>Port</code>.
	 * 
	 * @return the granular depth of the <code>Port</code>
	 */
	public Integer getGranularDepth();

	/**
	 * Sets the granular depth of the <code>Port</code>.
	 * 
	 * @param granularDepth
	 *            the granular depth of the <code>Port</code>
	 */
	public void setGranularDepth(Integer granularDepth);
}
