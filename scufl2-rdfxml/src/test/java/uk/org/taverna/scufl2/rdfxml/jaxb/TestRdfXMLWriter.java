package uk.org.taverna.scufl2.rdfxml.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.ParseException;

import javax.management.openmbean.InvalidOpenTypeException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.rdfxml.RdfXMLReader;
import uk.org.taverna.scufl2.rdfxml.RdfXMLWriter;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class TestRdfXMLWriter {

	private static final String APPLICATION_RDF_XML = "application/rdf+xml";
	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	protected WorkflowBundle workflowBundle;
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();

	@Before
	public void makeExampleWorkflow() {
		workflowBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}

	@Test
	public void writeBundleToFile() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(workflowBundle, bundleFile,
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
		UCFPackage ucfPackage = new UCFPackage(bundleFile);
		verifyPackageStructure(ucfPackage);
		// TODO: Check RDF/XML using xpath
	}

	@Test
	public void validFileName() {
		assertEquals("f%2fsd%5Csdf'asd%20fa%3asd%20%C4%91%C3%BE%E2%80%9D%C2%BB%C3%A6", 
				new RdfXMLWriter().validFilename("f/sd\\sdf'asd fa:sd đþ”»æ"));
	}
	
	@Test
	public void writeBundleToStream() throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		bundleIO.writeBundle(workflowBundle, outStream,
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
		outStream.close();

		InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		UCFPackage ucfPackage;
		try {
			// Avoid UCFPackage from creating a temporary file
			System.setProperty("org.odftoolkit.odfdom.tmpfile.disable", "true");
			ucfPackage = new UCFPackage(inStream);
		} finally {
			System.clearProperty("org.odftoolkit.odfdom.tmpfile.disable");
		}
		verifyPackageStructure(ucfPackage);

	}

	protected void verifyPackageStructure(UCFPackage ucfPackage) {
		assertEquals(
				RdfXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE,
				ucfPackage.getPackageMediaType());
		assertEquals(APPLICATION_RDF_XML,
				ucfPackage.getResourceEntry("workflowBundle.rdf")
						.getMediaType());

		assertEquals(APPLICATION_RDF_XML,
				ucfPackage.getResourceEntry("workflow/HelloWorld.rdf")
						.getMediaType());

		assertEquals(APPLICATION_RDF_XML,
				ucfPackage.getResourceEntry("profile/tavernaServer.rdf")
						.getMediaType());
		assertEquals(APPLICATION_RDF_XML,
				ucfPackage.getResourceEntry("profile/tavernaWorkbench.rdf")
						.getMediaType());
	}

	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("test", ".scufl2");
		bundleFile.deleteOnExit();
		return bundleFile;
	}

}
