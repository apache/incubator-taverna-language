package org.apache.taverna.scufl2.api.core;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class PortOrderTest {
	List<Port> ports = new ArrayList<Port>();
	
	Workflow wf = new Workflow();

	@Test
	public void orderedByName() throws Exception {
		ports.add(new InputWorkflowPort(wf, "p3"));
		ports.add(new InputWorkflowPort(wf, "p1"));
		ports.add(new InputWorkflowPort(wf, "p2"));
		Collections.sort(ports);
		assertNamesEqual(ports, "p1", "p2", "p3");
	}
	
	@Test
	public void ignoringNull() throws Exception {
		ports.add(new InputWorkflowPort(null, "p3"));
		ports.add(new InputWorkflowPort(null, "p1"));
		ports.add(new InputWorkflowPort(wf, "p2"));
		Collections.sort(ports);
		assertNamesEqual(ports, "p1", "p2", "p3");
	}
	
	@Test
	public void orderedByClassName() throws Exception {
		ports.add(new InputWorkflowPort(wf, "p3"));
		ports.add(new OutputWorkflowPort(wf, "p1"));
		ports.add(new InputWorkflowPort(wf, "p2"));
		Collections.sort(ports);
		assertNamesEqual(ports, "p2", "p3", "p1");
	}


	public static void assertNamesEqual(List<? extends Named> named, String... expectedNames) {
		List<String> names = namesOf(named);	
		assertEquals(Arrays.asList(expectedNames), names);		
	}

	public static List<String> namesOf(List<? extends Named> named) {
		List<String> names = new ArrayList<String>();
		for (Named n : named) {
			names.add(n.getName());
		}
		return names;
	}

}
