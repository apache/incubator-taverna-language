package org.apache.taverna.scufl2.api.io;

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


import static org.junit.Assert.assertTrue;

import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage;
import org.junit.Before;
import org.junit.Test;


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
