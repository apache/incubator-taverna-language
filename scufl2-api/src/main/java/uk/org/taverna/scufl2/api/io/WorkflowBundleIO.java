package uk.org.taverna.scufl2.api.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * Utility class for reading and writing <code>WorkflowBundle</code>s.
 */
public class WorkflowBundleIO {

	private static final Scufl2Tools scufl2Tools = new Scufl2Tools();
	// delay initialising the ServiceLoaders
	protected ServiceLoader<WorkflowBundleWriter> writersLoader;
	protected ServiceLoader<WorkflowBundleReader> readersLoader;

	private List<WorkflowBundleWriter> writers;

	private List<WorkflowBundleReader> readers;

	protected List<WorkflowBundleReader> discoverReaders() {
		synchronized (this) {

			if (readersLoader == null) {
				// FIXME: This uses Thread.currentThread.getContextClassLoader()
				// - depending on who gets synchronized-block first this can
				// vary - but as it's still local per instance of
				// WorkflowBundleIO it should be OK for now..
				readersLoader = ServiceLoader.load(WorkflowBundleReader.class);
			}
		}

		List<WorkflowBundleReader> allReaders = new ArrayList<WorkflowBundleReader>();
		for (WorkflowBundleReader reader : readersLoader) {
			allReaders.add(reader);
		}
		return allReaders;
	}

	protected List<WorkflowBundleWriter> discoverWriters() {
		synchronized (this) {
			if (writersLoader == null) {
				// FIXME: This uses Thread.currentThread.getContextClassLoader()
				// - depending on who gets synchronized-block first this can
				// vary - but as it's still local per instance of
				// WorkflowBundleIO it should be OK for now..
				writersLoader = ServiceLoader.load(WorkflowBundleWriter.class);
			}
		}
		List<WorkflowBundleWriter> allWriters = new ArrayList<WorkflowBundleWriter>();
		for (WorkflowBundleWriter writer : writersLoader) {
			allWriters.add(writer);
		}
		return allWriters;
	}

	/**
	 * Returns a <code>WorkflowBundleReader</code> for the specified media type.
	 * 
	 * If there is more than one <code>WorkflowBundleReader</code> for the specified media type the
	 * first reader discovered is returned. Subsequent calls to this method may return a different
	 * reader.
	 * 
	 * If there is no <code>WorkflowBundleReader</code> for the specified media type
	 * <code>null</code> is returned.
	 * 
	 * @param mediaType
	 *            the media type of the <code>WorkflowBundleReader</code>
	 * @return a <code>WorkflowBundleReader</code> for the specified media type
	 */
	public WorkflowBundleReader getReaderForMediaType(String mediaType) {
		for (WorkflowBundleReader reader : getReaders()) {
			if (reader.getMediaTypes().contains(mediaType)) {
				return reader;
			}
		}
		return null;
	}

	/**
	 * Returns all the available <code>WorkflowBundleReader</code>s.
	 * 
	 * @return all the available <code>WorkflowBundleReader</code>s
	 */
	public List<WorkflowBundleReader> getReaders() {
		if (readers == null) {
			return discoverReaders();
		}
		return readers;
	}

	/**
	 * Returns a <code>WorkflowBundleWriter</code> for the specified media type.
	 * 
	 * If there is more than one <code>WorkflowBundleWriter</code> for the specified media type the
	 * first writer discovered is returned. Subsequent calls to this method may return a different
	 * writer.
	 * 
	 * If there is no <code>WorkflowBundleWriter</code> for the specified media type
	 * <code>null</code> is returned.
	 * 
	 * @param mediaType
	 *            the media type of the <code>WorkflowBundleWriter</code>
	 * @return a <code>WorkflowBundleWriter</code> for the specified media type
	 */
	public WorkflowBundleWriter getWriterForMediaType(String mediaType) {
		for (WorkflowBundleWriter writer : getWriters()) {
			if (writer.getMediaTypes().contains(mediaType)) {
				return writer;
			}
		}
		return null;
	}

