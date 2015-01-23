package org.apache.taverna.scufl2.wfdesc;
/*
 *
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
 *
*/


import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.wfdesc.ROEvoSerializer;
import org.junit.Before;
import org.junit.Test;


public class TestRoEvoSerializer {
	private static final String HELLOWORLD_T2FLOW = "helloanyone.t2flow";
	
	ROEvoSerializer roEvo = new ROEvoSerializer();
	WorkflowBundleIO io = new WorkflowBundleIO();

	private WorkflowBundle helloWorld;
	
	@Before
	public void loadHello() throws ReaderException, IOException {
		InputStream helloStream = getClass().getResourceAsStream("/" + HELLOWORLD_T2FLOW);
		assertNotNull(helloStream);
		helloWorld = io.readBundle(helloStream, "application/vnd.taverna.t2flow+xml");
		assertNotNull(helloWorld);
	}
	
	
	@Test
	public void workflowUUIDs() throws Exception {		
		roEvo.workflowHistory(helloWorld.getMainWorkflow(), System.out);
		
	}
	
}
