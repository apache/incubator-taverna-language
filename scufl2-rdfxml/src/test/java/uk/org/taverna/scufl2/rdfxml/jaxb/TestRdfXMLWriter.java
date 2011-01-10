package uk.org.taverna.scufl2.rdfxml.jaxb;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.rdfxml.RdfXMLReader;
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

	}

	protected void verifyPackageStructure(UCFPackage ucfPackage) {
		assertEquals(
				RdfXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE,
				ucfPackage.getPackageMediaType());
		assertEquals(APPLICATION_RDF_XML,
				ucfPackage.getResourceEntry("workflowBundle.rdf")
						.getMediaType());

		assertEquals(APPLICATION_RDF_XML,
				ucfPackage.getResourceEntry("workflowBundle.rdf")
						.getMediaType());



	}

	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("test", "scufl2");
		bundleFile.deleteOnExit();
		return bundleFile;
	}

}
