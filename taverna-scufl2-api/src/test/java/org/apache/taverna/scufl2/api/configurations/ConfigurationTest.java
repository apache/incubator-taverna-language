package org.apache.taverna.scufl2.api.configurations;

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

import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.DataLinkCompareTest;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Before;
import org.junit.Test;


public class ConfigurationTest extends ExampleWorkflow {
	
	@Before
	public void makeBundle() {
		makeWorkflowBundle();
	}
	
	/**
	 * Similar bug to {@link DataLinkCompareTest#dataLinkNotAddedTwice()}
	 */
	@Test
	public void configurationNotAddedTwice() throws Exception {
		Configuration c1a = new Configuration("c1");
		Profile p1 = new Profile("p1");
		p1.getConfigurations().add(c1a);		
		c1a.setParent(p1);
		p1.getConfigurations().add(c1a);
		
		
		Configuration c1b = new Configuration("c1");
		Profile p2 = new Profile("p2");
		p2.getConfigurations().add(c1b);		
		c1b.setParent(p2);
		p2.getConfigurations().add(c1b);
		
		
		WorkflowBundle bundle = new WorkflowBundle();
		p1.setParent(bundle);
		p2.setParent(bundle);
		new Scufl2Tools().setParents(bundle);
		assertEquals(1, p1.getConfigurations().size());
		assertEquals(1, p2.getConfigurations().size());
		
	}
	
	@Test
	public void configurationNotAddedTwiceExample() throws Exception {
		Profile p = workflowBundle.getMainProfile();
		assertEquals(1, p.getConfigurations().size());
		new Scufl2Tools().setParents(workflowBundle);
		assertEquals(1, p.getConfigurations().size());
	}
	
}
