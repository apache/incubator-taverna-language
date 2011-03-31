package uk.org.taverna.scufl2.api.common;

import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.OutputPort;

/**
 * A {@link WorkflowBean} that has {@link uk.org.taverna.scufl2.api.portInputPort InputPorts} and
 * {@link uk.org.taverna.scufl2.api.portOutputPort OutputPorts}.
 */
public interface Ported extends WorkflowBean {

	/**
	 * Returns the {@link uk.org.taverna.scufl2.api.port.InputPort InputPorts}.
	 * 
	 * @return the input ports
	 */
	public NamedSet<? extends InputPort> getInputPorts();

	/**
	 * Returns the {@link uk.org.taverna.scufl2.api.port.OutputPort OutputPorts}.
	 * 
	 * @return the output ports
	 */
	public NamedSet<? extends OutputPort> getOutputPorts();

}
