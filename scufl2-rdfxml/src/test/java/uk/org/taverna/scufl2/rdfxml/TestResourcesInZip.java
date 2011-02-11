package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.rdfxml.TestRDFXMLWriter.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

public class TestResourcesInZip {

	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	protected ExampleWorkflow exampleWorkflow = new ExampleWorkflow();
	protected WorkflowBundle originalBundle;


	@Before
	public void makeBundle() {
		originalBundle = exampleWorkflow.makeWorkflowBundle();
	}

	@Test
	public void singleFile() throws Exception {
		UCFPackage resources = originalBundle.getResources();
		resources.addResource("Hello there", "hello.txt", "text/plain");
		File bundleFile = tempFile();
		bundleIO.writeBundle(originalBundle, bundleFile, APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
		
		ZipFile zipFile = new ZipFile(bundleFile);
		ZipEntry hello = zipFile.getEntry("hello.txt");
		assertEquals("hello.txt", hello.getName());
		assertEquals("Hello there",
				IOUtils.toString(zipFile.getInputStream(hello), "ASCII"));
		
	}
	
	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("test", ".scufl2");
		bundleFile.deleteOnExit();
		//System.out.println(bundleFile);
		return bundleFile;
	}
}
