/**
 *
 */
package uk.org.taverna.scufl2.api.common;

/**
 * @author Alan R Williams
 *
 */
public class ToBeDecided implements WorkflowBean {

	@Override
	public boolean accept(Visitor visitor) {
		return false;
	}

}
