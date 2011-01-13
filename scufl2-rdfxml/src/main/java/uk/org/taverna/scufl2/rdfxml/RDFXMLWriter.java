package uk.org.taverna.scufl2.rdfxml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WorkflowBundleWriter;
import uk.org.taverna.scufl2.api.io.WriterException;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import static uk.org.taverna.scufl2.rdfxml.RDFXMLReader.*;

public class RDFXMLWriter implements WorkflowBundleWriter {

	private static final String RDF = ".rdf";
	private static final String WORKFLOW = "workflow/";
	private static final String PROFILE = "profile/";
	private static final String WORKFLOW_BUNDLE_RDF = "workflowBundle.rdf";

	@Override
	public Set<String> getMediaTypes() {
		return Collections
				.singleton(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws WriterException, IOException {
		UCFPackage ucfPackage = makeUCFPackage(wfBundle);
		ucfPackage.save(destination);
	}

	protected UCFPackage makeUCFPackage(WorkflowBundle wfBundle)
			throws IOException, WriterException {
		UCFPackage ucfPackage = new UCFPackage();
		ucfPackage
				.setPackageMediaType(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);

		RDFXMLSerializer serializer = new RDFXMLSerializer(wfBundle);
		
		for (Workflow wf : wfBundle.getWorkflows()) {
			String path = WORKFLOW + validFilename(wf.getName()) + RDF;

			OutputStream outputStream = ucfPackage
					.addResourceUsingOutputStream(path, APPLICATION_RDF_XML);
			try {
				serializer.workflowDoc(outputStream, wf, URI.create(path));
			} catch (JAXBException e) {
				throw new WriterException("Can't generate " + path, e);
			}
			outputStream.close();
		}

		for (Profile pf : wfBundle.getProfiles()) {
			String path = PROFILE + validFilename(pf.getName()) + RDF;
			OutputStream outputStream = ucfPackage
					.addResourceUsingOutputStream(path, APPLICATION_RDF_XML);
			try {
				serializer.profileDoc(outputStream, pf, URI.create(path));
			} catch (JAXBException e) {
				throw new WriterException("Can't generate " + path, e);
			}
			outputStream.close();

		}

		OutputStream outputStream = ucfPackage.addResourceUsingOutputStream(
				WORKFLOW_BUNDLE_RDF, APPLICATION_RDF_XML);
		try {
			serializer.workflowBundleDoc(outputStream,
					URI.create(WORKFLOW_BUNDLE_RDF));
		} catch (JAXBException e) {
			throw new WriterException("Can't generate " + WORKFLOW_BUNDLE_RDF,
					e);
		}
		outputStream.close();
		ucfPackage.setRootFile(WORKFLOW_BUNDLE_RDF);

		return ucfPackage;

	}

	public String validFilename(String name) {

		// Make a relative URI
		URI uri;
		try {
			uri = new URI(null, null, name, null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid name " + name);
		}
		String ascii = uri.toASCIIString();
		// And escape / and \
		String escaped = ascii.replace("/", "%2f");
		escaped = escaped.replace("\\", "%5c");
		escaped = escaped.replace(":", "%3a");
		return escaped;
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws WriterException, IOException {
		UCFPackage ucfPackage = makeUCFPackage(wfBundle);
		ucfPackage.save(output);
	}

}
