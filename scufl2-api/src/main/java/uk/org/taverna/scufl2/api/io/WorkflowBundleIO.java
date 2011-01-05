package uk.org.taverna.scufl2.api.io;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class WorkflowBundleIO {

	protected ServiceLoader<WorkflowBundleWriter> writers = ServiceLoader
	.load(WorkflowBundleWriter.class);
	protected ServiceLoader<WorkflowBundleReader> readers = ServiceLoader
	.load(WorkflowBundleReader.class);

	public WorkflowBundleWriter getWriterForMediaType(
			String mediaType) {
		for (WorkflowBundleWriter writer : getWriters()) {
			if (writer.getMediaTypes().contains(mediaType)) {
				return writer;
			}
		}
		return null;
	}

	public List<WorkflowBundleWriter> getWriters() {
		List<WorkflowBundleWriter> allWriters = new ArrayList<WorkflowBundleWriter>();
		for (WorkflowBundleWriter writer : writers) {
			allWriters.add(writer);
		}
		return allWriters;
	}



}
