package uk.org.taverna.scufl2.api.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public interface WorkflowBundleReader {

	public Set<String> getMediaTypes();

	public WorkflowBundle readBundle(File bundleFile, String mediaType)
	throws ParseException, IOException;

	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
	throws ParseException, IOException;

}
