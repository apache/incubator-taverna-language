package net.sf.taverna.scufl2.api.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.taverna.scufl2.api.activity.Activity;
import net.sf.taverna.scufl2.api.bindings.Bindings;
import net.sf.taverna.scufl2.api.common.WorkflowBean;
import net.sf.taverna.scufl2.api.core.Workflow;

public class TavernaResearchObject implements ResearchObject, WorkflowBean {

	private Set<Bindings> bindings = new HashSet<Bindings>();
	private Workflow mainWorkflow;
	private Set<Activity> activities = new HashSet<Activity>();

	public Set<Bindings> getBindings() {
		return bindings;
	}

	public Workflow getMainWorkflow() {
		if (mainWorkflow == null) {
			mainWorkflow = new Workflow();
		}
		return mainWorkflow;
	}

	public void setBindings(Set<Bindings> bindings) {
		this.bindings = bindings;
	}

	public void setMainWorkflow(Workflow mainWorkflow) {
		this.mainWorkflow = mainWorkflow;
	}

	public Set<Activity> getActivities() {
		return activities;
	}

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
