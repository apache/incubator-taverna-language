package uk.org.taverna.scufl2.rdfxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

public class RDFXMLReader implements WorkflowBundleReader {
	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	public static final String APPLICATION_RDF_XML = "application/rdf+xml";

	@Override
	public Set<String> getMediaTypes() {
		return Collections.singleton(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		UCFPackage ucfPackage = new UCFPackage(bundleFile);
		WorkflowBundleParser deserializer = new WorkflowBundleParser();
		return deserializer.readWorkflowBundle(ucfPackage, bundleFile.toURI());
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		UCFPackage ucfPackage = new UCFPackage(inputStream);
		WorkflowBundleParser deserializer = new WorkflowBundleParser();
		return deserializer.readWorkflowBundle(ucfPackage, URI.create(""));
	}

	@Override
	public String guessMediaTypeForSignature(byte[] firstBytes) {
		if (firstBytes.length < 100)
			return null;
		Charset latin1 = Charset.forName("ISO-8859-1");
		String pk = new String(firstBytes, 0, 2, latin1);
		if (!pk.equals("PK"))
			return null;
		String mimetype = new String(firstBytes, 30, 8, latin1);
		if (!mimetype.equals("mimetype"))
			return null;
		String bundle = new String(firstBytes, 38,
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE.length(), latin1);
		if (!bundle.equals(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE))
			return null;
		return APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;
	}
}
