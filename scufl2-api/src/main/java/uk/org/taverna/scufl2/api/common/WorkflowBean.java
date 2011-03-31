package uk.org.taverna.scufl2.api.common;

/**
 * The top level interface for all objects in a workflow.
 * 
 * @author Alan R Williams
 */
public interface WorkflowBean {

	/**
	 * Accepts a {@link Visitor} to this <code>WorkflowBean</code>.
	 * 
	 * @param visitor
	 *            the <code>Visitor</code> to accept
	 * @return <code>true</code> if this <code>WorkflowBeans</code> children should be visited.
	 */
	public boolean accept(Visitor visitor);
	
}
