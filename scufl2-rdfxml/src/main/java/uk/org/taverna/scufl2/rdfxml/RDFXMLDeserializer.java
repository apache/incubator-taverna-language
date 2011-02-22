package uk.org.taverna.scufl2.rdfxml;

import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_RDF_XML;
import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.rdfxml.jaxb.Blocking;
import uk.org.taverna.scufl2.rdfxml.jaxb.Control;
import uk.org.taverna.scufl2.rdfxml.jaxb.DataLink;
import uk.org.taverna.scufl2.rdfxml.jaxb.DataLinkEntry;
import uk.org.taverna.scufl2.rdfxml.jaxb.DispatchStack;
import uk.org.taverna.scufl2.rdfxml.jaxb.IterationStrategyStack;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.Processor.InputProcessorPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Processor.OutputProcessorPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Profile;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.InputWorkflowPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.OutputWorkflowPort;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow.Processor;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundleDocument;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowDocument;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class RDFXMLDeserializer {

	private final UCFPackage ucfPackage;
	private WorkflowBundle workflowBundle;
	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private URITools uriTools = new URITools();
	private Workflow currentWorkflow;
	private uk.org.taverna.scufl2.api.core.Processor currentProcessor;
	private URI currentBase;

	public RDFXMLDeserializer(UCFPackage ucfPackage) {
		this.ucfPackage = ucfPackage;

		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalStateException(
					"Can't create JAXBContext/unmarshaller", e);
		}
	}

	@SuppressWarnings("unchecked")
	public WorkflowBundle readWorkflowBundle(URI suggestedLocation) throws IOException,
			ReaderException {

		location = suggestedLocation;
		if (location == null) {
			location = URI.create("");
		} else {
			if (!location.getRawPath().endsWith("/")) {
				if (location.getQuery() != null
						|| location.getFragment() != null) {
					// Ouch.. Perhaps some silly website with ?bundleId=15 ?
					// We'll better conserve that somehow.
					// Let's do the jar: trick and hope it works. Have to escape
					// evil chars.
					location = URI.create("jar:"
							+ location.toASCIIString().replace("?", "%63")
									.replace("#", "#35") + "!/");
				} else {
					// Simple, pretend we're one level down inside the ZIP file
					// as a directory
					location = location.resolve(location.getRawPath() + "/");
				}
			}
		}
		String workflowBundlePath = findWorkflowBundlePath();

		InputStream bundleStream = ucfPackage
				.getResourceAsInputStream(workflowBundlePath);

		JAXBElement<WorkflowBundleDocument> elem;
		try {
			elem = (JAXBElement<WorkflowBundleDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse workflow bundle document "
					+ workflowBundlePath, e);
		}
		WorkflowBundleDocument workflowBundleDocument = elem.getValue();

		URI base = location.resolve(workflowBundlePath);
		if (workflowBundleDocument.getBase() != null) {
			base = location.resolve(workflowBundleDocument.getBase());
		}

		if (workflowBundleDocument.getAny().size() != 1) {
			throw new ReaderException(
					"Invalid WorkflowBundleDocument, expected only one <WorkflowBundle>");
		}
		
		uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle wb = (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle) workflowBundleDocument
				.getAny().get(0);
		parseWorkflowBundle(wb, base);

		scufl2Tools.setParents(workflowBundle);
		return workflowBundle;
	}

	protected void parseWorkflowBundle(
			uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle wb, URI base) throws ReaderException, IOException {
		workflowBundle = new WorkflowBundle();
		workflowBundle.setResources(ucfPackage);
		if (wb.getName() != null) {
			workflowBundle.setName(wb.getName());
		}
		if (wb.getSameBaseAs() != null
				&& wb.getSameBaseAs().getResource() != null) {
			workflowBundle.setSameBaseAs(base.resolve(wb.getSameBaseAs()
					.getResource()));
		}
		mapBean(base.resolve(wb.getAbout()), workflowBundle);
		for (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Workflow wfEntry : wb
				.getWorkflow()) {
			URI wfUri = base.resolve(wfEntry.getWorkflow().getAbout());
			String resource = wfEntry.getWorkflow().getSeeAlso().getResource();
			URI source = uriTools
					.relativePath(location, base.resolve(resource));
			readWorkflow(wfUri, source);
		}
		for (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle.Profile pfEntry : wb
				.getProfile()) {
			URI wfUri = base.resolve(pfEntry.getProfile().getAbout());
			String resource = pfEntry.getProfile().getSeeAlso().getResource();
			URI source = uriTools
					.relativePath(location, base.resolve(resource));
			readProfile(wfUri, source);
		}

		if (wb.getMainWorkflow() != null
				&& wb.getMainWorkflow().getResource() != null) {
			URI mainWfUri = base.resolve(wb.getMainWorkflow().getResource());
			Workflow mainWorkflow = (Workflow) resolveBeanUri(mainWfUri);
			if (mainWorkflow == null) {
				throw new ReaderException("Unknown main workflow " + mainWfUri +", got" + uriToBean.keySet());
			}
			workflowBundle.setMainWorkflow(mainWorkflow);
		}
		if (wb.getMainProfile() != null
				&& wb.getMainProfile().getResource() != null) {
			URI profileUri = base.resolve(wb.getMainProfile().getResource());
			uk.org.taverna.scufl2.api.profiles.Profile mainWorkflow = (uk.org.taverna.scufl2.api.profiles.Profile) resolveBeanUri(profileUri);
			workflowBundle.setMainProfile(mainWorkflow);
		}

		
	}

	protected WorkflowBean resolveBeanUri(URI uri) {
		WorkflowBean workflowBean = uriToBean.get(uri);
		if (workflowBean != null) {
			return workflowBean;
		}
		return uriTools.resolveUri(uri, workflowBundle);
	}

	protected void readProfile(URI profileUri, URI source) throws ReaderException {
		if (source.isAbsolute()) {
			throw new ReaderException("Can't read external profile source " + source);
		}

		uk.org.taverna.scufl2.api.profiles.Profile p = new uk.org.taverna.scufl2.api.profiles.Profile();
		p.setParent(workflowBundle);
		mapBean(profileUri, p);
	}

	protected void readWorkflow(URI wfUri, URI source) throws ReaderException, IOException {
		if (source.isAbsolute()) {
			throw new ReaderException("Can't read external workflow source " + source);
		}

		InputStream bundleStream = ucfPackage
				.getResourceAsInputStream(source.getPath());

		JAXBElement<WorkflowDocument> elem;
		try {
			elem = (JAXBElement<WorkflowDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse workflow document "
					+ source, e);
		}
		
		URI base = location.resolve(source);
		if (elem.getValue().getBase() != null) {
			base = location.resolve(elem.getValue().getBase());
		}
		
		if (elem.getValue().getAny().size() != 1) {
			throw new ReaderException("Expects only a <Workflow> element in " + source);
		}
		uk.org.taverna.scufl2.rdfxml.jaxb.Workflow workflow = (uk.org.taverna.scufl2.rdfxml.jaxb.Workflow) elem.getValue().getAny().get(0);

		currentBase = base;
		parseWorkflow(workflow, wfUri);
		
		
	}

	protected void parseWorkflow(
			uk.org.taverna.scufl2.rdfxml.jaxb.Workflow workflow, URI wfUri) {
		Workflow wf = new Workflow();
		wf.setParent(workflowBundle);
		
		if (workflow.getAbout() != null) {
			mapBean(currentBase.resolve(workflow.getAbout()), wf);
			// TODO: Compare resolved URI with desired wfUri
		} else {
			mapBean(wfUri, wf);
		}

		currentWorkflow = wf;
		
		if (workflow.getName() != null) {
			wf.setName(workflow.getName());
		}		
		if (workflow.getWorkflowIdentifier() != null && workflow.getWorkflowIdentifier().getResource() != null) {
			wf.setWorkflowIdentifier(currentBase.resolve(workflow.getWorkflowIdentifier().getResource()));
		}
		
		for (InputWorkflowPort inputWorkflowPort : workflow.getInputWorkflowPort()) {
			parseInputWorkflowPort(inputWorkflowPort.getInputWorkflowPort());
		}
		for (OutputWorkflowPort outputWorkflowPort : workflow.getOutputWorkflowPort()) {
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

	private void parseProcessor(uk.org.taverna.scufl2.rdfxml.jaxb.Processor processor) {
		uk.org.taverna.scufl2.api.core.Processor p = new uk.org.taverna.scufl2.api.core.Processor();
		currentProcessor = p;
		mapBean(currentBase.resolve(processor.getAbout()), p);
		if (processor.getName() != null) {
			p.setName(processor.getName());		
		}
		for (InputProcessorPort inputProcessorPort : processor.getInputProcessorPort()) {
			processorInputProcessorPort(inputProcessorPort.getInputProcessorPort());
		}
		for (OutputProcessorPort outputProcessorPort : processor.getOutputProcessorPort()) {
			processorOutputProcessorPort(outputProcessorPort.getOutputProcessorPort());
		}
		if (processor.getDispatchStack() != null) {
			parseDispatchStack(processor.getDispatchStack().getDispatchStack());
		}
		if (processor.getIterationStrategyStack() != null) {
			parseIterationStrategyStack(processor.getIterationStrategyStack().getIterationStrategyStack());
		}
	}

	private void parseDispatchStack(DispatchStack dispatchStack) {
		// TODO Auto-generated method stub
		
	}

	private void parseIterationStrategyStack(
			IterationStrategyStack iterationStrategyStack) {
		// TODO Auto-generated method stub
		
	}

	private void processorOutputProcessorPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputProcessorPort outputProcessorPort) {
		// TODO Auto-generated method stub
		
	}

	private void processorInputProcessorPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputProcessorPort inputProcessorPort) {
		// TODO Auto-generated method stub
		
	}

	private void parseDataLink(DataLink dataLink) {
		// TODO Auto-generated method stub
		
	}

	private void parseControlLink(Blocking blocking) {
		// TODO Auto-generated method stub
		
	}

	private void parseOutputWorkflowPort(uk.org.taverna.scufl2.rdfxml.jaxb.OutputWorkflowPort outputWorkflowPort) {
		// TODO Auto-generated method stub
		
	}

	private void parseInputWorkflowPort(uk.org.taverna.scufl2.rdfxml.jaxb.InputWorkflowPort inputWorkflowPort) {
		// TODO Auto-generated method stub
		
	}

	protected Map<URI, WorkflowBean> uriToBean = new HashMap<URI, WorkflowBean>();
	protected Map<WorkflowBean, URI> beanToUri = new HashMap<WorkflowBean, URI>();
	private URI location;

	protected void mapBean(URI uri, WorkflowBean bean) {
		uriToBean.put(uri, bean);
		beanToUri.put(bean, uri);
	}

	protected String findWorkflowBundlePath() {
		if (APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE.equals(ucfPackage
				.getPackageMediaType())) {
			for (ResourceEntry potentialRoot : ucfPackage.getRootFiles()) {
				if (APPLICATION_RDF_XML.equals(potentialRoot.getMediaType())) {
					return potentialRoot.getPath();
				}
			}
		}
		return RDFXMLWriter.WORKFLOW_BUNDLE_RDF;
	}

}
