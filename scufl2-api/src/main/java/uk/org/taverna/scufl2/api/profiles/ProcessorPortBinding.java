package uk.org.taverna.scufl2.api.profiles;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.ProcessorPort;

/**
 * The binding between an <code>ActivityPort</code> and a <code>ProcessorPort</code>.
 * 
 * @param <A>
 *            the <code>ActivityPort</code>
 * @param <P>
 *            the <code>ProcessorPort</code>
 */
public interface ProcessorPortBinding<A extends ActivityPort, P extends ProcessorPort> extends
Child<ProcessorBinding> {

	/**
	 * Returns the bound <code>ActivityPort</code>
	 * 
	 * @return the bound <code>ActivityPort</code>
	 */
	A getBoundActivityPort();

	/**
	 * Returns the bound <code>ProcessorPort</code>
	 * 
	 * @return the bound <code>ProcessorPort</code>
	 */
	P getBoundProcessorPort();

	/**
	 * Sets the bound <code>ProcessorPort</code>
	 * 
	 * @param boundActivityPort
	 *            the bound <code>ProcessorPort</code>
	 */
	void setBoundActivityPort(A boundActivityPort);

	/**
	 * Sets the bound <code>ProcessorPort</code>
	 * 
	 * @param boundProcessorPort
	 *            the bound <code>ProcessorPort</code>
	 */
	void setBoundProcessorPort(P boundProcessorPort);

}
