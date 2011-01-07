package uk.org.taverna.scufl2.api.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class WorkflowBundleIO {

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

	public WorkflowBundleReader getReaderForMediaType(String mediaType) {
		for (WorkflowBundleReader reader : getReaders()) {
			if (reader.getMediaTypes().contains(mediaType)) {
				return reader;
			}
		}
		return null;
	}

	public List<WorkflowBundleReader> getReaders() {
		if (readers == null) {
			return discoverReaders();
		}
		return readers;
	}

	public WorkflowBundleWriter getWriterForMediaType(String mediaType) {
		for (WorkflowBundleWriter writer : getWriters()) {
			if (writer.getMediaTypes().contains(mediaType)) {
				return writer;
			}
		}
		return null;
	}

	public List<WorkflowBundleWriter> getWriters() {
		if (writers == null) {
			return discoverWriters();
		}
		return writers;
	}

	public WorkflowBundle readBundle(File bundleFile, String mediaType)
	throws ParseException, IOException {
		WorkflowBundleReader reader = getReaderForMediaType(mediaType);
		if (reader == null) {
			throw new IllegalArgumentException(
					"Could not find reader for media type " + mediaType);
		}
		return reader.readBundle(bundleFile, mediaType);
	}

	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
	throws ParseException, IOException {
		WorkflowBundleReader reader = getReaderForMediaType(mediaType);
		if (reader == null) {
			throw new IllegalArgumentException(
					"Could not find reader for media type " + mediaType);
		}
		return reader.readBundle(inputStream, mediaType);
	}

	public void setReaders(List<WorkflowBundleReader> readers) {
		this.readers = readers;
	}

	public void setWriters(List<WorkflowBundleWriter> writers) {
		this.writers = writers;
	}

	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws ParseException, IOException {
		WorkflowBundleWriter writer = getWriterForMediaType(mediaType);
		if (writer == null) {
			throw new IllegalArgumentException(
					"Could not find writer for media type " + mediaType);
		}
		writer.writeBundle(wfBundle, destination, mediaType);
	}

	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws ParseException, IOException {
		WorkflowBundleWriter writer = getWriterForMediaType(mediaType);
		if (writer == null) {
			throw new IllegalArgumentException(
					"Could not find writer for media type " + mediaType);
		}
		writer.writeBundle(wfBundle, output, mediaType);

	}

}
