package uk.org.taverna.scufl2.api.container;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;

import uk.org.taverna.scufl2.api.core.Workflow;

public class TestWorkflowBundleEquals {
	
	@Test
	public void notEquals() throws Exception {
		WorkflowBundle wb1 = new WorkflowBundle();
		
		WorkflowBundle wb2 = new WorkflowBundle();
		// Make them look "equal"
		wb1.setName("bob");
		wb2.setName("bob");
		wb1.setGlobalBaseURI(URI.create("http://example.com/bob"));
		wb2.setGlobalBaseURI(URI.create("http://example.com/bob"));		
		assertFalse(wb1.equals(wb2));
	}
	
	@Test
	public void equals() throws Exception {
		WorkflowBundle wb1 = new WorkflowBundle();	
		assertTrue(wb1.equals(wb1));		
	}
	
	@Test 
	public void workflowsNotEqualsUnlessOrphans() {
		Workflow wf1 = new Workflow();
		Workflow wf2 = new Workflow();
		wf1.setName("fred");
		wf2.setName("fred");
		// No parents, so they are equal
		assertEquals(wf1, wf2);
		
		
		WorkflowBundle wb1 = new WorkflowBundle();
		
		WorkflowBundle wb2 = new WorkflowBundle();
		// Make them look "equal"
		wb2.setName(wb1.getName());
		wb2.setGlobalBaseURI(wb1.getGlobalBaseURI());
		assertFalse(wb1.equals(wb2));
		
		wf1.setParent(wb1);
		wf2.setParent(wb2);		
		assertFalse(wf1.equals(wf2));
		
		wf1.setParent(null);
		assertFalse(wf1.equals(wf2));
		assertFalse(wf2.equals(wf1));
		wf2.setParent(null);
		assertTrue(wf1.equals(wf2));	
	}
	
}
