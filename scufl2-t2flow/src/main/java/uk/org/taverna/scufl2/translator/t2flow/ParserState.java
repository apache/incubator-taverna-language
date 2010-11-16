package uk.org.taverna.scufl2.translator.t2flow;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class ParserState {
	private uk.org.taverna.scufl2.api.activity.Activity currentActivity;
	private Profile currentProfile;
	private Processor currentProcessor;

	public uk.org.taverna.scufl2.api.activity.Activity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(
			uk.org.taverna.scufl2.api.activity.Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	public Profile getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(Profile currentProfile) {
		this.currentProfile = currentProfile;
	}

	public Processor getCurrentProcessor() {
		return currentProcessor;
	}

	public void setCurrentProcessor(Processor currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public ProcessorBinding getCurrentProcessorBinding() {
		return currentProcessorBinding;
	}

	public void setCurrentProcessorBinding(
			ProcessorBinding currentProcessorBinding) {
		this.currentProcessorBinding = currentProcessorBinding;
	}

	public WorkflowBundle getCurrentResearchObject() {
		return currentResearchObject;
	}

	public void setCurrentResearchObject(WorkflowBundle currentResearchObject) {
		this.currentResearchObject = currentResearchObject;
	}

	public T2Parser getCurrentT2Parser() {
		return currentT2Parser;
	}

	public void setCurrentT2Parser(T2Parser currentT2Parser) {
		this.currentT2Parser = currentT2Parser;
	}

	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	private ProcessorBinding currentProcessorBinding;
	private WorkflowBundle currentResearchObject;
	private T2Parser currentT2Parser;
	private Workflow currentWorkflow;

}
