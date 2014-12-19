package uk.org.taverna.scufl2.api.io;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

public class TestResources {

	private WorkflowBundle wb;
	ExampleWorkflow exampleWorkflow = new ExampleWorkflow();

	@Test
	public void emptyResources() throws Exception {
		UCFPackage resources = wb.getResources();
		assertTrue(resources.listResources().isEmpty());
	}

	@Before
	public void makeBundle() {
		wb = exampleWorkflow.makeWorkflowBundle();
	}

	@Test
	public void singleFile() throws Exception {
		UCFPackage resources = wb.getResources();
		resources.addResource("Hello there", "hello.txt", "text/plain");
		assertTrue(resources.listResources().containsKey("hello.txt"));
	}
}
