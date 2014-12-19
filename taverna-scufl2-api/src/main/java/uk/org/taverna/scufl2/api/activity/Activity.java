package uk.org.taverna.scufl2.api.activity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.Typed;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * An Activity specifies a way of implementing a {@link Processor Processor}
 * within a {@link Workflow Workflow}.
 * <p>
 * When the Workflow is run, a particular Activity will be specified as bound to
 * the Processor and {@link Configuration Configuration} information will be
 * specified for the Activity.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Activity extends AbstractNamed implements Configurable,
		Child<Profile>, Typed, Ported {
	private final NamedSet<InputActivityPort> inputPorts = new NamedSet<>();
	private final NamedSet<OutputActivityPort> outputPorts = new NamedSet<>();

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
	 *            the name of the Activity. <strong>Must not</strong> be
	 *            <code>null</code> or an empty String.
	 */
	public Activity(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<>();
			children.add(getInputPorts());
			children.add(getOutputPorts());
			outer: for (Iterable<? extends WorkflowBean> child : children)
				for (WorkflowBean bean : child)
					if (!bean.accept(visitor))
						break outer;
		}
		return visitor.visitLeave(this);
	}

	/**
	 * Returns the type of the <code>Activity</code>.
	 * 
	 * @return the type of the <code>Activity</code>
	 */
	@Override
	public URI getType() {
		return type;
	}

	/**
	 * Returns the <code>InputActivityPort</code>s.
	 * 
	 * If there are no <code>InputActivityPort</code>s an empty set is returned.
	 * 
	 * @return the <code>InputActivityPort</code>s.
	 */
	@Override
	public NamedSet<InputActivityPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * Returns the <code>OutputActivityPort</code>s.
	 * 
	 * If there are no <code>OutputActivityPort</code>s an empty set is
	 * returned.
	 * 
	 * @return the <code>OutputActivityPort</code>s.
	 */
	@Override
	public NamedSet<OutputActivityPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	/**
	 * Sets the type of the <code>Activity</code>.
	 * 
	 * @param type
	 *            the type of the <code>Activity</code>
	 */
	@Override
	public void setType(URI type) {
		this.type = type;
	}

	/**
	 * Set the <code>InputActivityPort</code>s to be the contents of the
	 * specified set.
	 * <p>
	 * <code>InputActivityPort</code>s can be added by using
	 * {@link #getInputPorts()}.add(inputPort).
	 * 
	 * @param inputPorts
	 *            the <code>InputActivityPort</code>s. <strong>Must not</strong>
	 *            be null
	 */
	public void setInputPorts(Set<InputActivityPort> inputPorts) {
		this.inputPorts.clear();
		this.inputPorts.addAll(inputPorts);
	}

	/**
	 * Set the <code>OutputActivityPort</code>s to be the contents of the
	 * specified set.
	 * <p>
	 * <code>OutputActivityPort</code>s can be added by using
	 * {@link #getOutputPorts()}.add(outputPort).
	 * 
	 * @param outputPorts
	 *            the <code>OutputActivityPort</code>s. <strong>Must
	 *            not</strong> be null
	 */
	public void setOutputPorts(Set<OutputActivityPort> outputPorts) {
		this.outputPorts.clear();
		this.outputPorts.addAll(outputPorts);
	}

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getActivities().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getActivities().add(this);
	}

	@Override
	public String toString() {
		return "Activity " + getType() + " \"" + getName() + '"';
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		Activity cloneActivity = (Activity) clone;
		cloneActivity.setType(getType());
	}

	// Derived operations, implemented via Scufl2Tools

	/**
	 * Get all the configurations that are associated with this activity.
	 * Assumes that {@link #setParent(Profile)} has already been called.
	 * 
	 * @see Scufl2Tools#configurationsFor(Activity,Profile)
	 */
	public List<Configuration> getConfigurations() {
		return getTools().configurationsFor(this, getParent());
	}

	/**
	 * Get the configuration for this activity. Assumes that
	 * {@link #setParent(Profile)} has already been called.
	 * 
	 * @throws IllegalStateException
	 *             If there are more than one configuration for the activity.
	 * @throws IndexOutOfBoundsException
	 *             If there aren't any configurations for the activity.
	 * @see Scufl2Tools#configurationFor(Activity,Profile)
	 */
	public Configuration getConfiguration() {
		return getTools().configurationFor(this, getParent());
	}

	/**
	 * Creates a configuration for this activity. Note that much code may assume
	 * that there is only one configuration per activity; this method does
	 * <i>not</i> enforce that and the underlying workflow model does not
	 * require that.
	 * 
	 * @param configType
	 *            The URI of the type of the configuration (i.e., the type of
	 *            the Activity: Beanshell, WSDL, REST, Component, etc.)
	 * @see Scufl2Tools#createConfigurationFor(Activity,URI)
	 */
	public Configuration createConfiguration(URI configType) {
		return getTools().createConfigurationFor(this, configType);
	}

	/**
	 * Create the ports on this activity to match the given processor.
	 * 
	 * @see Scufl2Tools#createActivityPortsFromProcessor(Activity,Processor)
	 */
	public void createPorts(Processor processor) {
		getTools().createActivityPortsFromProcessor(this, processor);
	}

	/**
	 * Get all processors that have a binding to this activity.
	 * 
	 * @return a list of processor bindings (which can be queried for the
	 *         processors themselves).
	 * @see Scufl2Tools#processorBindingsToActivity(Activity)
	 */
	public List<ProcessorBinding> getBoundProcessors() {
		return getTools().processorBindingsToActivity(this);
	}

	/**
	 * Create a processor and binding to match the given activity.
	 * 
	 * @see Scufl2Tools#createProcessorAndBindingFromActivity(Activity)
	 */
	public ProcessorBinding createProcessorAndBinding() {
		return getTools().createProcessorAndBindingFromActivity(this);
	}
}
