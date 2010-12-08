package uk.org.taverna.scufl2.api.activity;

import java.net.URI;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.Profile;


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
public class Activity extends AbstractNamedChild implements Configurable,
		Child<Profile> {

	private NamedSet<InputActivityPort> inputPorts = new NamedSet<InputActivityPort>();
	private NamedSet<OutputActivityPort> outputPorts = new NamedSet<OutputActivityPort>();

	private URI type;
	private Profile parent;

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

	@Override
	public Profile getParent() {
		return parent;
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

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getActivities().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getActivities().add(this);
		}
	}

	public void setType(URI type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Activity " + getType() + " \"" + getName() + '"';
	}

}
