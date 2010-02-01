package net.sf.taverna.scufl2.api.common;

/**
 * @author alanrw
 *
 */
public interface Named extends WorkflowBean {

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @param name
	 */
	public void setName(String name);

}
