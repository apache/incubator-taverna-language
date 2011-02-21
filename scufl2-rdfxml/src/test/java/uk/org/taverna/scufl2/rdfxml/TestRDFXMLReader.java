package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.org.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class TestRDFXMLReader {
	
	private URL exampleBundle;
	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	protected WorkflowBundle workflowBundle;
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO testWorkflowBundleIO = new uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO();
	
	@Before
	public void exampleBundle() throws ReaderException, IOException {
		String name = "example.scufl2";
		exampleBundle = getClass().getResource(name);
		assertNotNull("Can't find example workflow bundle " + name, exampleBundle);
		workflowBundle = bundleIO.readBundle(exampleBundle, APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}
	
	@Test
	public void testParsedWorkflow() throws Exception {
		assertEquals("HelloWorld", workflowBundle.getName());
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(workflowBundle, output, TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		String bundleTxt = new String(output.toByteArray(), "UTF-8");
		
		assertEquals(testWorkflowBundleIO.getStructureFormatWorkflowBundle(), bundleTxt);
		
	}
	
}
