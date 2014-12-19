package uk.org.taverna.scufl2.api.port;

public interface DepthPort extends Port {
	/**
	 * Returns the depth of the <code>Port</code>.
	 * 
	 * @return the depth of the <code>Port</code>
	 */
	Integer getDepth();

	/**
	 * Sets the depth of the <code>Port</code>.
	 * 
	 * @param depth
	 *            the depth of the <code>Port</code>
	 */
	void setDepth(Integer depth);
}
