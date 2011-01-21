package uk.org.taverna.scufl2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.property.PropertyObject;

public class VisitorTest {
	@Test
	public void visitAll() throws Exception {

		final List<WorkflowBean> enters = new ArrayList<WorkflowBean>();
		final List<WorkflowBean> leaves = new ArrayList<WorkflowBean>();
		final List<WorkflowBean> visits = new ArrayList<WorkflowBean>();

		WorkflowBundle example = new ExampleWorkflow().makeWorkflowBundle();

		example.accept(new Visitor() {
			@Override
			public boolean visit(WorkflowBean node) {
				visits.add(node);
				if (node instanceof Child) {
					Child child = (Child) node;
					assertTrue(enters.contains(child.getParent()));
				}
				return true;
			}

			@Override
			public boolean visitEnter(WorkflowBean node) {
				if (enters.contains(node)) {
					fail("Duplicate enter on " + node);
				}
				if (leaves.contains(node)) {
					fail("Leave before enter on " + node);
				}

				enters.add(node);
				return true;
			}

			@Override
			public boolean visitLeave(WorkflowBean node) {
				leaves.add(node);
				assertTrue(enters.contains(node));
				return true;
			}
		});
		assertEquals(enters.size(), leaves.size());
		HashSet entersSet = new HashSet(enters);
		HashSet leavesSet = new HashSet(leaves);
		assertEquals(entersSet, leavesSet);
		assertEquals(enters.size(), entersSet.size());

		for (WorkflowBean b : visits) {
			if (b instanceof Child) {
				@SuppressWarnings("rawtypes")
				Child child = (Child) b;
				WorkflowBean parent = child.getParent();
				assertTrue("", enters.contains(parent));
			} else {
				assertTrue("Bean is not a PropertyObject or Child: " + b,
						b instanceof PropertyObject);
			}
		}

	}
}
