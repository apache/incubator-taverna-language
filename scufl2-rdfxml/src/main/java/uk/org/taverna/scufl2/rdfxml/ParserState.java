package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

public class ParserState {
	private Map<WorkflowBean, URI> beanToUri = new HashMap<WorkflowBean, URI>();
	private URI currentBase;
	private uk.org.taverna.scufl2.api.core.Processor currentProcessor;
	private Profile currentProfile;
	private uk.org.taverna.scufl2.api.dispatchstack.DispatchStack currentStack;
	private Workflow currentWorkflow;
	private URI location;
	private Stack<WorkflowBean> stack = new Stack<WorkflowBean>();
	private UCFPackage ucfPackage;
	private Map<URI, WorkflowBean> uriToBean = new HashMap<URI, WorkflowBean>();
	private WorkflowBundle workflowBundle;

	public Map<WorkflowBean, URI> getBeanToUri() {
		return beanToUri;
	}

	public URI getCurrentBase() {
		return currentBase;
	}

	public uk.org.taverna.scufl2.api.core.Processor getCurrentProcessor() {
		return currentProcessor;
	}

	public final Profile getCurrentProfile() {
		return currentProfile;
	}

	public uk.org.taverna.scufl2.api.dispatchstack.DispatchStack getCurrentStack() {
		return currentStack;
	}

	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public URI getLocation() {
		return location;
	}

	public Stack<WorkflowBean> getStack() {
		return stack;
	}

	public UCFPackage getUcfPackage() {
		return ucfPackage;
	}

	public Map<URI, WorkflowBean> getUriToBean() {
		return uriToBean;
	}

	public WorkflowBundle getWorkflowBundle() {
		return workflowBundle;
	}

	public WorkflowBean peek() {
		return getStack().peek();
	}

	public <T extends WorkflowBean> T peek(Class<T> beanType) {
		return beanType.cast(getStack().peek());
	}

	public WorkflowBean pop() {
		return getStack().pop();
	}

	public void push(WorkflowBean workflowBean) {
		getStack().push(workflowBean);
	}

	public void setBeanToUri(Map<WorkflowBean, URI> beanToUri) {
		this.beanToUri = beanToUri;
	}

	public void setCurrentBase(URI currentBase) {
		this.currentBase = currentBase;
	}

	public void setCurrentProcessor(
			uk.org.taverna.scufl2.api.core.Processor currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public void setCurrentProfile(Profile currentProfile) {
		this.currentProfile = currentProfile;
	}

	public void setCurrentStack(
			uk.org.taverna.scufl2.api.dispatchstack.DispatchStack currentStack) {
		this.currentStack = currentStack;
	}

	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	public void setLocation(URI location) {
		this.location = location;
	}

	public void setStack(Stack<WorkflowBean> currentIterationStrategyNode) {
		stack = currentIterationStrategyNode;
	}

	public void setUcfPackage(UCFPackage ucfPackage) {
		this.ucfPackage = ucfPackage;
	}

	public void setUriToBean(Map<URI, WorkflowBean> uriToBean) {
		this.uriToBean = uriToBean;
	}

	public void setWorkflowBundle(WorkflowBundle workflowBundle) {
		this.workflowBundle = workflowBundle;
	}

}