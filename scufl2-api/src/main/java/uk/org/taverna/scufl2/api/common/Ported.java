package uk.org.taverna.scufl2.api.common;

import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.OutputPort;

public interface Ported extends WorkflowBean {

	public NamedSet<? extends InputPort> getInputPorts();

	public NamedSet<? extends OutputPort> getOutputPorts();

}
