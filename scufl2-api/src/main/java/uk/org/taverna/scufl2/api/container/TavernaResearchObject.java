package uk.org.taverna.scufl2.api.container;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.reference.Reference;

/**
 * @author Alan R Williams
 * 
 */

@XmlRootElement
@XmlType(propOrder = { "workflows", "mainWorkflowReference", "activities",
		"profiles" })
public class TavernaResearchObject implements ResearchObject, WorkflowBean {

	private NamedSet<Profile> profiles = new NamedSet<Profile>();
	private Workflow mainWorkflow;
	private NamedSet<Workflow> workflows = new NamedSet<Workflow>();
	private NamedSet<Activity> activities = new NamedSet<Activity>();

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "activities", nillable = false, required = true)
	@XmlElement(name = "activity", nillable = false)
	public NamedSet<Activity> getActivities() {
		return activities;
	}

	/**
	 * @return
	 */
	@XmlTransient
	public Workflow getMainWorkflow() {
		if (mainWorkflow == null) {
			mainWorkflow = new Workflow();
		}
		return mainWorkflow;
	}

	public Reference<Workflow> getMainWorkflowReference() {
		return Reference.createReference(mainWorkflow);
	}

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "profiles", nillable = false, required = true)
	@XmlElement(name = "profile", nillable = false)
	public NamedSet<Profile> getProfiles() {
		return profiles;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "workflows", nillable = false, required = true)
	@XmlElement(name = "workflow", nillable = false)
	public NamedSet<Workflow> getWorkflows() {
		return workflows;
	}

	/**
	 * @param activities
	 */
	public void setActivities(Set<Activity> activities) {
		this.activities.clear();
		this.activities.addAll(activities);
	}

	/**
	 * @param mainWorkflow
	 */
	public void setMainWorkflow(Workflow mainWorkflow) {
		this.mainWorkflow = mainWorkflow;
	}

	public void setMainWorkflowReference(
			Reference<Workflow> mainWorkflowReference) {
		mainWorkflow = mainWorkflowReference.resolve();
	}

	/**
	 * @param profiles
	 */
	public void setProfiles(Set<Profile> profiles) {
		this.profiles.clear();
		this.profiles.addAll(profiles);
	}

	/**
	 * @param workflows
	 */
	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows.clear();
		this.workflows.addAll(workflows);
	}

	@Override
	public String toString() {
		final int maxLen = 6;
		return "TavernaResearchObject [activities="
		+ (activities != null ? toString(activities, maxLen) : null)
				+ ", profiles="
				+ (profiles != null ? toString(profiles, maxLen) : null)
		+ ", mainWorkflow=" + mainWorkflow + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
		&& i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}
