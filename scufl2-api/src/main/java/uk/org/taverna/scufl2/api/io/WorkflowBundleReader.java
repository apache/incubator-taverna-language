package uk.org.taverna.scufl2.api.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A reader for {@link WorkflowBundle}s.
 * 
 * Implementations specify workflow bundle formats (media types) that they can read.
 */
public interface WorkflowBundleReader {

	/**
	 * Returns the media types that this reader can handle.
	 * 
	 * @return the media types that this reader can handle
	 */
	public Set<String> getMediaTypes();

	/**
	 * Reads a file containing a workflow bundle in the specified media type and returns a
	 * <code>WorkflowBundle</code>.
	 * 
	 * @param bundleFile
	 *            the file containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle
	 * @return the <code>WorkflowBundle</code> read from the file
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading the file
	 */
	public WorkflowBundle readBundle(File bundleFile, String mediaType) throws ReaderException,
	IOException;

	/**
	 * Reads a stream containing a workflow bundle in the specified media type and returns a
	 * <code>WorkflowBundle</code>.
	 * 
	 * @param inputStream
	 *            the stream containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle
	 * @return the <code>WorkflowBundle</code> read from the stream
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading from the stream
	 */
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
	throws ReaderException, IOException;

}
