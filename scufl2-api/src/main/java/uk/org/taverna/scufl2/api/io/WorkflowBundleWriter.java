package uk.org.taverna.scufl2.api.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public interface WorkflowBundleWriter {

	public Set<String> getMediaTypes();

	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws ParseException, IOException;

	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws ParseException, IOException;

}
