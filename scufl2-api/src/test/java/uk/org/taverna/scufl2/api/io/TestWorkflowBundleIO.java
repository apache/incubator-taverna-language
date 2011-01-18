package uk.org.taverna.scufl2.api.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.api.io.SillyReader.APPLICATION_VND_EXAMPLE_SILLY;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestWorkflowBundleIO extends ExampleWorkflow {

	private static final String UTF_8 = "utf-8";
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	protected WorkflowBundle wfBundle = makeWorkflowBundle();

	@Test
	public void getReaderForMediaType() throws Exception {
		WorkflowBundleReader Reader = bundleIO
		.getReaderForMediaType(APPLICATION_VND_EXAMPLE_SILLY);
		assertTrue(Reader instanceof SillyReader);
	}

	@Test
	public void getReaderForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getReaderForMediaType("application/vnd.example.unknownStuff"));
	}

	public String getSillyFormatWorkflowBundle() {
		return "WorkflowBundle 'HelloWorld'\n"
		+ "  MainWorkflow 'HelloWorld'\n"
		+ "  Workflow 'HelloWorld'\n"
		+ "    In 'yourName'\n"
		+ "    Out 'results'\n"
		+ "    Processor 'Hello'\n"
		+ "      In 'name'\n"
		+ "      Out 'greeting'\n"
		+ "    Links\n"
		+ "      'Hello:greeting' -> 'results'\n"
		+ "      'yourName' -> 'Hello:name'\n"
		+ "      'yourName' -> 'results'\n"
		+ "  MainProfile 'tavernaWorkbench'\n"
		+ "  Profile 'tavernaServer'\n"
		+ "    Activity 'HelloScript'\n"
		+ "      Type <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Activity>\n"
		+ "      In 'personName'\n"
		+ "      Out 'hello'\n"
		+ "    ProcessorBinding\n"
		+ "      Activity 'HelloScript'\n"
		+ "      Processor 'HelloWorld:Hello'\n"
		+ "      InputPortBindings\n"
		+ "        'name' -> 'personName'\n"
		+ "      OutputPortBindings\n"
		+ "        'hello' -> 'greeting'\n"
		+ "    Configuration 'Hello'\n"
		+ "      Type <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Configuration>\n"
		+ "      Configures 'activity/HelloScript'\n"
		+ "      Property <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#script>\n"
		+ "        '''hello = \"Hello, \" + personName;\n"
		+ "System.out.println(\"Server says: \" + hello);'''\n"
		+ "  Profile 'tavernaWorkbench'\n"
		+ "    Activity 'HelloScript'\n"
		+ "      Type <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Activity>\n"
		+ "      In 'personName'\n"
		+ "      Out 'hello'\n"
		+ "    ProcessorBinding\n"
		+ "      Activity 'HelloScript'\n"
		+ "      Processor 'HelloWorld:Hello'\n"
		+ "      InputPortBindings\n"
		+ "        'name' -> 'personName'\n"
		+ "      OutputPortBindings\n"
		+ "        'hello' -> 'greeting'\n"
		+ "    Configuration 'Hello'\n"
		+ "      Type <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Configuration>\n"
		+ "      Configures 'activity/HelloScript'\n"
		+ "      Property <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#script>\n"
		+ "        '''hello = \"Hello, \" + personName;\n"
		+ "System.out.println(\"Server says: \" + hello);'''\n";
	}

	@Test
	public void getWorkflowBundleReaders() throws Exception {
		assertEquals(1, bundleIO.getReaders().size());
		WorkflowBundleReader Reader = bundleIO.getReaders().get(0);
		assertTrue(Reader instanceof SillyReader);
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
		.getWriterForMediaType(APPLICATION_VND_EXAMPLE_SILLY);
		assertTrue(writer instanceof SillyWriter);
	}

	@Test
	public void getWriterForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getWriterForMediaType("application/vnd.example.unknownStuff"));
	}

	@Test
	public void readBundleFile() throws Exception {
		File bundleFile = tempFile();
		FileUtils.writeStringToFile(bundleFile, getSillyFormatWorkflowBundle(),
				UTF_8);
		WorkflowBundle wfBundle = bundleIO.readBundle(bundleFile,
				APPLICATION_VND_EXAMPLE_SILLY);
		assertEquals("HelloWorld", wfBundle.getName());
		assertEquals("HelloWorld", wfBundle.getMainWorkflow().getName());
		assertTrue(wfBundle.getMainWorkflow().getProcessors()
				.containsName("Hello"));
	}

	@Test
	public void readBundleStream() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getSillyFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle wfBundle = bundleIO.readBundle(inputStream,
				APPLICATION_VND_EXAMPLE_SILLY);
		assertEquals("HelloWorld", wfBundle.getName());
		assertEquals("HelloWorld", wfBundle.getMainWorkflow().getName());
		assertTrue(wfBundle.getMainWorkflow().getProcessors()
				.containsName("Hello"));
	}

	@Test
	public void readToWriteRoundTrip() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getSillyFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle readBundle = bundleIO.readBundle(inputStream,
				APPLICATION_VND_EXAMPLE_SILLY);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(readBundle, output, APPLICATION_VND_EXAMPLE_SILLY);
		String bundleTxt = new String(output.toByteArray(), UTF_8);
		assertEquals(getSillyFormatWorkflowBundle(), bundleTxt);
	}

	@Test
	public void setReaders() {
		WorkflowBundleReader myReader = new WorkflowBundleReader() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
			@Override
			public WorkflowBundle readBundle(File bundleFile, String mediaType) {
				return null;
			}
			@Override
			public WorkflowBundle readBundle(InputStream inputStream,
					String mediaType) {
				return null;
			}
		};

		bundleIO.setReaders(Collections.singletonList(myReader));
		assertEquals(1, bundleIO.getReaders().size());
		assertSame(myReader, bundleIO.getReaders().get(0));
		assertSame(myReader,
				bundleIO.getReaderForMediaType("application/vnd.example.myOwn"));

		// Should now be null
		assertNull(bundleIO
				.getReaderForMediaType(APPLICATION_VND_EXAMPLE_SILLY));
	}

	@Test
	public void setWriters() {
		WorkflowBundleWriter myWriter = new WorkflowBundleWriter() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
			@Override
			public void writeBundle(WorkflowBundle wfBundle, File destination,
					String mediaType) {
			}
			@Override
			public void writeBundle(WorkflowBundle wfBundle,
					OutputStream output, String mediaType) {
			}
		};

		bundleIO.setWriters(Collections.singletonList(myWriter));
		assertEquals(1, bundleIO.getWriters().size());
		assertSame(myWriter, bundleIO.getWriters().get(0));
		assertSame(myWriter,
				bundleIO.getWriterForMediaType("application/vnd.example.myOwn"));

		// Should now be null
		assertNull(bundleIO
				.getWriterForMediaType(APPLICATION_VND_EXAMPLE_SILLY));
	}

	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("scufl2", "txt");
		bundleFile.deleteOnExit();
		return bundleFile;
	}

	@Test
	public void writeBundleFile() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(wfBundle, bundleFile,
				APPLICATION_VND_EXAMPLE_SILLY);
		String bundleTxt = FileUtils.readFileToString(bundleFile, UTF_8);
		assertEquals(getSillyFormatWorkflowBundle(), bundleTxt);
	}

	@Test
	public void writeBundleStream() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(wfBundle, output, APPLICATION_VND_EXAMPLE_SILLY);
		String bundleTxt = new String(output.toByteArray(), UTF_8);
		assertEquals(getSillyFormatWorkflowBundle(), bundleTxt);
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeBundleUnknownMediaType() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(wfBundle, bundleFile,
		"application/vnd.example.unknownStuff");
	}

	@Test(expected = IOException.class)
	public void writeBundleWrongLocation() throws Exception {
		File bundleDir = tempFile();
		bundleDir.delete();
		File bundleFile = new File(bundleDir, "nonExistingDir");
		bundleIO.writeBundle(wfBundle, bundleFile,
				APPLICATION_VND_EXAMPLE_SILLY);
	}

}
