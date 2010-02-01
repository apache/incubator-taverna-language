package net.sf.taverna.scufl2.api.common;

/**
 * @author alanrw
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
