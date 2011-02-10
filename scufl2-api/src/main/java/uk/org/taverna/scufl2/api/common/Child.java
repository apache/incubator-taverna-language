package uk.org.taverna.scufl2.api.common;

/**
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 *
 * @param <T>
 */
public interface Child<T extends WorkflowBean> extends WorkflowBean {

	/**
	 * Return the parent of this workflow bean, or <code>null</code> if it is
	 * orphan.
	 *
	 * @return
	 */
	T getParent();

	/**
	 * Set the parent of this workflow bean.
	 * <p>
	 * Setting the parent would normally also add the object to the relevant
	 * collection in the parent if it does not already exist there.
	 * <p>
	 * If the child has an existing, object-identity different parent, the child
	 * will first be removed from the parent collection if it exists there.
	 * <p>
	 * <strong>Note:</strong>If the child is {@link Named} the parent collection
	 * will be a {@link NamedSet}. This implicit insertion would overwrite any
	 * conflicting sibling with the same {@link Named#getName()} - to avoid
	 * this, add the child to the parent collection by using
	 * {@link NamedSet#addWithUniqueName(Named)} before setting the parent.
	 * <p>
	 *
	 * @param parent
	 */
	void setParent(T parent);

}
