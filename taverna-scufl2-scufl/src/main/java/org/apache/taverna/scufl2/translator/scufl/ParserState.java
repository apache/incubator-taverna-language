/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl;

import java.util.HashMap;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;


/**
 * @author alanrw
 */
public class ParserState {
	private ScuflParser currentParser;
	private WorkflowBundle currentWorkflowBundle;
	private Profile currentProfile;
	private Workflow currentWorkflow;
	private Processor currentProcessor;
	private ScuflExtensionParser currentExtensionParser;
	private Activity currentActivity;

	private HashMap<Object, WorkflowBean> forwardMapping = new HashMap<>();

	public void setCurrentWorkflowBundle(WorkflowBundle wfBundle) {
		this.currentWorkflowBundle = wfBundle;
	}

	public WorkflowBundle getCurrentWorkflowBundle() {
		return currentWorkflowBundle;
	}

	public void setCurrentProfile(Profile profile) {
		this.currentProfile = profile;
	}

	/**
	 * @return the currentProfile
	 */
	public Profile getCurrentProfile() {
		return currentProfile;
	}

	/**
	 * @return the currentParser
	 */
	public ScuflParser getCurrentParser() {
		return currentParser;
	}

	/**
	 * @param currentParser
	 *            the currentParser to set
	 */
	public void setCurrentParser(ScuflParser currentParser) {
		this.currentParser = currentParser;
	}

	/**
	 * @param currentWorkflow
	 *            the currentWorkflow to set
	 */
	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	/**
	 * @return the currentWorkflow
	 */
	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void addMapping(Object scuflObject, WorkflowBean scufl2Object) {
		forwardMapping.put(scuflObject, scufl2Object);
	}

	/**
	 * @return the currentProcessor
	 */
	public Processor getCurrentProcessor() {
		return currentProcessor;
	}

	/**
	 * @param currentProcessor
	 *            the currentProcessor to set
	 */
	public void setCurrentProcessor(Processor currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	/**
	 * @return the currentExtensionParser
	 */
	public ScuflExtensionParser getCurrentExtensionParser() {
		return currentExtensionParser;
	}

	/**
	 * @param currentExtensionParser
	 *            the currentExtensionParser to set
	 */
	public void setCurrentExtensionParser(
			ScuflExtensionParser currentExtensionParser) {
		this.currentExtensionParser = currentExtensionParser;
	}

	/**
	 * @return the currentActivity
	 */
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * @param currentActivity
	 *            the currentActivity to set
	 */
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}
}
