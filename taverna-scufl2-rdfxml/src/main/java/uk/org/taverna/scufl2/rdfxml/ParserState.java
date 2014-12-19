package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ParserState {
	private Map<WorkflowBean, URI> beanToUri = new HashMap<>();
	private URI currentBase;
	private URI location;
	private Stack<WorkflowBean> stack = new Stack<>();
	private UCFPackage ucfPackage;
	private Map<URI, WorkflowBean> uriToBean = new HashMap<>();
	private Map<Processor, ObjectNode> dispatchConfigs = new HashMap<>();

	public Map<WorkflowBean, URI> getBeanToUri() {
		return beanToUri;
	}

	public <T extends WorkflowBean> T getCurrent(Class<T> beanType) {
		if (getStack().isEmpty())
			throw new IllegalStateException("Parser stack is empty");
		if (beanType.isInstance(getStack().peek()))
			return beanType.cast(getStack().peek());
		T candidate = null;
		for (WorkflowBean bean : getStack())
			if (beanType.isInstance(bean))
				// Don't return - we want the *last* candidate
				candidate = beanType.cast(bean);
		if (candidate == null)
			throw new IllegalStateException("Could not find a " + beanType + " on parser stack");
		return candidate;
	}

	public URI getCurrentBase() {
		return currentBase;
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

	public WorkflowBean peek() {
		return getStack().peek();
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

    public Map<Processor, ObjectNode> getDispatchConfigs() {
        return dispatchConfigs;
    }

    public void setDispatchConfigs(Map<Processor, ObjectNode> dispatchConfigs) {
        this.dispatchConfigs = dispatchConfigs;
    }
}