package uk.org.taverna.scufl2.api.common;

/**
 * A named {@link WorkflowBean}.
 * 
 * @author Alan R Williams
 */
@SuppressWarnings("rawtypes")
public interface Named extends WorkflowBean, Comparable {

	/**
	 * Returns the name of the {@link WorkflowBean}.
	 * 
	 * @return  the name of the <code>WorkflowBean</code>
	 */
	public String getName();

	/**
	 * Sets the name of the {@link WorkflowBean}.
	 * 
	 * The name <strong>must not</strong> be <code>null</code> or an empty String.
	 * 
	 * @param name the name of the <code>WorkflowBean</code>
	 */
	public void setName(String name);

}
