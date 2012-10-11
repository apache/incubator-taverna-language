package uk.org.taverna.scufl2.api.profiles;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.annotation.Revisioned;
import uk.org.taverna.scufl2.api.common.AbstractRevisioned;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A <code>Profile</code> specifies a set of compatible {@link ProcessorBinding}s.
 * <p>
 * For example, one <code>Profile</code> could contain ways of enacting a set of
 * {@link uk.org.taverna.scufl2.api.core.Processor Processor}s on a grid whilst another contained
 * ways of enacting the <code>Processor</code>s on a laptop.
 * 
 * @author Alan R Williams
 */
public class Profile extends AbstractRevisioned implements Child<WorkflowBundle>, Revisioned {

	private final NamedSet<ProcessorBinding> processorBindings = new NamedSet<ProcessorBinding>();

	private final NamedSet<Configuration> configurations = new NamedSet<Configuration>();

	private Integer profilePosition;

	private final NamedSet<Activity> activities = new NamedSet<Activity>();

	private WorkflowBundle parent;

	/**
	 * Constructs a <code>Profile</code> with a random UUID as the name.
	 */
	public Profile() {
		super();
		String profileId = getIdentifierRoot().relativize(getIdentifier())
				.toASCIIString();
		setName("pf-" + profileId);
	}

	/**
	 * Constructs a <code>Profile</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
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

	/**
	 * Returns the <code>Activity</code>s that this <code>Profile</code> contains.
	 * <p>
	 * The <code>Activity</code>s may be bound to <code>Processor</code>s in the
	 * <code>ProcessorBinding</code>s.
	 * 
	 * @return the <code>Activity</code>s that this <code>Profile</code> contains
	 */
	public NamedSet<Activity> getActivities() {
		return activities;
	}

	/**
	 * Returns the <code>Configuration</code>s that this <code>Profile</code> contains.
	 * 
	 * @return the <code>Configuration</code>s that this <code>Profile</code> contains
	 */
	public NamedSet<Configuration> getConfigurations() {
		return configurations;
	}

	@Override
	public WorkflowBundle getParent() {
		return parent;
	}

	/**
	 * Return the set of bindings for individual <code>Processor</code>s.
	 * 
	 * @return the set of bindings for individual <code>Processor</code>s
	 */
	public NamedSet<ProcessorBinding> getProcessorBindings() {
		return processorBindings;
	}

	/**
	 * Return the suggested position of this profile within the {@link WorkflowBundle}.
	 * <p>
	 * If ordering profiles (for instance for displaying them to the user), they 
	 * might be sorted by increasing profilePosition. If two profiles
	 * have the same position, their internal order is undetermined. Profiles with 
	 * profile position <code>null</code> should be sorted last in such a list. 
	 * 
	 * @return the position of this profile within the <code>WorkflowBundle</code>
	 */
	public final Integer getProfilePosition() {
		return profilePosition;
	}

	/**
	 * Sets the <code>Activity</code>s that this <code>Profile</code> contains.
	 * 
	 * @param activities the <code>Activity</code>s that this <code>Profile</code> contains
	 */
	public void setActivities(Set<Activity> activities) {
		this.activities.clear();
		this.activities.addAll(activities);
	}

	/**
	 * Sets the <code>Configuration</code>s that this <code>Profile</code> contains.
	 * 
	 * @param configurations the <code>Configuration</code>s that this <code>Profile</code> contains
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
	 * Sets the bindings for individual <code>Processor</code>s.
	 * 
	 * @param processorBindings the bindings for individual <code>Processor</code>s
	 */
	public void setProcessorBindings(Set<ProcessorBinding> processorBindings) {
		this.processorBindings.clear();
		this.processorBindings.addAll(processorBindings);
	}

	/**
	 * Sets the position of this profile within the {@link WorkflowBundle}.
	 * <p>
	 * When ordering profiles, they can be sorted by decreasing profilePosition. If two profiles
	 * have the same position, their internal order is undetermined.
	 * 
	 * 
	 * @param profilePosition
	 *            the position of this profile within the <code>WorkflowBundle</code>
	 */
	public final void setProfilePosition(Integer profilePosition) {
		this.profilePosition = profilePosition;
	}

	public static final URI PROFILE_ROOT = URI
			.create("http://ns.taverna.org.uk/2012/scufl2-profile/");

	@Override
	protected URI getIdentifierRoot() {
		return PROFILE_ROOT;
	}

	public void setIdentifier(URI workflowIdentifier) {
	}

	public URI getIdentifier() {
		return null;
	}

}
