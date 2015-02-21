package org.apache.taverna.scufl2.api.common;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.taverna.scufl2.api.common.AbstractCloneable;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.TestWorkflowBundleIO;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Before;
import org.junit.Test;


public class TestAbstractCloneable {
	private WorkflowBundle originalWfBundle;
	private List<WorkflowBean> originalBeans;

	@Before
	public void makeExampleWorkflow() {
		originalWfBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
		originalBeans = AllBeansVisitor.allBeansFrom(originalWfBundle);
	}

	@Test
	public void cloneBundle() throws Exception {
		AbstractCloneable clone = originalWfBundle.clone();
		// AbstractCloneable clone = originalWfBundle;

		List<WorkflowBean> stillOriginalBeans = AllBeansVisitor
				.allBeansFrom(originalWfBundle);
		System.out.println(stillOriginalBeans);
		assertEquals(originalBeans.size(), stillOriginalBeans.size());
		// All original beans should be identical
		assertEquals(originalBeans.size(),
				findCommonById(originalBeans, stillOriginalBeans).size());

		List<WorkflowBean> clonedBeans = AllBeansVisitor.allBeansFrom(clone);
		List<WorkflowBean> common = findCommonById(originalBeans, clonedBeans);
		assertTrue("Found some common beans: " + common, common.isEmpty());
		//
		// Check parents are present
		checkParents(originalBeans);
		checkParents(stillOriginalBeans);
		checkParents(clonedBeans);
	}

	@Test
	public void cloneWorkflow() throws Exception {
		Workflow original = originalWfBundle.getMainWorkflow();
		assertEquals(originalWfBundle, original.getParent());
		Workflow clone = (Workflow) original.clone();
		assertNull(clone.getParent());
		assertEquals(original.getName(), clone.getName());
		assertNotSame(original.getProcessors().getByName("Hello"), clone
				.getProcessors().getByName("Hello"));
		assertNotSame(original.getCurrentRevision(), clone.getCurrentRevision());
		assertEquals(original.getCurrentRevision(), clone.getCurrentRevision());

	}

	@Test
	public void cloneProfile() throws Exception {
		Profile original = originalWfBundle.getMainProfile();
		assertEquals(originalWfBundle, original.getParent());
		Profile clone = (Profile) original.clone();
		assertNull(clone.getParent());
		assertEquals(original.getName(), clone.getName());

		ProcessorBinding originalBinding = original.getProcessorBindings()
				.getByName("Hello");
		ProcessorBinding cloneBinding = clone.getProcessorBindings().getByName(
				"Hello");
		assertNotSame(originalBinding, cloneBinding);
		assertNotSame(originalBinding.getBoundActivity(),
				cloneBinding.getBoundActivity());
		// but processor is the same, as we did not clone the workflow
		assertSame(originalBinding.getBoundProcessor(),
				cloneBinding.getBoundProcessor());
	}

	@Test
	public void cloneProcessor() throws Exception {
		Workflow wf = originalWfBundle.getMainWorkflow();
		Processor original = wf.getProcessors().getByName("Hello");
		Processor clone = (Processor) original.clone();
		assertEquals(clone.getName(), original.getName());
		assertNotNull(original.getParent());
		assertNull(clone.getParent());
		
		wf.getProcessors().addWithUniqueName(clone);
		assertTrue(!clone.getName().equals(original.getName()));
		// Now it is safe to set the parent without loosing original
		clone.setParent(wf);
		
		assertSame(original, wf.getProcessors().getByName("Hello"));
		assertSame(clone, wf.getProcessors().getByName(clone.getName()));
	}

	@Test
	public void nullParentNotCopied() throws Exception {
		Workflow wf = new Workflow();
		Processor orphan = new Processor();
		orphan.setName("orphan");
		// NOTE: NOT calling
		// orphan.setParent(wf)
		wf.getProcessors().add(orphan);

		assertNull(orphan.getParent());

		Workflow clone = (Workflow) wf.clone();
		assertTrue(clone.getProcessors().isEmpty());

		orphan.setParent(wf);

		Workflow clone2 = (Workflow) wf.clone();
		assertEquals(Collections.singleton("orphan"), clone2.getProcessors()
				.getNames());

	}

	public static void checkParents(List<WorkflowBean> beans) {
		for (WorkflowBean b : beans) {
			if (b instanceof Child) {
				@SuppressWarnings("rawtypes")
				Child child = (Child) b;
				if (child.getParent() == null) {
					System.err.println("No parent? " + child);
					continue;
				}
				if (!beans.contains(child.getParent())) {
					fail("Unknown parent for " + child + " "
							+ child.getParent());
				}
			}
		}
	}

	public static <T> List<T> findCommonById(List<T> listA, List<T> listB) {
		List<T> common = new ArrayList<T>();
		for (T a : listA) {
			int bIndex = listB.indexOf(a);
			if (bIndex < 0) {
				// System.err.println("Missing " + a);
				continue;
			}
			T b = listB.get(bIndex);
			if (a == b) {
				common.add(a);
			} else {
				// System.err.println("Non-identical equals " + a + " " + b);
			}
		}
		return common;
	}
}
