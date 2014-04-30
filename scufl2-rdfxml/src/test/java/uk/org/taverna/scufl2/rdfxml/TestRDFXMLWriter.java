package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

import com.fasterxml.jackson.databind.JsonNode;

public class TestRDFXMLWriter {

	private static final String APPLICATION_RDF_XML = "application/rdf+xml";
	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	protected WorkflowBundle workflowBundle;
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();

	@Before
	public void makeExampleWorkflow() {
		workflowBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}
	
	@Test
    public void awkwardFilenames() throws Exception {
	    workflowBundle.getProfiles().removeByName("tavernaServer");
	    String funnyName = "Funny_%2f_characters_50%_of the time";
        workflowBundle.getMainProfile().setName(funnyName);        
        workflowBundle.getMainWorkflow().setName(funnyName);
        File bundleFile = tempFile();
        bundleIO.writeBundle(workflowBundle, bundleFile,
                APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
        UCFPackage ucfPackage = new UCFPackage(bundleFile);
        Map<String, ResourceEntry> profiles = ucfPackage.listResources("profile");
        assertEquals(2, profiles.size());
        assertTrue(profiles.keySet().contains("Funny_%252f_characters_50%25_of%20the%20time.rdf"));
        assertTrue(profiles.keySet().contains("Funny_%252f_characters_50%25_of%20the%20time/"));
        
        Map<String, ResourceEntry> workflows = ucfPackage.listResources("workflow");
        assertEquals(1, workflows.size());
        assertEquals("Funny_%252f_characters_50%25_of%20the%20time.rdf", workflows.keySet().iterator().next());
        
        // and.. can we read it in again correctly?
        WorkflowBundle readBundle = bundleIO.readBundle(bundleFile, APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
        assertEquals(funnyName, readBundle.getMainProfile().getName());
        assertEquals(funnyName, readBundle.getMainWorkflow().getName());
        // did the JSON parse back in?
        JsonNode oldJson = workflowBundle.getMainProfile().getConfigurations().getByName("Hello").getJson();
        assertTrue(oldJson.get("script").asText().startsWith("hello"));           
        JsonNode newJson = readBundle.getMainProfile().getConfigurations().getByName("Hello").getJson();
        assertTrue(newJson.get("script").asText().startsWith("hello"));        
        assertEquals(oldJson, newJson);
    }

	@Test
	public void writeBundleToFile() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(workflowBundle, bundleFile,
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
		UCFPackage ucfPackage = new UCFPackage(bundleFile);
		verifyRootFile(ucfPackage);
		verifyPackageStructure(ucfPackage);
		// TODO: Check RDF/XML using xpath
	}

    private void verifyRootFile(UCFPackage ucfPackage) {
        List<ResourceEntry> rootFiles = ucfPackage.getRootFiles();
        assertEquals(1, rootFiles.size());
		assertEquals("workflowBundle.rdf", rootFiles.get(0).getPath());
		assertEquals("application/rdf+xml", rootFiles.get(0).getMediaType());
		assertEquals("0.4.0", rootFiles.get(0).getVersion());
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
				RDFXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE,
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
//		bundleFile.deleteOnExit();
		System.out.println(bundleFile);
		return bundleFile;
	}

}
