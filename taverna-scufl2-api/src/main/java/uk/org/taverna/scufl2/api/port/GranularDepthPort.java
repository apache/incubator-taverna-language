package uk.org.taverna.scufl2.api.port;

public interface GranularDepthPort extends DepthPort {
	/**
	 * Returns the granular depth of the <code>Port</code>.
	 * 
	 * @return the granular depth of the <code>Port</code>
	 */
	Integer getGranularDepth();

	/**
	 * Sets the granular depth of the <code>Port</code>.
	 * 
	 * @param granularDepth
	 *            the granular depth of the <code>Port</code>
	 */
	void setGranularDepth(Integer granularDepth);
}
