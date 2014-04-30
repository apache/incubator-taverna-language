package uk.org.taverna.scufl2.api.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import uk.org.taverna.scufl2.api.annotation.Revisioned;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Utility class for reading and writing <code>WorkflowBundle</code>s.
 * <p>
 * This class depends on implemented {@link WorkflowBundleReader} and
 * {@link WorkflowBundleWriter} instances, which are discovered from the
 * classpath using {@link ServiceLoader} or set using {@link #setReaders(List)}
 * and {@link #setWriters(List)}. An OSGi service descriptors is provided for
 * instantiating this class as bean <code>workflowBundleIO</code>, while non-OSGi
 * uses can just instantiate this class where needed.
 * <p>
 * The methods {@link #readBundle(File, String)},
 * {@link #readBundle(InputStream, String)}, {@link #readBundle(URL, String)}
 * and {@link #writeBundle(WorkflowBundle, File, String)}/
 * {@link #writeBundle(WorkflowBundle, OutputStream, String)} take an argument
 * to indicate the media type of the format. The reader methods from file and
 * URL allow the parameter to be <code>null</code> in order to guess the format,
 * but the writer method requires the format to be specified explicitly.
 * <p>
 * Known supported formats (as of 2013-04-23):
 * <dl>
 * <dt>text/vnd.taverna.scufl2.structure</dt>
 * <dd>A textual tree-view, useful for debugging, but probably incomplete for
 * actual workflow execution. Reader and writer provided by scufl2-api (this
 * module).</dd>
 * <dt>application/vnd.taverna.scufl2.workflow-bundle</dt>
 * <dd>The <a href=
 * 'http://dev.mygrid.org.uk/wiki/display/developer/Taverna+Workflow+Bundle'>SCU
 * F L 2 workflow bundle</a> format, a ZIP container of RDF/XML files. Reader
 * and writer provided by the scufl2-rdfxml module.</dd>
 * <dt>application/vnd.taverna.t2flow+xml</dt>
 * <dd>The Taverna 2 workflow format t2flow. An XML format based on XMLBeans
 * serialization of T2 java objects. Reader provided by the scufl2-t2flow
 * module.
 * <dt>application/vnd.taverna.scufl+xml</dt>
 * <dd>The Taverna 1 workflow format SCUFL. An XML format made for the FreeFluo
 * workflow engine. Experimental reader provided by the scufl2-scufl module.
 * <dt>text/vnd.wf4ever.wfdesc+turtle</dt>
 * <dd>An abstract workflow structure format by the <a
 * href="http://www.wf4ever-project.org/">Wf4Ever project. RDF Turtle according
 * to the <a href="http://purl.org/wf4ever/model">wfdesc ontology</a>. Writer
 * provided by the third-party <a
 * href="https://github.com/wf4ever/scufl2-wfdesc">scufl2-wfdesc</a> module.
 * </dl>
 */
public class WorkflowBundleIO {
	private static Logger log = Logger.getLogger(WorkflowBundleIO.class.getCanonicalName());
	
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
     * Get the supported media types for reading.
     * <p>
     * Returned media types can be used with {@link #readBundle(File, String)}, {@link #readBundle(InputStream, String)} and/or {@link #readBundle(URL, String)}.
     * 
     * @return A (usually sorted) set of media types
     */
    public Set<String> getSupportedReaderMediaTypes() {	    
        Set<String> mediaTypes = new TreeSet<String>();
        for (WorkflowBundleReader reader : getReaders()) {
            mediaTypes.addAll(reader.getMediaTypes());
        }
        return mediaTypes;
    }

    /**
     * Get the supported media types for writing.
     * <p>
     * Returned media types can be used with {@link #writeBundle(WorkflowBundle, File, String) and/or #writeBundle(WorkflowBundle, OutputStream, String).
     * 
     * @return A (usually sorted) set of media types
     */
    public Set<String> getSupportedWriterMediaTypes() {
        Set<String> mediaTypes = new TreeSet<String>();
        for (WorkflowBundleWriter writer : getWriters()) {
            mediaTypes.addAll(writer.getMediaTypes());
        }
        return mediaTypes;
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
			log.warning("Multiple media types found: " + mediaTypes);
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
			FileInputStream fileIn = new FileInputStream(bundleFile);
			try {			
				fileIn.read(firstBytes);
			} finally {
				fileIn.close();
			}
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
    public WorkflowBundle readBundle(URL url, String mediaType)
            throws ReaderException, IOException {
        URLConnection connection = url.openConnection();
        if (mediaType != null && !mediaType.isEmpty()) {
            addAcceptHeaders(mediaType, connection);
        } else {
            for (String supportedType : getSupportedReaderMediaTypes()) {
                addAcceptHeaders(supportedType, connection);
                connection.addRequestProperty("Accept", "*/*;q=0.1");
            }
        }

        InputStream inputStream = connection.getInputStream();
        try {
            String contentType = connection.getContentType();
            List<String> ignoreTypes = Arrays.asList("text/plain",
                    "application/octet-stream", "application/zip",
                    "application/x-zip-compressed", "text/xml",
                    "application/xml", "content/unknown");
            if (contentType == null || contentType.isEmpty()) {
                contentType = mediaType; // might still be null -> guess
            } else {
                for (String ignore : ignoreTypes) {
                    if (contentType.toLowerCase().startsWith(ignore)) {
                        contentType = mediaType; // might still be null -> guess
                    }
                }
            }
            // TODO: Pass URL to reader (as baseURI)
            return readBundle(url.openStream(), contentType);
        } finally {
            inputStream.close();
        }
    }

    private void addAcceptHeaders(String mediaType, URLConnection connection) {
        connection.addRequestProperty("Accept", mediaType);

        if (mediaType.endsWith("+zip")
                || mediaType.equals("vnd.taverna.scufl2.workflow-bundle")) {
            connection
                    .addRequestProperty("Accept", "application/zip;q=0.5");
            connection.addRequestProperty("Accept",
                    "application/x-zip-compressed;q=0.5");
        } else if (mediaType.endsWith("+xml")) {
            connection
                    .setRequestProperty("Accept", "application/xml;q=0.6");
            connection.setRequestProperty("Accept", "text/xml;q=0.5");
        }
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

	/**
	 * Create a new WorkflowBundle with a default workflow and profile.
	 * <p>
	 * Unlike the {@link WorkflowBundle} constructor, this method will also make
	 * a {@link WorkflowBundle#getMainWorkflow()} and
	 * {@link WorkflowBundle#getMainProfile()}, simplifying construction of
	 * workflow bundles from scratch.
	 * <p>
	 * Each of the bundle, workflow and profile will also have a revision set
	 * using {@link Revisioned#newRevision()} and their names set to
	 * short default values.
	 * 
	 * @return A template {@link WorkflowBundle} which has a main workflow and main profile
	 */
	public WorkflowBundle createBundle() {
		WorkflowBundle wb = new WorkflowBundle();
		wb.setName("bundle1");
		
		Workflow workflow = new Workflow();
		workflow.setName("workflow1");
		workflow.setParent(wb);
		workflow.newRevision();

		Profile profile = new Profile();
		profile.setName("profile1");
		profile.setParent(wb);
		profile.newRevision();
		
		wb.setMainWorkflow(workflow);
		wb.setMainProfile(profile);
		wb.newRevision();
		return wb;
	}

}
