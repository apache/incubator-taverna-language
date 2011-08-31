/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

class DummyProfile extends Profile {

	private NamedSet<ProcessorBinding> processorBindings = null;

	private NamedSet<Configuration> configurations = null;

	private NamedSet<Activity> activities = null;

	/**
	 * @return the processorBindings
	 */
	public NamedSet<ProcessorBinding> getProcessorBindings() {
		return processorBindings;
	}

	/**
	 * @param processorBindings the processorBindings to set
	 */
	public void setProcessorBindings(NamedSet<ProcessorBinding> processorBindings) {
		this.processorBindings = processorBindings;
	}

	/**
	 * @return the configurations
	 */
	public NamedSet<Configuration> getConfigurations() {
		return configurations;
	}

	/**
	 * @param configurations the configurations to set
	 */
	public void setConfigurations(NamedSet<Configuration> configurations) {
		this.configurations = configurations;
	}

	/**
	 * @return the activities
	 */
	public NamedSet<Activity> getActivities() {
		return activities;
	}

	/**
	 * @param activities the activities to set
	 */
	public void setActivities(NamedSet<Activity> activities) {
		this.activities = activities;
	}
	
	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			if (getActivities() != null) {
				children.add(getActivities());
			}
			if (getProcessorBindings() != null) {
				children.add(getProcessorBindings());
			}
			if (getConfigurations() != null) {
				children.add(getConfigurations());
			}
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


}