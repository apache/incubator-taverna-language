package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
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

		public Map<WorkflowBean, URI> getBeanToUri() {
			return beanToUri;
		}

		public URI getCurrentBase() {
			return currentBase;
		}

		public Stack<IterationStrategyParent> getCurrentIterationStrategyNode() {
			return currentIterationStrategyNode;
		}

		public uk.org.taverna.scufl2.api.core.Processor getCurrentProcessor() {
			return currentProcessor;
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

		public UCFPackage getUcfPackage() {
			return ucfPackage;
		}

		public Map<URI, WorkflowBean> getUriToBean() {
			return uriToBean;
		}

		public WorkflowBundle getWorkflowBundle() {
			return workflowBundle;
		}

		public void setBeanToUri(Map<WorkflowBean, URI> beanToUri) {
			this.beanToUri = beanToUri;
		}

		public void setCurrentBase(URI currentBase) {
			this.currentBase = currentBase;
		}

		public void setCurrentIterationStrategyNode(
				Stack<IterationStrategyParent> currentIterationStrategyNode) {
			this.currentIterationStrategyNode = currentIterationStrategyNode;
		}

		public void setCurrentProcessor(
				uk.org.taverna.scufl2.api.core.Processor currentProcessor) {
			this.currentProcessor = currentProcessor;
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

	protected JAXBContext jaxbContext;

	protected Unmarshaller unmarshaller;

	protected Scufl2Tools scufl2Tools = new Scufl2Tools();
	protected URITools uriTools = new URITools();
	protected final ThreadLocal<ParserState> parserState;
	public AbstractParser() {
		parserState = new ThreadLocal<ParserState>() {
			@Override
			protected ParserState initialValue() {
				return new ParserState();
			};
		};
		createMarshaller();
	}

	public AbstractParser(ThreadLocal<ParserState> parserState) {
		this.parserState = parserState;
		createMarshaller();
	}

	protected void clearParserState() {
		parserState.remove();
	}

	private void createMarshaller() {
		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException(
					"Can't create JAXBContext/unmarshaller", e);
		}
	}

	protected ParserState getParserState() {
		return parserState.get();
	}

	protected void mapBean(URI uri, WorkflowBean bean) {
		getParserState().getUriToBean().put(uri, bean);
		getParserState().getBeanToUri().put(bean, uri);
	}

	protected WorkflowBean resolveBeanUri(URI uri) {
		WorkflowBean workflowBean = getParserState().getUriToBean().get(uri);
		if (workflowBean != null) {
			return workflowBean;
		}
		return uriTools.resolveUri(uri, getParserState().getWorkflowBundle());
	}

}