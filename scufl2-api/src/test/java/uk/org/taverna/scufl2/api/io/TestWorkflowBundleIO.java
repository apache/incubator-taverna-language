package uk.org.taverna.scufl2.api.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestWorkflowBundleIO extends ExampleWorkflow {

	WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	WorkflowBundle wfBundle = makeWorkflowBundle();

	@Test
	public void getReaderForMediaType() throws Exception {
		WorkflowBundleReader Reader = bundleIO
		.getReaderForMediaType("application/vnd.example.silly");
		assertTrue(Reader instanceof SillyReader);
	}

	@Test
	public void getReaderForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getReaderForMediaType("application/vnd.example.unknownStuff"));
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
		.getWriterForMediaType("application/vnd.example.silly");
		assertTrue(writer instanceof SillyWriter);
	}

	@Test
	public void getWriterForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getWriterForMediaType("application/vnd.example.unknownStuff"));
	}

	@Test
	public void setReaders() {
		WorkflowBundleReader myReader = new WorkflowBundleReader() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
		};

		bundleIO.setReaders(Collections.singletonList(myReader));
		assertEquals(1, bundleIO.getReaders().size());
		assertSame(myReader, bundleIO.getReaders().get(0));
		assertSame(myReader,
				bundleIO.getReaderForMediaType("application/vnd.example.myOwn"));

		// Should now be null
		assertNull(bundleIO
				.getReaderForMediaType("application/vnd.example.silly"));
	}

	@Test
	public void setWriters() {
		WorkflowBundleWriter myWriter = new WorkflowBundleWriter() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
		};

		bundleIO.setWriters(Collections.singletonList(myWriter));
		assertEquals(1, bundleIO.getWriters().size());
		assertSame(myWriter, bundleIO.getWriters().get(0));
		assertSame(myWriter,
				bundleIO.getWriterForMediaType("application/vnd.example.myOwn"));

		// Should now be null
		assertNull(bundleIO
				.getWriterForMediaType("application/vnd.example.silly"));
	}

	@Test
	public void writeBundleFile() throws Exception {
		File bundleFile = File.createTempFile("scufl2", "txt");
		bundleIO.writeBundle(wfBundle, bundleFile,
		"application/vnd.example.silly");
		String bundleTxt = FileUtils.readFileToString(bundleFile, "utf-8");
		assertTrue(bundleTxt
				.equals("WorkflowBundle 'helloWorld'\n"
						+ "  MainWorkflow 'HelloWorld'\n"
						+ "  Workflow 'HelloWorld'\n"
						+ "    In 'yourName'\n"
						+ "    Out 'results'\n"
						+ "    Processor 'Hello'\n"
						+ "      In 'name'\n"
						+ "      Out 'greeting'\n"
						+ "    Links\n"
						+ "       'yourName' -> 'results'\n"
						+ "       'yourName' -> 'Hello:name'\n"
						+ "       'Hello:greeting' -> 'results'\n"
						+ "  MainProfile 'tavernaWorkbench'\n"
						+ "  Profile 'tavernaWorkbench'\n"
						+ "    Activity 'HelloScript'\n"
						+ "      Type <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Activity>\n"
						+ "      In 'personName'\n"
						+ "      Out 'hello'\n"
						+ "    ProcessorBinding 'Hello'\n"
						+ "      Activity 'HelloScript'\n"
						+ "      Processor 'HelloWorld:Hello'\n"
						+ "      InputPortBindings\n"
						+ "        'name' -> 'personName'\n"
						+ "      OutputPortBindings\n"
						+ "        'hello' -> 'greeting'\n"
						+ "    ActivatesConfiguration 'Hello'\n"
						+ "    Configuration 'Hello'\n"
						+ "       Type <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Configuration>\n"
						+ "       Configures 'activity/HelloScript'\n"
						+ "       Property <http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Script>\n"
						+ "         '''hello = \"Hello, \" + personName;\n"
						+ "System.out.println(\"Server says: \" + hello);'''\n"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeBundleUnknownMediaType() throws Exception {
		File bundleFile = File.createTempFile("scufl2", "txt");
		bundleIO.writeBundle(wfBundle, bundleFile,
		"application/vnd.example.unknownStuff");
	}

}
