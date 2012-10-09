package uk.org.taverna.scufl2.api.common;

import java.net.URI;

/**
 * A typed {@link WorkflowBean}.
 * 
 * @author Stian Soiland-Reyes
 */
public interface Typed extends WorkflowBean {

	// TODO: Change back to getType/setType()
	
	/**
	 * Returns the type of the {@link WorkflowBean}.
	 * 
	 * @return the type of the <code>WorkflowBean</code>
	 */
	public URI getConfigurableType();

	/**
	 * Sets the type of the {@link WorkflowBean}.
	 * 
	 * @param type
	 *            the type of the <code>WorkflowBean</code>.
	 */
	public void setConfigurableType(URI type);

}
