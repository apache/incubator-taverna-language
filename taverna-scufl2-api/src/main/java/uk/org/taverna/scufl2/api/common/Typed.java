package uk.org.taverna.scufl2.api.common;

import java.net.URI;

/**
 * A typed {@link WorkflowBean}.
 * 
 * @author Stian Soiland-Reyes
 */
public interface Typed extends WorkflowBean {
	/**
	 * Returns the type of the {@link WorkflowBean}.
	 * 
	 * @return the type of the <code>WorkflowBean</code>
	 */
	URI getType();

	/**
	 * Sets the type of the {@link WorkflowBean}.
	 * 
	 * @param type
	 *            the type of the <code>WorkflowBean</code>.
	 */
	void setType(URI type);
}
