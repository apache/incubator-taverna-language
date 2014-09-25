/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * @author alanrw
 *
 */
public class DummyWorkflowBundle extends WorkflowBundle {
	
	private NamedSet<Profile> profiles = null;
	private NamedSet<Workflow> workflows = null;
	private Workflow mainWorkflow;
	private Profile mainProfile;
	
	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			if (getWorkflows() != null) {
				children.add(getWorkflows());
			}
			if (getProfiles() != null) {
				children.add(getProfiles());
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

	/**
	 * @return the profiles
	 */
	@Override
	public NamedSet<Profile> getProfiles() {
		return profiles;
	}

	/**
	 * @param profiles the profiles to set
	 */
	public void setProfiles(NamedSet<Profile> profiles) {
		this.profiles = profiles;
	}

	/**
	 * @return the workflows
	 */
	@Override
	public NamedSet<Workflow> getWorkflows() {
		return workflows;
	}

	/**
	 * @param workflows the workflows to set
	 */
	public void setWorkflows(NamedSet<Workflow> workflows) {
		this.workflows = workflows;
	}

	/**
	 * @return the mainWorkflow
	 */
	@Override
	public Workflow getMainWorkflow() {
		return mainWorkflow;
	}

	/**
	 * @param mainWorkflow the mainWorkflow to set
	 */
	@Override
	public void setMainWorkflow(Workflow mainWorkflow) {
		this.mainWorkflow = mainWorkflow;
	}

	/**
	 * @return the mainProfile
	 */
	@Override
	public Profile getMainProfile() {
		return mainProfile;
	}

	/**
	 * @param mainProfile the mainProfile to set
	 */
	@Override
	public void setMainProfile(Profile mainProfile) {
		this.mainProfile = mainProfile;
	}


}
