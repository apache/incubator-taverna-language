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

import java.io.FileOutputStream;
import java.net.URI;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Ignore;
import org.junit.Test;


public class TestAPICreation {

	private WorkflowBundle ro;

	@Test
	public void makeExampleWorkflow() throws Exception {

		ro = new WorkflowBundle();
		Workflow wf1 = new Workflow();
		ro.setWorkflows(Collections.singleton(wf1));
		ro.setMainWorkflow(wf1);

		assertEquals("Non-empty input ports", Collections.EMPTY_SET, wf1.getInputPorts());

		InputWorkflowPort i = new InputWorkflowPort(wf1, "I");
		assertEquals("Did not add input port 'I'", Collections.singleton(i), wf1
				.getInputPorts());

		OutputWorkflowPort wf1_out1 = new OutputWorkflowPort(wf1, "out1");
		assertTrue("Did not add 'out1' to list of output ports", wf1
				.getOutputPorts().contains(wf1_out1));

		new OutputWorkflowPort(wf1, "out1");

		assertTrue("Unexpected processors", wf1.getProcessors().isEmpty());
		Processor p1 = new Processor(wf1, "p1");
		assertTrue("Did not add processor", wf1.getProcessors().contains(p1));

		InputProcessorPort p1_y1 = new InputProcessorPort(p1, "Y1");
		OutputProcessorPort p1_y2 = new OutputProcessorPort(p1, "Y2");
		p1.getOutputPorts().add(p1_y2);

		Processor p4 = new Processor(wf1, "p4");
		wf1.getProcessors().add(p4);
		InputProcessorPort p4_x2 = new InputProcessorPort(p4, "X2");
		p4.getInputPorts().add(p4_x2);
		p4.getInputPorts().add(new InputProcessorPort(p4, "Y1"));
		OutputProcessorPort p4_y = new OutputProcessorPort(p4, "Y");
		p4.getOutputPorts().add(p4_y);

		Processor pNested = new Processor(wf1, "PNested");
		wf1.getProcessors().add(pNested);

		InputProcessorPort pNested_i = new InputProcessorPort(pNested, "I");
		pNested.getInputPorts().add(pNested_i);
		OutputProcessorPort pNested_o = new OutputProcessorPort(pNested, "O");
		pNested.getOutputPorts().add(pNested_o);

		wf1.getDataLinks().add(new DataLink(wf1, p1_y2, pNested_i));

		wf1.getDataLinks().add(new DataLink(wf1, p1_y2, p4_x2));

		wf1.getDataLinks().add(new DataLink(wf1, pNested_o, p1_y1));

		wf1.getDataLinks().add(new DataLink(wf1, p4_y, wf1_out1));

		Activity activity = new Activity("act0");
		Profile p = new Profile();
		ro.getProfiles().add(p);
		p.getActivities().add(activity);

		activity.setType(URI
				.create("http://taverna.sf.net/2009/2.1/activity/beanshell#wrongURI"));
	}

	@Ignore("Not doing XML here anymore")
	@Test
	public void marshal() throws Exception {
		makeExampleWorkflow();
		JAXBContext jc = JAXBContext.newInstance(WorkflowBundle.class );
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
				Boolean.TRUE );
		marshaller.marshal( ro, new FileOutputStream("target/foo.xml") );
	}
}
