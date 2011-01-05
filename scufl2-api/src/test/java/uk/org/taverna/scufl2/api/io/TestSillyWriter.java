package uk.org.taverna.scufl2.api.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestSillyWriter extends ExampleWorkflow {

	WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	WorkflowBundle wfBundle = makeWorkflowBundle();

	@Test
	public void addOwnWriters() {
		WorkflowBundleWriter myWriter = new WorkflowBundleWriter() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
		};
		bundleIO.addWriter(myWriter);
		assertEquals(2, bundleIO.getWriters().size());
		assertSame(myWriter, bundleIO.getWriters().get(1));
		assertSame(myWriter,
				bundleIO.getWriterForMediaType("application/vnd.example.myOwn"));

	}

	@Test
	public void getWorkflowBundleWriters() throws Exception {

		assertEquals(1, bundleIO.getWriters().size());
		WorkflowBundleWriter writer = bundleIO.getWriters().get(0);
		assertTrue(writer instanceof SillyWriter);
	}

	@Test
	public void getWriterForMediaType() throws Exception {
		WorkflowBundleWriter writer = bundleIO
		.getWriterForMediaType("application/vnd.example.silly");
		assertTrue(writer instanceof SillyWriter);
	}

	@Test
	public void getWriterForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getWriterForMediaType("application/vnd.example.unknownStuff"));
	}

}
