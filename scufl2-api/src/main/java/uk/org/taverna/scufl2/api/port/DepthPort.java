package uk.org.taverna.scufl2.api.port;

public interface DepthPort extends Port {
	/**
	 * Returns the depth of the <code>Port</code>.
	 * 
	 * @return the depth of the <code>Port</code>
	 */
	public Integer getDepth() ;

	/**
	 * Sets the depth of the <code>Port</code>.
	 * 
	 * @param depth
	 *            the depth of the <code>Port</code>
	 */
	public void setDepth(Integer depth);
}
