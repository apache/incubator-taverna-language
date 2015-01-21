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


import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.junit.Test;


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
