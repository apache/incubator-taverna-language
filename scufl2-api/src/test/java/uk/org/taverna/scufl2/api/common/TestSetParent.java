package uk.org.taverna.scufl2.api.common;

import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestSetParent {
	WorkflowBundle example = new ExampleWorkflow().makeWorkflowBundle();

	Scufl2Tools tools = new Scufl2Tools();

	@Test
	public void checkParents() throws Exception {
		example.accept(new VisitorWithPath() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean visit() {
				WorkflowBean node = getCurrentNode();
				if (node instanceof Child) {
					Child child = (Child) node;
					WorkflowBean parent = child.getParent();
					WorkflowBean expectedParent = getCurrentPath().peek();
					if (!(parent == expectedParent)) {
						throw new IllegalStateException("Wrong parent for "
								+ node + ": " + parent + ", expected: "
								+ expectedParent);
					}
				}
				return true;
			}

		});
	}

}
