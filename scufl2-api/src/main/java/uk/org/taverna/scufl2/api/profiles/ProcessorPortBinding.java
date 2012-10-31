package uk.org.taverna.scufl2.api.profiles;

import uk.org.taverna.scufl2.api.common.AbstractCloneable;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
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
public abstract class ProcessorPortBinding<A extends ActivityPort, P extends ProcessorPort>
		extends AbstractCloneable implements Child<ProcessorBinding> {

	private P boundProcessorPort;
	private A boundActivityPort;

	
	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the <code>InputActivityPort</code> to which data is actually sent when passed to the
	 * bound <code>InputProcessorPort</code>.
	 * 
	 * @return the <code>InputActivityPort</code> to which data is actually sent when passed to the
	 *         bound <code>InputProcessorPort</code>
	 */
	public A getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * Returns the <code>InputProcessorPort</code> that the binding is for.
	 * 
	 * @return the <code>InputProcessorPort</code> that the binding is for
	 */
	public P getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/**
	 * Sets the <code>InputActivityPort</code> to which data is actually sent when passed to the
	 * bound <code>InputProcessorPort</code>.
	 * 
	 * @param boundActivityPort
	 *            the <code>InputActivityPort</code> to which data is actually sent when passed to
	 *            the bound <code>InputProcessorPort</code>
	 */
	public void setBoundActivityPort(A boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/**
	 * Sets the InputProcessorPort that the binding is for.
	 * 
	 * @param boundProcessorPort
	 *            the InputProcessorPort that the binding is for
	 */
	public void setBoundProcessorPort(P boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		@SuppressWarnings("unchecked")
		ProcessorPortBinding<A,P> cloneBinding = (ProcessorPortBinding<A, P>) clone;
		cloneBinding.setBoundActivityPort(cloning.cloneOrOriginal(getBoundActivityPort()));
		cloneBinding.setBoundProcessorPort(cloning.cloneOrOriginal(getBoundProcessorPort()));
	}


}
