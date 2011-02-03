package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;

public class TestURITools {

	private URITools uriTools = new URITools();
	private WorkflowBundle wfBundle;

	@Before
	public void makeExampleWorkflow() {
		wfBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}

	@Test
	public void relativizeNotSameFolder() {
		URI base = URI.create("workflow/Workflow.rdf");
		URI uri = URI.create("profile/Profile/");
		assertEquals("../profile/Profile/", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeNotSameFolderAbsolute() {
		URI base = URI.create("/workflow/Workflow.rdf");
		URI uri = URI.create("/profile/Profile/");
		assertEquals("../profile/Profile/", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeNotSameFolderComplete() {
		URI base = URI.create("http://example.com/workflow/Workflow.rdf");
		URI uri = URI.create("http://example.com/profile/Profile/");
		assertEquals("../profile/Profile/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSameFolder() {
		URI base = URI.create("workflow/Workflow.rdf");
		URI uri = URI.create("workflow/Folder/");
		assertEquals("Folder/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSameFolderAbsoulute() {
		URI base = URI.create("/workflow/Workflow.rdf");
		URI uri = URI.create("/workflow/Folder/");
		assertEquals("Folder/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSameFolderComplete() {
		URI base = URI.create("http://example.com/workflow/Workflow.rdf");
		URI uri = URI.create("http://example.com/workflow/Folder/");
		assertEquals("Folder/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSamePath() {
		URI base = URI.create("workflow/../workflow//Workflow.rdf#");
		URI uri = URI.create("workflow/Workflow.rdf#fish");
		assertEquals("#fish", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeSamePathAbsolute() {
		URI base = URI.create("/workflow/../workflow//Workflow.rdf#");
		URI uri = URI.create("/workflow/Workflow.rdf#fish");
		assertEquals("#fish", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeSamePathComplete() {
		URI base = URI
				.create("http://example.com/workflow/../workflow//Workflow.rdf#");
		URI uri = URI
.create("http://example.com/workflow/Workflow.rdf#fish");
		assertEquals("#fish", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relatizeSame() throws Exception {
		URI base = URI.create("workflow/HelloWorld.rdf");
		URI uri = URI.create("workflow/HelloWorld/");
		assertEquals("HelloWorld/", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relatizeToFragment() throws Exception {
		URI base = URI.create("filename.txt");
		URI fragment = URI.create("./#sd");
		assertEquals(".#sd", uriTools.relativePath(base, fragment)
				.toASCIIString());
	}

	@Test
	public void validFileName() {
		assertEquals(
				"f%2fsd%5Csdf'asd%20fa%3as&%3F%3dd%20%C4%91%C3%BE%E2%80%9D%C2%BB%C3%A6",
				uriTools.validFilename("f/sd\\sdf'asd fa:s&?=d đþ”»æ"));
	}

	@Test
	public void wfBundle() {
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/",
				wfBundle.getSameBaseAs().toASCIIString());
		assertEquals(wfBundle.getSameBaseAs(), uriTools.uriForBean(wfBundle));
		String uuidPath = uriTools.relativePath(
				WorkflowBundle.WORKFLOW_BUNDLE_ROOT, wfBundle.getSameBaseAs())
				.getPath();
		assertTrue(uuidPath.endsWith("/"));
		// Should be a valid uuid
		UUID.fromString(uuidPath.substring(0, uuidPath.length() - 1));
	}

	@Test
	public void wfBundleSelfRelative() {
		assertEquals(URI.create(""),
				uriTools.relativeUriForBean(wfBundle, wfBundle));
	}

	@Test
	public void workflow() {
		Workflow wf = wfBundle.getMainWorkflow();
		URI wfUri = uriTools.uriForBean(wf);
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/",
				wfUri.toASCIIString());
		URI namePath = uriTools.relativeUriForBean(wf, wfBundle);
		assertEquals("workflow/HelloWorld/", namePath.toASCIIString()); // wf.getName();
	}

	@Test
	public void workflowIdentifier() {
		Workflow wf = wfBundle.getMainWorkflow();
		String uuidPath = uriTools.relativePath(Workflow.WORKFLOW_ROOT,
				wf.getWorkflowIdentifier()).getPath();
		assertTrue(uuidPath.endsWith("/"));
		// Should be a valid uuid
		UUID.fromString(uuidPath.substring(0, uuidPath.length() - 1));
	}

}
