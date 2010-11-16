package uk.org.taverna.scufl2.api.activity;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;


/**
 * 
 * An Activity specifies a way of implementing a Processor within a Workflow.
 * When the Workflow is run, a particular Activity will be specified as bound to
 * the Processor and Configuration information will be specified for the
 * Activity.
 * 
 * @author Alan R Williams
 * 
 */
public class Activity extends AbstractNamed implements Configurable {

	private NamedSet<InputActivityPort> inputPorts = new NamedSet<InputActivityPort>();
	private NamedSet<OutputActivityPort> outputPorts = new NamedSet<OutputActivityPort>();

	private URI type;

	public Activity() {
		super();
	}

	public Activity(String name) {
		super(name);
	}

	public NamedSet<InputActivityPort> getInputPorts() {
		return inputPorts;
	}

	public NamedSet<OutputActivityPort> getOutputPorts() {
		return outputPorts;
	}

	public URI getType() {
		return type;
	}

	public void setInputPorts(Set<InputActivityPort> inputPorts) {
		this.inputPorts.clear();
		this.inputPorts.addAll(inputPorts);
	}


	public void setOutputPorts(Set<OutputActivityPort> outputPorts) {
		this.outputPorts.clear();
		this.outputPorts.addAll(outputPorts);
	}

	public void setType(URI type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Activity " + getType() + " \"" + getName() + '"';
	}

}
