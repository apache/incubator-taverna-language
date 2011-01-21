package uk.org.taverna.scufl2.api.profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A Profile specifies a set of compatible ProcessorBindings. For example, one
 * Profile could contain ways of enacting a set of Processors on a grid whilst
 * another contained ways of enacting the Processors on a laptop.
 *
 * @author Alan R Williams
 *
 */
@XmlType(propOrder = { "profilePosition", "processorBindings", "configurations" })
public class Profile extends AbstractNamedChild implements
Child<WorkflowBundle> {

	private NamedSet<ProcessorBinding> processorBindings = new NamedSet<ProcessorBinding>();

	private NamedSet<Configuration> configurations = new NamedSet<Configuration>();

	private int profilePosition;

	private NamedSet<Activity> activities = new NamedSet<Activity>();

	private WorkflowBundle parent;

	public Profile() {
		super();
	}

	public Profile(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getActivities());
			children.add(getProcessorBindings());
			children.add(getConfigurations());
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

	public NamedSet<Activity> getActivities() {
		return activities;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "configurations", nillable = false, required = true)
	@XmlElement(name = "configuration", nillable = false)
	public NamedSet<Configuration> getConfigurations() {
		return configurations;
	}

	@Override
	public WorkflowBundle getParent() {
		return parent;
	}

	/**
	 * Return the set of bindings for individual Processors.
	 *
	 * @return
	 */
	@XmlElementWrapper(name = "processorBindings", nillable = false, required = true)
	@XmlElement(name = "processorBinding", nillable = false)
	public NamedSet<ProcessorBinding> getProcessorBindings() {
		return processorBindings;
	}

	/**
	 * Position of this profile within the {@link WorkflowBundle}. When
	 * ordering profiles, they can be sorted by decreasing profilePosition. If
	 * two profiles have the same position, their internal order is
	 * indetermined.
	 *
	 * @return
	 */
	@XmlElement(required = true, nillable = false)
	public final int getProfilePosition() {
		return profilePosition;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities.clear();
		this.activities.addAll(activities);
	}

	/**
	 * @param configurations
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		this.configurations.clear();
		this.configurations.addAll(configurations);
	}

	@Override
	public void setParent(WorkflowBundle parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getProfiles().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getProfiles().add(this);
		}

	}

	/**
	 * @param processorBindings
	 */
	public void setProcessorBindings(Set<ProcessorBinding> processorBindings) {
		this.processorBindings.clear();
		this.processorBindings.addAll(processorBindings);
	}

	public final void setProfilePosition(int profilePosition) {
		this.profilePosition = profilePosition;
	}

}
