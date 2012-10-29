package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;

public class TestAbstractCloneable {
	private WorkflowBundle wfBundle;

	@Before
	public void makeExampleWorkflow() {
		wfBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}

	@Test
	public void megaClone() throws Exception {
		AbstractCloneable clone = wfBundle.cloned();
	}
}
