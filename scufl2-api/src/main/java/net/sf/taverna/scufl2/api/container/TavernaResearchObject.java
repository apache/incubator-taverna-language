package net.sf.taverna.scufl2.api.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.activity.Activity;
import net.sf.taverna.scufl2.api.bindings.Bindings;
import net.sf.taverna.scufl2.api.common.Configurable;
import net.sf.taverna.scufl2.api.common.WorkflowBean;
import net.sf.taverna.scufl2.api.configurations.Configuration;
import net.sf.taverna.scufl2.api.core.Workflow;
import net.sf.taverna.scufl2.api.reference.Reference;

/**
 * @author alanrw
 *
 */
@XmlType (propOrder = {"workflows", "mainWorkflowReference", "activities", "configurations", "bindings"})
public class TavernaResearchObject implements ResearchObject, WorkflowBean {

	private Set<Bindings> bindings = new HashSet<Bindings>();
	private Workflow mainWorkflow;
	private Set<Workflow> workflows = new HashSet<Workflow>();
	
	/**
	 * @return
	 */
	public Set<Workflow> getWorkflows() {
		return workflows;
	}

	/**
	 * @param workflows
	 */
	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows = workflows;
	}

	private Set<Activity> activities = new HashSet<Activity>();
	private Set<Configuration> configurations = new HashSet<Configuration>();

	/**
	 * @return
	 */
	@XmlElementWrapper( name="configurations",nillable=false,required=true)
	@XmlElement( name="configuration",nillable=false)
	public Set<Configuration> getConfigurations() {
		return configurations;
	}

	/**
	 * @param configurations
	 */
	public void setConfigurations(Set<Configuration> configurations) {
		this.configurations = configurations;
	}

	/**
	 * @return
	 */
	@XmlElementWrapper( name="bindings",nillable=false,required=true)
	@XmlElement( name="binding",nillable=false)
	public Set<Bindings> getBindings() {
		return bindings;
	}

	public Reference<Workflow> getMainWorkflowReference() {
		return Reference.createReference(mainWorkflow);
	}

	public void setMainWorkflowReference(Reference<Workflow> mainWorkflowReference) {
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
		this.bindings = bindings;
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
	@XmlElementWrapper( name="activities",nillable=false,required=true)
	@XmlElement( name="activity",nillable=false)
	public Set<Activity> getActivities() {
		return activities;
	}

	/**
	 * @param activities
	 */
	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
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
