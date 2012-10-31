package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		// All original beans should be identical
		System.out.println(originalBeans.size());
		System.out.println(stillOriginalBeans.size());
		assertEquals(originalBeans.size(), findCommonById(originalBeans, stillOriginalBeans).size());
		
		
		List<WorkflowBean> clonedBeans = AllBeansVisitor.allBeansFrom(clone);
		List<WorkflowBean> common = findCommonById(originalBeans, clonedBeans);
		assertTrue(common.isEmpty());
//		
		// Check parents
		for (WorkflowBean b : originalBeans) {
			if (b instanceof Child) {
				@SuppressWarnings("rawtypes")
				Child child = (Child) b;
				if (child.getParent() == null) {
					System.err.println("No parent? " + child);
					continue;
				}
				if (! originalBeans.contains(child.getParent())) {
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
				System.err.println("Missing " + a);
				continue;
			}
			T b = listB.get(bIndex);
			if (a == b) {
				common.add(a);
			} else {
				System.err.println("Non-identical equals " + a + " " + b);
			}
		}
		return common;		
	}
}
