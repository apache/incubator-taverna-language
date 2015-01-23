package org.apache.taverna.scufl2.translator.t2flow;
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

import java.io.File;
import java.net.URL;

import org.junit.Test;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

public class PropertyListRoundtripIT {

	/* From http://dev.mygrid.org.uk/issues/browse/SCUFL2-120 */

	private static final String WF_APICONSUMER = "/apiconsumer.t2flow";
	private static WorkflowBundleIO workflowBundleIO = new WorkflowBundleIO();
	
	
	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WF_APICONSUMER);
		assertNotNull("Could not find workflow " + WF_APICONSUMER,
				wfResource);

		File bundleFile = File.createTempFile("test", "wfbundle");		
		
		WorkflowBundle wfBundle = workflowBundleIO.readBundle(wfResource, null);		
		workflowBundleIO.writeBundle(wfBundle, bundleFile, "application/vnd.taverna.scufl2.workflow-bundle");
		wfBundle = workflowBundleIO.readBundle(bundleFile, null);
	}
	
}
