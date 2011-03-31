package uk.org.taverna.scufl2.api.activity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * An Activity specifies a way of implementing a {@link uk.org.taverna.scufl2.api.core.Processor Processor}
 * within a {@link uk.org.taverna.scufl2.api.core.Workflow Workflow}.
 * <p>
 * When the Workflow is run, a particular Activity will be specified as bound to the Processor and
 * {@link uk.org.taverna.scufl2.api.configurations.Configuration Configuration} information will be specified for the Activity.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Activity extends AbstractNamedChild implements Configurable, Child<Profile>, Typed,
		Ported {

	private NamedSet<InputActivityPort> inputPorts = new NamedSet<InputActivityPort>();
	private NamedSet<OutputActivityPort> outputPorts = new NamedSet<OutputActivityPort>();

	private URI type;
	private Profile parent;

	/**
	 * Constructs an <code>Activity</code> with a random UUID as the name.
	 */
	public Activity() {
		super();
	}

	/**
	 * Constructs an <code>Activity</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the Activity. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public Activity(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getInputPorts());
			children.add(getOutputPorts());
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
		}
		return visitor.visitLeave(this);
	}

	@Override
	public URI getConfigurableType() {
		return type;
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

	@Override
	public void setConfigurableType(URI type) {
		this.type = type;
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

	@Override
	public String toString() {
		return "Activity " + getConfigurableType() + " \"" + getName() + '"';
	}
}
