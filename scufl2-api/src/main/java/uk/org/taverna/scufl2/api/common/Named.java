package uk.org.taverna.scufl2.api.common;

/**
 * @author Alan R Williams
 *
 */
@SuppressWarnings("rawtypes")
public interface Named extends WorkflowBean, Comparable {

	public String getName();

	public void setName(String name);

}