	/**
	 * Returns all the available <code>WorkflowBundleWriter</code>s.
	 * 
	 * @return all the available <code>WorkflowBundleWriter</code>s
	 */
	public List<WorkflowBundleWriter> getWriters() {
		if (writers == null) {
			return discoverWriters();
		}
		return writers;
	}

	/**
	 * Attempt to guess the media type for a stream or file that starts with
	 * these bytes. 
	 * <p>
	 * All registered {@link #getReaders()} are consulted. 
	 * <p>
	 * Return <code>null</code> if ambiguous (more than one possibility) or
	 * unknown.
	 * 
	 * @param firstBytes
	 *            The initial bytes, at least 512 bytes long unless the resource
	 *            is smaller.
	 * @return The recognised media type, or <code>null</code> if the bytes were
	 *         ambiguous or unknown.
	 */
	public String guessMediaTypeForSignature(byte[] firstBytes) {
		Set<String> mediaTypes = new HashSet<String>();
		for (WorkflowBundleReader reader : getReaders()) {
			String guess = reader.guessMediaTypeForSignature(firstBytes);
			if (guess != null) {
				mediaTypes.add(guess);
			}
		}
		if (mediaTypes.isEmpty()) {
			return null;
		}
		if (mediaTypes.size() > 1) {
			// log.warn("Multiple media types found: " + mediaTypes)
			return null;
		}
		return mediaTypes.iterator().next();
	}
	
	
	/**
	 * Reads a file containing a workflow bundle in the specified media type and returns a
	 * <code>WorkflowBundle</code>.
	 * 
	 * @param bundleFile
	 *            the file containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle. A <code>WorkflowBundleReader</code> must
	 *            exist for this media type.
	 *            If <code>null</code>, the media type will be guessed as with {@link #guessMediaTypeForSignature(byte[])}.
	 * @return the <code>WorkflowBundle</code> read from the file
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading the file
	 * @throws IllegalArgumentException
	 *             if a <code>WorkflowBundleReader</code> cannot be found for the media type
	 */
	public WorkflowBundle readBundle(File bundleFile, String mediaType) throws ReaderException,
	IOException {
		if (mediaType == null) {
			byte[] firstBytes = new byte[1024];
			new FileInputStream(bundleFile).read(firstBytes);
			mediaType = guessMediaTypeForSignature(firstBytes);
		}
		WorkflowBundleReader reader = getReaderForMediaType(mediaType);
		if (reader == null) {
			if (mediaType == null) {
				throw new IllegalArgumentException("Could not guess media type for " + bundleFile);
			}
			throw new IllegalArgumentException("Could not find reader for media type " + mediaType);
		}
		return reader.readBundle(bundleFile, mediaType);
	}

	/**
	 * Reads a stream containing a workflow bundle in the specified media type and returns a
	 * <code>WorkflowBundle</code>.
	 * 
	 * @param inputStream
	 *            the stream containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle. A <code>WorkflowBundleReader</code> must
	 *            exist for this media type.
	 *            If <code>null</code>, the media type will be guessed as with {@link #guessMediaTypeForSignature(byte[])}.
	 * @return the <code>WorkflowBundle</code> read from the stream
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading from the stream
	 * @throws IllegalArgumentException
	 *             if a <code>WorkflowBundleReader</code> cannot be found for the media type
	 */
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
	throws ReaderException, IOException {
		
		if (mediaType == null) {
			byte[] firstBytes = new byte[1024];
			inputStream = new BufferedInputStream(inputStream);
			try { 
				inputStream.mark(firstBytes.length*2);			
				inputStream.read(firstBytes);
				mediaType = guessMediaTypeForSignature(firstBytes);
			} finally {
				inputStream.reset();
				// Important = so readBundle can start from the beginning
			}
		}
		WorkflowBundleReader reader = getReaderForMediaType(mediaType);
		if (reader == null) {
			if (mediaType == null) {
				throw new IllegalArgumentException("Could not guess media type for input stream");
			}
			throw new IllegalArgumentException("Could not find reader for media type " + mediaType);
		}
		return reader.readBundle(inputStream, mediaType);
	}

