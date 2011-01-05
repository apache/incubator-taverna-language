package uk.org.taverna.scufl2.api.io;

import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestSillyWriter extends ExampleWorkflow {
	@Test
	public void findSillyWriter() throws Exception {
		WorkflowBundleIO bundleIO = new WorkflowBundleIO();

		WorkflowBundle wfBundle = makeWorkflowBundle();

	}
}
