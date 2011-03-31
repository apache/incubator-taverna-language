package uk.org.taverna.scufl2.api.port;

/**
 * Abstract implementation of a <code>Port</code> that has a granular depth property.
 * <p>
 * The granular depth of a <code>Port </code> specifies the granularity of the depth at which data
 * is emitted. The granular depth must be less than or equal to the depth.
 * <p>
 * For example, if a <code>Port</code> has a depth of 1 and a granular depth of 0 the
 * <code>Port</code> will emit each element of the list separately.
 * 
 * @author Alan R Williams
 * 
 */
public abstract class AbstractGranularDepthPort extends AbstractDepthPort implements Port {

	private Integer granularDepth;

	/**
	 * Constructs an <code>AbstractGranularDepthPort</code> with a random UUID as the name.
	 */
	public AbstractGranularDepthPort() {
		super();
	}

	/**
	 * Constructs an <code>AbstractGranularDepthPort</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be
	 *            <code>null</code> or an empty String.
	 */
	public AbstractGranularDepthPort(String name) {
		super(name);
	}

	/**
	 * Returns the granular depth of the <code>Port</code>.
	 * 
	 * @return the granular depth of the <code>Port</code>
	 */
	public Integer getGranularDepth() {
		return granularDepth;
	}

	/**
	 * Sets the granular depth of the <code>Port</code>.
	 * 
	 * @param granularDepth
	 *            the granular depth of the <code>Port</code>
	 */
	public void setGranularDepth(Integer granularDepth) {
		this.granularDepth = granularDepth;
	}

}