	/**
	 * Reads a URL containing a workflow bundle in the specified media type and returns a
	 * <code>WorkflowBundle</code>.
	 * 
	 * @param url
	 *            the URL containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle. A <code>WorkflowBundleReader</code> must
	 *            exist for this media type
	 *            If <code>null</code>, the media type will 
	 *            be guessed as with {@link #guessMediaTypeForSignature(byte[])}.
	 * @return the <code>WorkflowBundle</code> read from the URL
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading from the stream
	 * @throws IllegalArgumentException
	 *             if a <code>WorkflowBundleReader</code> cannot be found for the media type
	 */
	public WorkflowBundle readBundle(URL url, String mediaType) throws ReaderException, IOException {
		// TODO: Pass URL to reader
		// TODO: Use known media type for Accept header
		// TODO: Extract Content-Type from result
		return readBundle(url.openStream(), mediaType);

	}

	/**
	 * Sets the <code>WorkflowBundleReader</code>s.
	 * <p>
	 * This method will normally be called by Spring when wiring beans.
	 * 
	 * @param readers
	 *            the <code>WorkflowBundleReader</code>s
	 */
	public void setReaders(List<WorkflowBundleReader> readers) {
		this.readers = readers;
	}

	/**
	 * Sets the <code>WorkflowBundleWriter</code>s.
	 * <p>
	 * This method will normally be called by Spring when wiring beans.
	 * 
	 * @param readers
	 *            the <code>WorkflowBundleWriter</code>s
	 */
	public void setWriters(List<WorkflowBundleWriter> writers) {
		this.writers = writers;
	}

	/**
	 * Write a <code>WorkflowBundle</code> to a file with specified media type.
	 * <p>
	 * {@link Scufl2Tools#setParents(WorkflowBundle)} will be called on the bundle to
	 * ensure everything contained by the bundle has it as an ancestor.
	 * 
	 * @param wfBundle
	 *            the workflow bundle to write
	 * @param destination
	 *            the file to write the workflow bundle to
	 * @param mediaType
	 *            the media type to write workflow bundle in. A <code>WorkflowBundleWriter</code>
	 *            must exist for this media type
	 * @throws WriterException
	 *             if there is an error writing the workflow bundle
	 * @throws IOException
	 *             if there is an error writing the file
	 * @throws IllegalArgumentException
	 *             if a <code>WorkflowBundleWriter</code> cannot be found for the media type
	 */
	public void writeBundle(WorkflowBundle wfBundle, File destination, String mediaType)
	throws WriterException, IOException {
		WorkflowBundleWriter writer = getWriterForMediaType(mediaType);
		if (writer == null) {
			throw new IllegalArgumentException("Could not find writer for media type " + mediaType);
		}
		
		scufl2Tools.setParents(wfBundle);		
		writer.writeBundle(wfBundle, destination, mediaType);
	}

	/**
	 * Write a <code>WorkflowBundle</code> to a stream with specified media type.
	 * <p>
	 * {@link Scufl2Tools#setParents(WorkflowBundle)} will be called on the bundle to
	 * ensure everything contained by the bundle has it as an ancestor.
	 * 
	 * @param wfBundle
	 *            the workflow bundle to write
	 * @param output
	 *            the stream to write the workflow bundle to
	 * @param mediaType
	 *            the media type to write workflow bundle in. A <code>WorkflowBundleWriter</code>
	 *            must exist for this media type
	 * @throws WriterException
	 *             if there is an error writing the workflow bundle
	 * @throws IOException
	 *             if there is an error writing to the stream
	 * @throws IllegalArgumentException
	 *             if a <code>WorkflowBundleWriter</code> cannot be found for the media type
	 */
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output, String mediaType)
	throws WriterException, IOException {
		WorkflowBundleWriter writer = getWriterForMediaType(mediaType);
		if (writer == null) {
			throw new IllegalArgumentException("Could not find writer for media type " + mediaType);
		}
		scufl2Tools.setParents(wfBundle);		
		writer.writeBundle(wfBundle, output, mediaType);
	}

}
