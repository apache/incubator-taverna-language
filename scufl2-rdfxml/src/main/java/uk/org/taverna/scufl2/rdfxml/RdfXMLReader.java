package uk.org.taverna.scufl2.rdfxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;

public class RdfXMLReader implements WorkflowBundleReader {

	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";

	public static final String APPLICATION_RDF_XML = "application/rdf+xml";


	@Override
	public Set<String> getMediaTypes() {
		return Collections
				.singleton(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ParseException, IOException {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ParseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
