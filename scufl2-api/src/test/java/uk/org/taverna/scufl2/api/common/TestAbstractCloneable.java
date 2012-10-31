package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;

public class TestAbstractCloneable {
	private WorkflowBundle originalWfBundle;
	private List<WorkflowBean> originalBeans;

	@Before
	public void makeExampleWorkflow() {
		originalWfBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
		originalBeans = AllBeansVisitor.allBeansFrom(originalWfBundle);		
	}

	@Test
	public void megaClone() throws Exception {
		AbstractCloneable clone = originalWfBundle.clone();
//		AbstractCloneable clone = originalWfBundle;
		
		
		List<WorkflowBean> stillOriginalBeans = AllBeansVisitor.allBeansFrom(originalWfBundle);
		System.out.println(stillOriginalBeans);
		assertEquals(originalBeans.size(), stillOriginalBeans.size());
		// All original beans should be identical
		assertEquals(originalBeans.size(), findCommonById(originalBeans, stillOriginalBeans).size());
		
		
		List<WorkflowBean> clonedBeans = AllBeansVisitor.allBeansFrom(clone);
		List<WorkflowBean> common = findCommonById(originalBeans, clonedBeans);
		assertTrue("Found some common beans: " + common, common.isEmpty());
//		
		// Check parents are present
		checkParents(originalBeans);
		checkParents(stillOriginalBeans);
		checkParents(clonedBeans);
	}

	private void checkParents(List<WorkflowBean> beans) {
		for (WorkflowBean b : beans) {
			if (b instanceof Child) {
				@SuppressWarnings("rawtypes")
				Child child = (Child) b;
				if (child.getParent() == null) {
					System.err.println("No parent? " + child);
					continue;
				}
				if (! beans.contains(child.getParent())) {
					fail("Unknown parent for " + child + " " + child.getParent());
				}
			}
		}
	}
	
	private <T> List<T> findCommonById(
			List<T> listA, List<T> listB) {
		List<T> common = new ArrayList<T>();
		for (T a : listA) {
			int bIndex = listB.indexOf(a);
			if (bIndex < 0) {
//				System.err.println("Missing " + a);
				continue;
			}
			T b = listB.get(bIndex);
			if (a == b) {
				common.add(a);
			} else {
//				System.err.println("Non-identical equals " + a + " " + b);
			}
		}
		return common;		
	}
}
