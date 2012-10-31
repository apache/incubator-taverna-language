package uk.org.taverna.scufl2.api.common;

/**
 * The top level interface for all objects in a workflow.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 */
public interface WorkflowBean extends Cloneable {

	/**
	 * Accepts a {@link Visitor} to this <code>WorkflowBean</code>.
	 * 
	 * @param visitor
	 *            the <code>Visitor</code> to accept
	 * @return <code>true</code> if this <code>WorkflowBeans</code> children should be visited.
	 */
	public boolean accept(Visitor visitor);

	/**
	 * Create a deep clone of this bean.
	 * <p>
	 * The cloned bean will have equivalent properties as the original bean. Any
	 * {@link Child} beans which parent match this bean will be cloned as well
	 * (recursively), non-child beans will remain the same. If this bean is a
	 * {@link Child}, the returnbed clone will not have a parent set.
	 * 
	 * @return A cloned workflow bean
	 */
	//@Override
	public WorkflowBean clone();
	
}
