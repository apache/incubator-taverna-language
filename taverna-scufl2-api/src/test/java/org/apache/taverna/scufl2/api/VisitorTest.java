package org.apache.taverna.scufl2.api;

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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.junit.Test;


public class VisitorTest {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void visitAll() throws Exception {

		final List<WorkflowBean> enters = new ArrayList<WorkflowBean>();
		final List<WorkflowBean> leaves = new ArrayList<WorkflowBean>();
		final List<WorkflowBean> visits = new ArrayList<WorkflowBean>();
		final Stack<WorkflowBean> stack = new Stack<WorkflowBean>();

		WorkflowBundle example = new ExampleWorkflow().makeWorkflowBundle();

		example.accept(new Visitor() {

			@Override
			public boolean visit(WorkflowBean node) {
				visits.add(node);
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
				stack.add(node);

				enters.add(node);
				return true;
			}

			@Override
			public boolean visitLeave(WorkflowBean node) {
				leaves.add(node);
				assertEquals(stack.pop(), node);
				assertTrue(enters.contains(node));
				return true;
			}
		});
		assertTrue(stack.isEmpty());
		assertEquals(enters.size(), leaves.size());

		HashSet entersSet = new HashSet(enters);
		HashSet leavesSet = new HashSet(leaves);
		assertEquals(entersSet, leavesSet);
		assertEquals(enters.size(), entersSet.size());

		for (WorkflowBean b : visits) {
			if (b instanceof Child) {
				Child child = (Child) b;
				WorkflowBean parent = child.getParent();
				assertTrue(enters.contains(parent));
			} else {
				fail("Bean is not a Child");
			}
		}

	}
}
