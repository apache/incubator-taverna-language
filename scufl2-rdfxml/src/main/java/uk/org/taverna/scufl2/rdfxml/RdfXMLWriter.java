package uk.org.taverna.scufl2.rdfxml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleWriter;

public class RdfXMLWriter implements WorkflowBundleWriter {

	@Override
	public Set<String> getMediaTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws ParseException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws ParseException, IOException {
		// TODO Auto-generated method stub

	}

}
