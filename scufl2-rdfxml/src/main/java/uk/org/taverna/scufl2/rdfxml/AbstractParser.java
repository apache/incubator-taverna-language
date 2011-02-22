package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

public class AbstractParser {

	public static class ParserState {
		private UCFPackage ucfPackage;
		private WorkflowBundle workflowBundle;
		private Workflow currentWorkflow;
		private uk.org.taverna.scufl2.api.core.Processor currentProcessor;
		private URI currentBase;
		private uk.org.taverna.scufl2.api.dispatchstack.DispatchStack currentStack;
		private Stack<IterationStrategyParent> currentIterationStrategyNode = new Stack<IterationStrategyParent>();
		private Map<URI, WorkflowBean> uriToBean = new HashMap<URI, WorkflowBean>();
		private Map<WorkflowBean, URI> beanToUri = new HashMap<WorkflowBean, URI>();
		private URI location;

		public UCFPackage getUcfPackage() {
			return ucfPackage;
		}

		public void setUcfPackage(UCFPackage ucfPackage) {
			this.ucfPackage = ucfPackage;
		}

		public WorkflowBundle getWorkflowBundle() {
			return workflowBundle;
		}

		public void setWorkflowBundle(WorkflowBundle workflowBundle) {
			this.workflowBundle = workflowBundle;
		}

		public Workflow getCurrentWorkflow() {
			return currentWorkflow;
		}

		public void setCurrentWorkflow(Workflow currentWorkflow) {
			this.currentWorkflow = currentWorkflow;
		}

		public uk.org.taverna.scufl2.api.core.Processor getCurrentProcessor() {
			return currentProcessor;
		}

		public void setCurrentProcessor(
				uk.org.taverna.scufl2.api.core.Processor currentProcessor) {
			this.currentProcessor = currentProcessor;
		}

		public URI getCurrentBase() {
			return currentBase;
		}

		public void setCurrentBase(URI currentBase) {
			this.currentBase = currentBase;
		}

		public uk.org.taverna.scufl2.api.dispatchstack.DispatchStack getCurrentStack() {
			return currentStack;
		}

		public void setCurrentStack(
				uk.org.taverna.scufl2.api.dispatchstack.DispatchStack currentStack) {
			this.currentStack = currentStack;
		}

		public Stack<IterationStrategyParent> getCurrentIterationStrategyNode() {
			return currentIterationStrategyNode;
		}

		public void setCurrentIterationStrategyNode(
				Stack<IterationStrategyParent> currentIterationStrategyNode) {
			this.currentIterationStrategyNode = currentIterationStrategyNode;
		}

		public Map<URI, WorkflowBean> getUriToBean() {
			return uriToBean;
		}

		public void setUriToBean(Map<URI, WorkflowBean> uriToBean) {
			this.uriToBean = uriToBean;
		}

		public Map<WorkflowBean, URI> getBeanToUri() {
			return beanToUri;
		}

		public void setBeanToUri(Map<WorkflowBean, URI> beanToUri) {
			this.beanToUri = beanToUri;
		}

		public URI getLocation() {
			return location;
		}

		public void setLocation(URI location) {
			this.location = location;
		}
	}

	protected JAXBContext jaxbContext;
	protected Unmarshaller unmarshaller;
	protected Scufl2Tools scufl2Tools = new Scufl2Tools();
	protected URITools uriTools = new URITools();
	private ThreadLocal<ParserState> parserState = new ThreadLocal<ParserState>() {
		protected ParserState initialValue() {
			return new ParserState();
		};
	};

	protected void clearParserState() {
		parserState.remove();
	}

	public AbstractParser() {
		super();
	}

	protected ParserState getParserState() {
		return parserState.get();
	}

	protected WorkflowBean resolveBeanUri(URI uri) {
		WorkflowBean workflowBean = getParserState().getUriToBean().get(uri);
		if (workflowBean != null) {
			return workflowBean;
		}
		return uriTools.resolveUri(uri, getParserState().getWorkflowBundle());
	}

	protected void mapBean(URI uri, WorkflowBean bean) {
		getParserState().getUriToBean().put(uri, bean);
		getParserState().getBeanToUri().put(bean, uri);
	}

}