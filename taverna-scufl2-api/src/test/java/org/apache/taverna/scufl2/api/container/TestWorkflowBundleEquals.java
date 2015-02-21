package org.apache.taverna.scufl2.api.container;

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

import java.net.URI;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.junit.Test;


public class TestWorkflowBundleEquals {
	
	@Test
	public void notEquals() throws Exception {
		WorkflowBundle wb1 = new WorkflowBundle();
		
		WorkflowBundle wb2 = new WorkflowBundle();
		// Make them look "equal"
		wb1.setName("bob");
		wb2.setName("bob");
		wb1.setGlobalBaseURI(URI.create("http://example.com/bob"));
		wb2.setGlobalBaseURI(URI.create("http://example.com/bob"));		
		assertFalse(wb1.equals(wb2));
	}
	
	@Test
	public void equals() throws Exception {
		WorkflowBundle wb1 = new WorkflowBundle();	
		assertTrue(wb1.equals(wb1));		
	}
	
	@Test 
	public void workflowsNotEqualsUnlessOrphans() {
		Workflow wf1 = new Workflow();
		Workflow wf2 = new Workflow();
		wf1.setName("fred");
		wf2.setName("fred");
		// No parents, so they are equal
		assertEquals(wf1, wf2);
		
		
		WorkflowBundle wb1 = new WorkflowBundle();
		
		WorkflowBundle wb2 = new WorkflowBundle();
		// Make them look "equal"
		wb2.setName(wb1.getName());
		wb2.setGlobalBaseURI(wb1.getGlobalBaseURI());
		assertFalse(wb1.equals(wb2));
		
		wf1.setParent(wb1);
		wf2.setParent(wb2);		
		assertFalse(wf1.equals(wf2));
		
		wf1.setParent(null);
		assertFalse(wf1.equals(wf2));
		assertFalse(wf2.equals(wf1));
		wf2.setParent(null);
		assertTrue(wf1.equals(wf2));	
	}
	
}
