package uk.org.taverna.scufl2.wfdesc;

import static uk.org.taverna.scufl2.wfdesc.WfdescReader.TEXT_VND_WF4EVER_WFDESC_TURTLE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.purl.wf4ever.wfdesc.Workflow;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleWriter;
import uk.org.taverna.scufl2.api.io.WriterException;

public class WfdescWriter implements WorkflowBundleWriter {

	@Override
	public Set<String> getMediaTypes() {
		return new HashSet<String>(
				Arrays.asList(TEXT_VND_WF4EVER_WFDESC_TURTLE));
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws WriterException, IOException {
		if (!mediaType.equals(TEXT_VND_WF4EVER_WFDESC_TURTLE)) {
			throw new WriterException("Unsupported media type: " + mediaType);
		}
		WfdescSerialiser serializer = new WfdescSerialiser();
		BufferedOutputStream outStream = new BufferedOutputStream(
				new FileOutputStream(destination));
		try {
			serializer.save(wfBundle, outStream);
		} finally {
			outStream.close();
		}

	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws WriterException, IOException {
		if (!mediaType.equals(TEXT_VND_WF4EVER_WFDESC_TURTLE)) {
			throw new WriterException("Unsupported media type: " + mediaType);
		}

		WfdescSerialiser serializer = new WfdescSerialiser();
		serializer.save(wfBundle, output);

	}

}
