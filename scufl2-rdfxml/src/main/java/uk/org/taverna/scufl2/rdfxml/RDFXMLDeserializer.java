package uk.org.taverna.scufl2.rdfxml;

import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_RDF_XML;
import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundleDocument;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class RDFXMLDeserializer {

	private final UCFPackage ucfPackage;
	private WorkflowBundle workflowBundle;
	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	private Scufl2Tools scufl2Tools = new Scufl2Tools();

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
	public WorkflowBundle parseWorkflowBundle(URI location) throws IOException,
			ReaderException {
		if (location == null) {
			location = URI.create("");
		}
		workflowBundle = new WorkflowBundle();
		workflowBundle.setResources(ucfPackage);

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

		URI base = location;
		if (workflowBundleDocument.getBase() != null) {
			base = location.resolve(workflowBundleDocument.getBase());
		}

		if (workflowBundleDocument.getAny().size() != 1) {
			throw new ReaderException(
					"Invalid WorkflowBundleDocument, expected only one <WorkflowBundle>");
		}
		uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle wb = (uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle) workflowBundleDocument
				.getAny().get(0);
		workflowBundle.setName(wb.getName());
		if (wb.getSameBaseAs() != null
				&& wb.getSameBaseAs().getResource() != null) {
			workflowBundle.setSameBaseAs(base.resolve(wb.getSameBaseAs()
					.getResource()));
		}

		scufl2Tools.setParents(workflowBundle);
		return workflowBundle;
	}

	protected String findWorkflowBundlePath() {
		String rootFile = null;
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
