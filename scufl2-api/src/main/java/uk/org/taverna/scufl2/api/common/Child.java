package uk.org.taverna.scufl2.api.common;

/**
 * @author Alan R Williams
 *
 * @param <T>
 */
public interface Child<T extends WorkflowBean> {
	
	/**
	 * @return
	 */
	T getParent();
	
	/**
	 * @param parent
	 */
	void setParent(T parent);

}
