package uk.org.taverna.scufl2.rdfxml;

import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_RDF_XML;
import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyParent;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Blocking;
import uk.org.taverna.scufl2.rdfxml.jaxb.Control;
import uk.org.taverna.scufl2.rdfxml.jaxb.CrossProduct;
import uk.org.taverna.scufl2.rdfxml.jaxb.DataLink;
import uk.org.taverna.scufl2.rdfxml.jaxb.DataLinkEntry;
import uk.org.taverna.scufl2.rdfxml.jaxb.DispatchStack;
import uk.org.taverna.scufl2.rdfxml.jaxb.DispatchStackLayer;
import uk.org.taverna.scufl2.rdfxml.jaxb.DotProduct;
import uk.org.taverna.scufl2.rdfxml.jaxb.IterationStrategyStack;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.PortNode;
import uk.org.taverna.scufl2.rdfxml.jaxb.Processor.InputProcessorPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Processor.OutputProcessorPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProductOf;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.InputWorkflowPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.OutputWorkflowPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.Processor;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundleDocument;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowDocument;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class WorkflowParser {

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

	public WorkflowParser() {

		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException(
					"Can't create JAXBContext/unmarshaller", e);
		}
	}

	@SuppressWarnings("unchecked")
	public WorkflowBundle readWorkflowBundle(UCFPackage ucfPackage,
			URI suggestedLocation) throws IOException, ReaderException {
		try {
			this.getParserState().setUcfPackage(ucfPackage);
			getParserState().setLocation(suggestedLocation);
			if (getParserState().getLocation() == null) {
				getParserState().setLocation(URI.create(""));
			} else {
				if (!getParserState().getLocation().getRawPath().endsWith("/")) {
					if (getParserState().getLocation().getQuery() != null
							|| getParserState().getLocation().getFragment() != null) {
						// Ouch.. Perhaps some silly website with ?bundleId=15 ?
						// We'll better conserve that somehow.
						// Let's do the jar: trick and hope it works. Have to
						// escape
						// evil chars.
						getParserState().setLocation(
								URI.create("jar:"
										+ getParserState().getLocation()
												.toASCIIString()
												.replace("?", "%63")
												.replace("#", "#35") + "!/"));
					} else {
						// Simple, pretend we're one level down inside the ZIP
						// file
						// as a directory
						getParserState().setLocation(
								getParserState().getLocation().resolve(
										getParserState().getLocation()
												.getRawPath() + "/"));
					}
				}
			}
			String workflowBundlePath = findWorkflowBundlePath();

			InputStream bundleStream = getParserState().getUcfPackage()
					.getResourceAsInputStream(workflowBundlePath);

			JAXBElement<WorkflowBundleDocument> elem;
			try {
				elem = (JAXBElement<WorkflowBundleDocument>) unmarshaller
						.unmarshal(bundleStream);
			} catch (JAXBException e) {
				throw new ReaderException(
						"Can't parse workflow bundle document "
								+ workflowBundlePath, e);
			}
			WorkflowBundleDocument workflowBundleDocument = elem.getValue();

			URI base = getParserState().getLocation().resolve(
					workflowBundlePath);
			if (workflowBundleDocument.getBase() != null) {
				base = getParserState().getLocation().resolve(
						workflowBundleDocument.getBase());
			}

			if (workflowBundleDocument.getAny().size() != 1) {
				throw new ReaderException(
						"Invalid WorkflowBundleDocument, expected only one <WorkflowBundle>");
			}

			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle wb = (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle) workflowBundleDocument
					.getAny().get(0);
			parseWorkflowBundle(wb, base);

			scufl2Tools.setParents(getParserState().getWorkflowBundle());
			return getParserState().getWorkflowBundle();
		} finally {
			clearParserState();
		}
	}

	protected void clearParserState() {
		parserState.remove();
	}

	protected void parseWorkflowBundle(
			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle wb, URI base)
			throws ReaderException, IOException {
		getParserState().setWorkflowBundle(new WorkflowBundle());
		getParserState().getWorkflowBundle().setResources(
				getParserState().getUcfPackage());
		if (wb.getName() != null) {
			getParserState().getWorkflowBundle().setName(wb.getName());
		}
		if (wb.getSameBaseAs() != null
				&& wb.getSameBaseAs().getResource() != null) {
			getParserState().getWorkflowBundle().setSameBaseAs(
					base.resolve(wb.getSameBaseAs().getResource()));
		}
		mapBean(base.resolve(wb.getAbout()), getParserState()
				.getWorkflowBundle());
		for (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Workflow wfEntry : wb
				.getWorkflow()) {
			URI wfUri = base.resolve(wfEntry.getWorkflow().getAbout());
			String resource = wfEntry.getWorkflow().getSeeAlso().getResource();
			URI source = uriTools.relativePath(getParserState().getLocation(),
					base.resolve(resource));
			readWorkflow(wfUri, source);
		}
		for (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Profile pfEntry : wb
				.getProfile()) {
			URI wfUri = base.resolve(pfEntry.getProfile().getAbout());
			String resource = pfEntry.getProfile().getSeeAlso().getResource();
			URI source = uriTools.relativePath(getParserState().getLocation(),
					base.resolve(resource));
			readProfile(wfUri, source);
		}

		if (wb.getMainWorkflow() != null
				&& wb.getMainWorkflow().getResource() != null) {
			URI mainWfUri = base.resolve(wb.getMainWorkflow().getResource());
			Workflow mainWorkflow = (Workflow) resolveBeanUri(mainWfUri);
			if (mainWorkflow == null) {
				throw new ReaderException("Unknown main workflow " + mainWfUri
						+ ", got" + getParserState().getUriToBean().keySet());
			}
			getParserState().getWorkflowBundle().setMainWorkflow(mainWorkflow);
		}
		if (wb.getMainProfile() != null
				&& wb.getMainProfile().getResource() != null) {
			URI profileUri = base.resolve(wb.getMainProfile().getResource());
			uk.org.taverna.scufl2.api.profiles.Profile mainWorkflow = (uk.org.taverna.scufl2.api.profiles.Profile) resolveBeanUri(profileUri);
			getParserState().getWorkflowBundle().setMainProfile(mainWorkflow);
		}

	}

	protected WorkflowBean resolveBeanUri(URI uri) {
		WorkflowBean workflowBean = getParserState().getUriToBean().get(uri);
		if (workflowBean != null) {
			return workflowBean;
		}
		return uriTools.resolveUri(uri, getParserState().getWorkflowBundle());
	}

	protected void readProfile(URI profileUri, URI source)
			throws ReaderException {
		if (source.isAbsolute()) {
			throw new ReaderException("Can't read external profile source "
					+ source);
		}

		uk.org.taverna.scufl2.api.profiles.Profile p = new uk.org.taverna.scufl2.api.profiles.Profile();
		p.setParent(getParserState().getWorkflowBundle());
		mapBean(profileUri, p);
	}

	protected void readWorkflow(URI wfUri, URI source) throws ReaderException,
			IOException {
		if (source.isAbsolute()) {
			throw new ReaderException("Can't read external workflow source "
					+ source);
		}

		InputStream bundleStream = getParserState().getUcfPackage()
				.getResourceAsInputStream(source.getPath());

		JAXBElement<WorkflowDocument> elem;
		try {
			elem = (JAXBElement<WorkflowDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException(
					"Can't parse workflow document " + source, e);
		}

		URI base = getParserState().getLocation().resolve(source);
		if (elem.getValue().getBase() != null) {
			base = base.resolve(elem.getValue().getBase());
		}

		if (elem.getValue().getAny().size() != 1) {
			throw new ReaderException("Expects only a <Workflow> element in "
					+ source);
		}
		uk.org.taverna.scufl2.rdfxml.jaxb.Workflow workflow = (uk.org.taverna.scufl2.rdfxml.jaxb.Workflow) elem
				.getValue().getAny().get(0);

		getParserState().setCurrentBase(base);
		parseWorkflow(workflow, wfUri);

	}

	protected void parseWorkflow(
			uk.org.taverna.scufl2.rdfxml.jaxb.Workflow workflow, URI wfUri)
			throws ReaderException {
		Workflow wf = new Workflow();
		wf.setParent(getParserState().getWorkflowBundle());

		if (workflow.getAbout() != null) {
			mapBean(getParserState().getCurrentBase().resolve(
					workflow.getAbout()), wf);
			// TODO: Compare resolved URI with desired wfUri
		} else {
			mapBean(wfUri, wf);
		}

		getParserState().setCurrentWorkflow(wf);

		if (workflow.getName() != null) {
			wf.setName(workflow.getName());
		}
		if (workflow.getWorkflowIdentifier() != null
				&& workflow.getWorkflowIdentifier().getResource() != null) {
			wf.setWorkflowIdentifier(getParserState().getCurrentBase().resolve(
					workflow.getWorkflowIdentifier().getResource()));
		}

		for (InputWorkflowPort inputWorkflowPort : workflow
				.getInputWorkflowPort()) {
			parseInputWorkflowPort(inputWorkflowPort.getInputWorkflowPort());
		}
		for (OutputWorkflowPort outputWorkflowPort : workflow
				.getOutputWorkflowPort()) {
			parseOutputWorkflowPort(outputWorkflowPort.getOutputWorkflowPort());
		}
		for (Processor processor : workflow.getProcessor()) {
			parseProcessor(processor.getProcessor());
		}
		for (DataLinkEntry dataLinkEntry : workflow.getDatalink()) {
			parseDataLink(dataLinkEntry.getDataLink());
		}
		for (Control c : workflow.getControl()) {
			parseControlLink(c.getBlocking());
		}

	}

	protected void parseProcessor(
			uk.org.taverna.scufl2.rdfxml.jaxb.Processor processor)
			throws ReaderException {
		uk.org.taverna.scufl2.api.core.Processor p = new uk.org.taverna.scufl2.api.core.Processor();
		getParserState().setCurrentProcessor(p);
		p.setParent(getParserState().getCurrentWorkflow());
		mapBean(getParserState().getCurrentBase().resolve(processor.getAbout()),
				p);
		if (processor.getName() != null) {
			p.setName(processor.getName());
		}
		for (InputProcessorPort inputProcessorPort : processor
				.getInputProcessorPort()) {
			processorInputProcessorPort(inputProcessorPort
					.getInputProcessorPort());
		}
		for (OutputProcessorPort outputProcessorPort : processor
				.getOutputProcessorPort()) {
			processorOutputProcessorPort(outputProcessorPort
					.getOutputProcessorPort());
		}
		if (processor.getDispatchStack() != null) {
			parseDispatchStack(processor.getDispatchStack().getDispatchStack());
		}
		if (processor.getIterationStrategyStack() != null) {
			parseIterationStrategyStack(processor.getIterationStrategyStack()
					.getIterationStrategyStack());
		}
	}

	protected void parseDispatchStack(DispatchStack original) {
		uk.org.taverna.scufl2.api.dispatchstack.DispatchStack stack = new uk.org.taverna.scufl2.api.dispatchstack.DispatchStack();
		if (original.getType() != null) {
			stack.setType(getParserState().getCurrentBase().resolve(
					original.getType().getResource()));
		}
		stack.setParent(getParserState().getCurrentProcessor());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				stack);
		getParserState().setCurrentStack(stack);
		if (original.getDispatchStackLayers() != null) {
			for (DispatchStackLayer dispatchStackLayer : original
					.getDispatchStackLayers().getDispatchStackLayer()) {
				parseDispatchStackLayer(dispatchStackLayer);
			}
		}
	}

	protected void parseDispatchStackLayer(DispatchStackLayer original) {
		uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer layer = new uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer();
		layer.setConfigurableType(getParserState().getCurrentBase().resolve(
				original.getType().getResource()));
		layer.setParent(getParserState().getCurrentStack());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				layer);
	}

	protected void parseIterationStrategyStack(IterationStrategyStack original)
			throws ReaderException {
		uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack iterationStrategyStack = new uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack();
		iterationStrategyStack
				.setParent(getParserState().getCurrentProcessor());
		getParserState().getCurrentIterationStrategyNode().clear();
		getParserState().getCurrentIterationStrategyNode().push(
				iterationStrategyStack);
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				iterationStrategyStack);
		if (original.getIterationStrategies() != null) {
			parseCrossDotOrPortNodeList(original.getIterationStrategies()
					.getDotProductOrCrossProduct());
		}
	}

	protected void parseCrossDotOrPortNodeList(List<Object> nodeList)
			throws ReaderException {
		for (Object node : nodeList) {
			if (node instanceof DotProduct) {
				parseDotProduct((DotProduct) node);
			} else if (node instanceof CrossProduct) {
				parseCrossProduct((CrossProduct) node);
			} else if (node instanceof PortNode) {
				parsePortNode((PortNode) node);
			} else {
				throw new ReaderException("Unexpected node " + node);
			}
		}
	}

	protected void parsePortNode(PortNode original) {
		uk.org.taverna.scufl2.api.iterationstrategy.PortNode node = new uk.org.taverna.scufl2.api.iterationstrategy.PortNode();
		node.setParent(getParserState().getCurrentIterationStrategyNode()
				.peek());
		if (original.getDesiredDepth() != null) {
			node.setDesiredDepth(original.getDesiredDepth().getValue());
		}
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				node);
		URI inputPortUri = getParserState().getCurrentBase().resolve(
				original.getIterateOverInputPort().getResource());
		uk.org.taverna.scufl2.api.port.InputProcessorPort inputPort = (uk.org.taverna.scufl2.api.port.InputProcessorPort) resolveBeanUri(inputPortUri);
		node.setInputProcessorPort(inputPort);
	}

	protected void parseCrossProduct(CrossProduct original)
			throws ReaderException {
		uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct cross = new uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct();
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				cross);
		cross.setParent(getParserState().getCurrentIterationStrategyNode()
				.peek());
		getParserState().getCurrentIterationStrategyNode().push(cross);
		parseProductOf(original.getProductOf());
		getParserState().getCurrentIterationStrategyNode().pop();
	}

	protected void parseProductOf(ProductOf productOf) throws ReaderException {
		if (productOf == null) {
			return;
		}
		parseCrossDotOrPortNodeList(productOf
				.getCrossProductOrDotProductOrPortNode());

	}

	protected void parseDotProduct(DotProduct original) throws ReaderException {
		uk.org.taverna.scufl2.api.iterationstrategy.DotProduct dot = new uk.org.taverna.scufl2.api.iterationstrategy.DotProduct();
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				dot);
		dot.setParent(getParserState().getCurrentIterationStrategyNode().peek());
		getParserState().getCurrentIterationStrategyNode().push(dot);
		parseProductOf(original.getProductOf());
		getParserState().getCurrentIterationStrategyNode().pop();
	}

	protected void processorOutputProcessorPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputProcessorPort original) {
		uk.org.taverna.scufl2.api.port.OutputProcessorPort port = new uk.org.taverna.scufl2.api.port.OutputProcessorPort();
		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
		if (original.getGranularPortDepth() != null) {
			port.setGranularDepth(original.getGranularPortDepth().getValue());
		}
		port.setParent(getParserState().getCurrentProcessor());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	protected void processorInputProcessorPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputProcessorPort original) {
		uk.org.taverna.scufl2.api.port.InputProcessorPort port = new uk.org.taverna.scufl2.api.port.InputProcessorPort();
		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
		port.setParent(getParserState().getCurrentProcessor());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	protected void parseDataLink(DataLink original) {
		URI fromUri = getParserState().getCurrentBase().resolve(
				original.getReceiveFrom().getResource());
		URI toUri = getParserState().getCurrentBase().resolve(
				original.getSendTo().getResource());
		WorkflowBean from = resolveBeanUri(fromUri);
		WorkflowBean to = resolveBeanUri(toUri);

		uk.org.taverna.scufl2.api.core.DataLink link = new uk.org.taverna.scufl2.api.core.DataLink();
		link.setReceivesFrom((SenderPort) from);
		link.setSendsTo((ReceiverPort) to);
		if (original.getMergePosition() != null) {
			link.setMergePosition(original.getMergePosition().getValue());
		}
		link.setParent(getParserState().getCurrentWorkflow());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				link);
	}

	protected void parseControlLink(Blocking original) {
		URI blockUri = getParserState().getCurrentBase().resolve(
				original.getBlock().getResource());
		URI untilFinishedUri = getParserState().getCurrentBase().resolve(
				original.getUntilFinished().getResource());
		WorkflowBean block = resolveBeanUri(blockUri);
		WorkflowBean untilFinished = resolveBeanUri(untilFinishedUri);

		BlockingControlLink blocking = new BlockingControlLink();
		blocking.setBlock((uk.org.taverna.scufl2.api.core.Processor) block);
		blocking.setUntilFinished((uk.org.taverna.scufl2.api.core.Processor) untilFinished);

		blocking.setParent(getParserState().getCurrentWorkflow());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				blocking);

	}

	protected void parseOutputWorkflowPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputWorkflowPort original) {
		uk.org.taverna.scufl2.api.port.OutputWorkflowPort port = new uk.org.taverna.scufl2.api.port.OutputWorkflowPort();
		port.setName(original.getName());
		port.setParent(getParserState().getCurrentWorkflow());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	protected void parseInputWorkflowPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputWorkflowPort original) {
		uk.org.taverna.scufl2.api.port.InputWorkflowPort port = new uk.org.taverna.scufl2.api.port.InputWorkflowPort();
		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
		port.setParent(getParserState().getCurrentWorkflow());
		mapBean(getParserState().getCurrentBase().resolve(original.getAbout()),
				port);
	}

	private ThreadLocal<ParserState> parserState = new ThreadLocal<ParserState>() {
		protected ParserState initialValue() {
			return new ParserState();
		};
	};

	protected void mapBean(URI uri, WorkflowBean bean) {
		getParserState().getUriToBean().put(uri, bean);
		getParserState().getBeanToUri().put(bean, uri);
	}

	protected String findWorkflowBundlePath() {
		if (APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE
				.equals(getParserState().getUcfPackage().getPackageMediaType())) {
			for (ResourceEntry potentialRoot : getParserState().getUcfPackage()
					.getRootFiles()) {
				if (APPLICATION_RDF_XML.equals(potentialRoot.getMediaType())) {
					return potentialRoot.getPath();
				}
			}
		}
		return RDFXMLWriter.WORKFLOW_BUNDLE_RDF;
	}

	protected ParserState getParserState() {
		return parserState.get();
	}

}
