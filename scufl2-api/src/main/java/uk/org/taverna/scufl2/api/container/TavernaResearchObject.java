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
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Bindings;
import uk.org.taverna.scufl2.api.reference.Reference;

/**
 * @author Alan R Williams
 * 
 */

@XmlRootElement
@XmlType(propOrder = { "workflows", "mainWorkflowReference", "activities",
		"configurations", "bindings" })
public class TavernaResearchObject implements ResearchObject, WorkflowBean {

	private NamedSet<Bindings> bindings = new NamedSet<Bindings>();
	private Workflow mainWorkflow;
	private NamedSet<Workflow> workflows = new NamedSet<Workflow>();
	private NamedSet<Activity> activities = new NamedSet<Activity>();
	private NamedSet<Configuration> configurations = new NamedSet<Configuration>();

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "workflows", nillable = false, required = true)
	@XmlElement(name = "workflow", nillable = false)
	public NamedSet<Workflow> getWorkflows() {
		return workflows;
	}

	/**
	 * @param workflows
	 */
	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows.clear();
		this.workflows.addAll(workflows);
	}


	/**
	 * @return
	 */
	@XmlElementWrapper(name = "configurations", nillable = false, required = true)
	@XmlElement(name = "configuration", nillable = false)
	public NamedSet<Configuration> getConfigurations() {
		return configurations;
	}

	/**
	 * @param configurations
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		this.configurations.clear();
		this.configurations.addAll(configurations);
	}

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "bindings", nillable = false, required = true)
	@XmlElement(name = "binding", nillable = false)
	public NamedSet<Bindings> getBindings() {
		return bindings;
	}

	public Reference<Workflow> getMainWorkflowReference() {
		return Reference.createReference(mainWorkflow);
	}

	public void setMainWorkflowReference(
			Reference<Workflow> mainWorkflowReference) {
		mainWorkflow = mainWorkflowReference.resolve();
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

	/**
	 * @param bindings
	 */
	public void setBindings(Set<Bindings> bindings) {
		this.bindings.clear();
		this.bindings.addAll(bindings);
	}

	/**
	 * @param mainWorkflow
	 */
	public void setMainWorkflow(Workflow mainWorkflow) {
		this.mainWorkflow = mainWorkflow;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper(name = "activities", nillable = false, required = true)
	@XmlElement(name = "activity", nillable = false)
	public NamedSet<Activity> getActivities() {
		return activities;
	}

	/**
	 * @param activities
	 */
	public void setActivities(Set<Activity> activities) {
		this.activities.clear();
		this.activities.addAll(activities);
	}

	@Override
	public String toString() {
		final int maxLen = 6;
		return "TavernaResearchObject [activities="
				+ (activities != null ? toString(activities, maxLen) : null)
				+ ", bindings="
				+ (bindings != null ? toString(bindings, maxLen) : null)
				+ ", mainWorkflow=" + mainWorkflow + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}
