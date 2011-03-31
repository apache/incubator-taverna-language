package uk.org.taverna.scufl2.api.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A writer for {@link WorkflowBundle}s.
 * 
 * Implementations specify workflow bundle formats (media types) that they can write.
 */
public interface WorkflowBundleWriter {

	/**
	 * Returns the media types that this writer can handle.
	 * 
	 * @return the media types that this writer can handle
	 */
	public Set<String> getMediaTypes();

	/**
	 * Writes a <code>WorkflowBundle</code> to a file with specified media type.
	 * 
	 * @param wfBundle
	 *            the workflow bundle to write
	 * @param destination
	 *            the file to write the workflow bundle to
	 * @param mediaType
	 *            the media type to write workflow bundle in
	 * @throws WriterException
	 *             if there is an error writing the workflow bundle
	 * @throws IOException
	 *             if there is an error writing the file
	 */
	public void writeBundle(WorkflowBundle wfBundle, File destination, String mediaType)
	throws WriterException, IOException;

	/**
	 * Writes a <code>WorkflowBundle</code> to a stream with specified media type.
	 * 
	 * @param wfBundle
	 *            the workflow bundle to write
	 * @param output
	 *            the stream to write the workflow bundle to
	 * @param mediaType
	 *            the media type to write workflow bundle in
	 * @throws WriterException
	 *             if there is an error writing the workflow bundle
	 * @throws IOException
	 *             if there is an error writing to the stream
	 */
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output, String mediaType)
	throws WriterException, IOException;

}
