package org.apache.taverna.scufl2.integration;
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


import java.io.File;
import java.net.URI;

import org.junit.Test;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestSimpleWf {
	private static final WorkflowBundleIO bundleIo = new WorkflowBundleIO();
	private static final Scufl2Tools scufl2Tools = new Scufl2Tools();
	private static final String bundleType = "application/vnd.taverna.scufl2.workflow-bundle";
	public static URI BEANSHELL = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	
	@Test
	public void testName() throws Exception {
		// Workflow
		WorkflowBundle wb = new WorkflowBundleIO().createBundle();
		
		Workflow wf = wb.getMainWorkflow();
		wf.setName("test_wf");
		InputWorkflowPort raw = new InputWorkflowPort(wf, "RAW");
		OutputWorkflowPort msconvert_log = new OutputWorkflowPort(wf, "MSCONVERT_LOG");
		OutputWorkflowPort cmd = new OutputWorkflowPort(wf, "cmd");
		
		// processor
		Processor msconvert = new Processor(wf, "MSCONVERT");
		InputProcessorPort ms_raw = new InputProcessorPort(msconvert, "raw");
		OutputProcessorPort ms_out = new OutputProcessorPort(msconvert, "out");
		OutputProcessorPort ms_cmd = new OutputProcessorPort(msconvert, "cmd");
		
		// links
		new DataLink(wf, raw, ms_raw);
		new DataLink(wf, ms_out, msconvert_log);
		new DataLink(wf, ms_cmd, cmd);
		
		// Beanshell script
		Activity script = new Activity("msconvert");
		script.setType(BEANSHELL);

		Profile profile = wb.getMainProfile();
		script.setParent(profile);
		profile.getActivities().add(script);
		
		scufl2Tools.createActivityPortsFromProcessor(script, msconvert);
		scufl2Tools.bindActivityToProcessorByMatchingPorts(script, msconvert);
		
		Configuration config = new Configuration();
		config.setConfigures(script);
		config.setType(BEANSHELL.resolve("#Config"));
		((ObjectNode)config.getJson()).put("script", 
				"blablalbal");
		profile.getConfigurations().add(config);
		
		// Save to file (or System.out ? )
		File file = File.createTempFile("test", ".wfbundle");
		bundleIo.writeBundle(wb, file, bundleType);
		System.out.println(file);
	}

}
