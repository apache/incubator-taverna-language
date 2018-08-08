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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;
import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Before;
import org.junit.Test;


public class TestScufl2Tools extends ExampleWorkflow {

	private Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Before
	public void makeBundle() {
		makeWorkflowBundle();
		assertNotNull(workflowBundle);
	}

	@Test
	public void testNestedWorkflows() {
		Workflow child = new Workflow();
		child.setName("childWorkflow");
		child.setParent(workflowBundle);

		Workflow mainWorkflow = workflowBundle.getMainWorkflow();
		Processor processor = new Processor();
		processor.setParent(mainWorkflow);

		Profile profile = workflowBundle.getMainProfile();

		Scufl2Tools tools = new Scufl2Tools();
		tools.setAsNestedWorkflow(processor, child, profile);
		Workflow nested = tools.nestedWorkflowForProcessor(processor, profile);

		assertEquals(child, nested);

		ProcessorBinding binding = processor.getBinding(profile);
		Activity activity = binding.getBoundActivity();
		Configuration configuration = activity.getConfiguration();

		assertEquals(activity.getType(), Scufl2Tools.NESTED_WORKFLOW);
		String nestedWorkflowName = configuration.getJson().get("nestedWorkflow").asText();
		assertEquals(nestedWorkflowName, child.getName());
	}

	@Test
	public void testNestedProcessHasCorrectStructure() {
		Workflow child = new Workflow();
		child.setName("childWorkflow");
		child.setParent(workflowBundle);

		Workflow mainWorkflow = workflowBundle.getMainWorkflow();
		Processor processor = new Processor();
		processor.setParent(mainWorkflow);

		Profile profile = workflowBundle.getMainProfile();

		Scufl2Tools tools = new Scufl2Tools();
		tools.setAsNestedWorkflow(processor, child, profile);

		Set<String> processorInputNames = processor.getInputPorts().stream().map(InputProcessorPort::getName).collect(Collectors.toSet());
		Set<String> workflowInputNames = child.getInputPorts().stream().map(InputWorkflowPort::getName).collect(Collectors.toSet());

		assertEquals(workflowInputNames, processorInputNames);

		Set<String> processorOutputNames = processor.getOutputPorts().stream().map(OutputProcessorPort::getName).collect(Collectors.toSet());
		Set<String> workflowOutputNames = child.getOutputPorts().stream().map(OutputWorkflowPort::getName).collect(Collectors.toSet());

		assertEquals(workflowOutputNames, processorOutputNames);
	}
	
	@Test
	public void controlLinksBlocking() {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		ControlLink controlLink = workflowBundle.getMainWorkflow().getControlLinks()
				.iterator().next();

		assertEquals(Collections.singletonList(controlLink),
				scufl2Tools.controlLinksBlocking(hello));
		assertTrue(scufl2Tools.controlLinksBlocking(wait4me).isEmpty());
	}

	@Test
	public void controlLinksWaitingFor() {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		ControlLink controlLink = workflowBundle.getMainWorkflow().getControlLinks()
				.iterator().next();

		assertEquals(Collections.singletonList(controlLink),
				scufl2Tools.controlLinksWaitingFor(wait4me));
		assertTrue(scufl2Tools.controlLinksWaitingFor(hello).isEmpty());
	}

	@Test
	public void processorPortBindingForInputActivityPort() throws Exception {
		Profile profile = workflowBundle.getMainProfile();
		Activity helloScript = profile.getActivities().getByName("HelloScript");
		InputActivityPort port = helloScript.getInputPorts().getByName(
				"personName");
		ProcessorBinding processorBinding = profile.getProcessorBindings()
				.getByName("Hello");
		ProcessorInputPortBinding inputPortBinding = processorBinding
				.getInputPortBindings().iterator().next();
		assertSame(inputPortBinding,
				scufl2Tools.processorPortBindingForPort(port, profile));
	}

	@Test
	public void processorPortBindingForInputProcessorPort() throws Exception {
		Profile profile = workflowBundle.getMainProfile();
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		InputProcessorPort port = hello.getInputPorts().getByName("name");
		ProcessorBinding processorBinding = profile.getProcessorBindings()
				.getByName("Hello");
		ProcessorInputPortBinding inputPortBinding = processorBinding
				.getInputPortBindings().iterator().next();
		assertSame(inputPortBinding,
				scufl2Tools.processorPortBindingForPort(port, profile));
	}

	@Test
	public void processorPortBindingForOutputActivityPort() throws Exception {
		Profile profile = workflowBundle.getMainProfile();
		Activity helloScript = profile.getActivities().getByName("HelloScript");
		OutputActivityPort port = helloScript.getOutputPorts().getByName(
				"hello");
		ProcessorBinding processorBinding = profile.getProcessorBindings()
				.getByName("Hello");
		ProcessorOutputPortBinding outputPortBinding = processorBinding
				.getOutputPortBindings().iterator().next();
		assertSame(outputPortBinding,
				scufl2Tools.processorPortBindingForPort(port, profile));
	}

	@Test
	public void processorPortBindingForOutputProcessorPort() throws Exception {
		Profile profile = workflowBundle.getMainProfile();
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		OutputProcessorPort port = hello.getOutputPorts().getByName("greeting");
		ProcessorBinding processorBinding = profile.getProcessorBindings()
				.getByName("Hello");
		ProcessorOutputPortBinding outputPortBinding = processorBinding
				.getOutputPortBindings().iterator().next();
		assertSame(outputPortBinding,
				scufl2Tools.processorPortBindingForPort(port, profile));
	}

	@Test
	public void createProcessorFromActivity() throws Exception {
		Profile profile = new Profile();
		Activity a = new Activity();
		a.setParent(profile);
		new InputActivityPort(a, "in1");
		new InputActivityPort(a, "in2").setDepth(1);		
		new OutputActivityPort(a, "out1");
		new OutputActivityPort(a, "out2").setDepth(0);		
		OutputActivityPort aOut3 = new OutputActivityPort(a, "out3");
		aOut3.setDepth(2);
		aOut3.setGranularDepth(1);
		
		ProcessorBinding binding = scufl2Tools.createProcessorAndBindingFromActivity(a);
		Processor p = binding.getBoundProcessor();
		assertEquals(profile, binding.getParent());
		
		assertEquals(2, p.getInputPorts().size());
		assertEquals(3, p.getOutputPorts().size());
		assertEquals(2, binding.getInputPortBindings().size());
		assertEquals(3, binding.getOutputPortBindings().size());
		assertEquals(a, binding.getBoundActivity());
		assertEquals(p, binding.getBoundProcessor());

	}
	

	@Test
	public void createActivityFromProcessor() throws Exception {
		Processor p = new Processor();
		new InputProcessorPort(p, "in1");
		new InputProcessorPort(p, "in2").setDepth(1);
		
		new OutputProcessorPort(p, "out1");
		new OutputProcessorPort(p, "out2").setDepth(0);
		
		OutputProcessorPort pOut3 = new OutputProcessorPort(p, "out3");
		pOut3.setDepth(2);
		pOut3.setGranularDepth(1);	

		Profile profile = new Profile();
		Activity a = scufl2Tools.createActivityFromProcessor(p, profile);
		
		assertEquals(profile, a.getParent());
		ProcessorBinding binding = scufl2Tools.processorBindingForProcessor(p, profile);
		
		assertEquals(2, a.getInputPorts().size());
		assertEquals(3, a.getOutputPorts().size());
		assertEquals(2, binding.getInputPortBindings().size());
		assertEquals(3, binding.getOutputPortBindings().size());
		assertEquals(a, binding.getBoundActivity());
		assertEquals(p, binding.getBoundProcessor());	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void updatePortBindingByMatchingPorts() throws Exception {
		Processor p = new Processor();
		new InputProcessorPort(p, "in1");
		new InputProcessorPort(p, "in2");
		new OutputProcessorPort(p, "out1");		
		new OutputProcessorPort(p, "out2");
		Profile profile = new Profile();
		Activity a = scufl2Tools.createActivityFromProcessor(p, profile);
		ProcessorBinding binding = scufl2Tools.processorBindingsToActivity(a).get(0);
		
		// Add some
		new InputProcessorPort(p, "new1");
		new InputProcessorPort(p, "new2");
		new InputActivityPort(a, "new1");
		new InputActivityPort(a, "new2");
		new OutputProcessorPort(p, "new3");
		new OutputProcessorPort(p, "new4");
		new OutputActivityPort(a, "new4");
		new OutputActivityPort(a, "new5");
		// And remove some
		p.getInputPorts().removeByName("in2");
		a.getOutputPorts().removeByName("out1");
		
		scufl2Tools.updateBindingByMatchingPorts(binding);
		
//		assertEquals(3, binding.getInputPortBindings().size());
//		assertEquals(2, binding.getOutputPortBindings().size());

		Set<String> namesIn = procPortNames(binding.getInputPortBindings());
		Set<String> namesOut = procPortNames(binding.getOutputPortBindings());
		assertEquals(new HashSet(Arrays.asList("in1", "new1", "new2")), namesIn);
		assertEquals(new HashSet(Arrays.asList("out2", "new4")), namesOut);		
	}
	
	
	@SuppressWarnings("rawtypes")
	private Set<String> procPortNames(
			Set<? extends ProcessorPortBinding> portBindings) {
		Set<String> names = new HashSet<String>();
		for (ProcessorPortBinding portBinding : portBindings) {
			names.add(portBinding.getBoundProcessorPort().getName());
		}
		return names;
	}

	@Test
	public void setParents() throws Exception {
		// Deliberately orphan a profile and a processor
		Profile profile = workflowBundle.getProfiles().getByName("tavernaWorkbench");
		profile.setParent(null);		
		workflowBundle.getProfiles().add(profile);		
		processor.setParent(null);
		workflow.getProcessors().add(processor);
		
		assertNull(processor.getParent());
		assertNull(profile.getParent());		
		scufl2Tools.setParents(workflowBundle);
		assertNotNull(processor.getParent());
		assertNotNull(profile.getParent());				

	}

	@Test
	public void setParentsAllBeans() throws Exception {
		AllBeansVisitor visitBefore = new AllBeansVisitor();
		workflowBundle.accept(visitBefore);
		scufl2Tools.setParents(workflowBundle);		
		AllBeansVisitor visitAfter = new AllBeansVisitor();
		workflowBundle.accept(visitAfter);
		// Ensure we did not loose or double-add anyone
		assertEquals(visitBefore.getAllBeans(), visitAfter.getAllBeans());		
	}
		

	@Test
	public void setParentsAllBeansAgain() throws Exception {
		AllBeansVisitor visitBefore = new AllBeansVisitor();
		workflowBundle.accept(visitBefore);
		setParentsAgain(workflowBundle);		
		AllBeansVisitor visitAfter = new AllBeansVisitor();
		workflowBundle.accept(visitAfter);
		// Ensure we did not loose or double-add anyone
		assertEquals(visitBefore.getAllBeans().toString(), visitAfter.getAllBeans().toString());	
	}

	/**
	 * A slight variation of {@link Scufl2Tools#setParents(org.apache.taverna.scufl2.api.container.WorkflowBundle)}
	 * that always set a new parent
	 * @param wfBundle 
	 * @throws Exception
	 */
	public void setParentsAgain(WorkflowBundle wfBundle) throws Exception {
		wfBundle.accept(new VisitorWithPath() {			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public boolean visit() {
				WorkflowBean node = getCurrentNode();
				if (node instanceof Child) {
					Child child = (Child) node;
					WorkflowBean parent = getCurrentPath().peek();
					child.setParent(parent);
				}
				return true;
			}
		});
	}

	@Test
	public void createActivityPortsFromProcessor() throws Exception {
		Processor p = new Processor();
		new InputProcessorPort(p, "in1");
		new InputProcessorPort(p, "in2").setDepth(1);
		
		new OutputProcessorPort(p, "out1");
		new OutputProcessorPort(p, "out2").setDepth(0);
		
		OutputProcessorPort pOut3 = new OutputProcessorPort(p, "out3");
		pOut3.setDepth(2);
		pOut3.setGranularDepth(1);
		
		
		Activity a = new Activity();
		scufl2Tools.createActivityPortsFromProcessor(a, p);
		
		
		assertEquals(2, a.getInputPorts().size());
		InputActivityPort aIn1 = a.getInputPorts().getByName("in1");
		assertNull(aIn1.getDepth());
		InputActivityPort aIn2 = a.getInputPorts().getByName("in2");
		assertEquals(1, aIn2.getDepth().intValue());
		
		assertEquals(3, a.getOutputPorts().size());
		OutputActivityPort aOut1 = a.getOutputPorts().getByName("out1");
		assertEquals(null, aOut1.getDepth());
		assertEquals(null, aOut1.getGranularDepth());
		
		OutputActivityPort aOut2 = a.getOutputPorts().getByName("out2");
		assertEquals(0, aOut2.getDepth().intValue());
		assertEquals(null, aOut2.getGranularDepth());
		
		OutputActivityPort aOut3 = a.getOutputPorts().getByName("out3");
		assertEquals(2, aOut3.getDepth().intValue());
		assertEquals(1, aOut3.getGranularDepth().intValue());		
	}

	@Test
	public void createActivityPortsFromProcessorWithOverwrite() throws Exception {
		Processor p = new Processor();
		new InputProcessorPort(p, "in1");
		new OutputProcessorPort(p, "out1");
		new OutputProcessorPort(p, "out2").setDepth(1);

		
		Activity a = new Activity();
		new InputActivityPort(a, "other");
		OutputActivityPort toBeOverWritten = new OutputActivityPort(a, "out1");
		toBeOverWritten.setDepth(1);
		assertEquals(a, toBeOverWritten.getParent());
		
		
		scufl2Tools.createActivityPortsFromProcessor(a, p);
		// Still there
		assertNotNull(a.getInputPorts().getByName("other"));
		
		// but out1 has been overwritten
 		OutputActivityPort aOut1 = a.getOutputPorts().getByName("out1");
		assertNull(aOut1.getDepth());
		assertNotSame(toBeOverWritten, aOut1);		
	}

	
	
	@Test
	public void createProcessorPortsFromActivity() throws Exception {
		Activity a = new Activity();
		new InputActivityPort(a, "in1");
		new InputActivityPort(a, "in2").setDepth(1);
		
		new OutputActivityPort(a, "out1");
		new OutputActivityPort(a, "out2").setDepth(0);
		
		OutputActivityPort aOut3 = new OutputActivityPort(a, "out3");
		aOut3.setDepth(2);
		aOut3.setGranularDepth(1);
		
		
		Processor p = new Processor();
		scufl2Tools.createProcessorPortsFromActivity(p, a);
		
		
		assertEquals(2, p.getInputPorts().size());
		InputProcessorPort pIn1 = p.getInputPorts().getByName("in1");
		assertNull(pIn1.getDepth());
		InputProcessorPort pIn2 = p.getInputPorts().getByName("in2");
		assertEquals(1, pIn2.getDepth().intValue());
		
		assertEquals(3, p.getOutputPorts().size());
		OutputProcessorPort pOut1 = p.getOutputPorts().getByName("out1");
		assertEquals(null, pOut1.getDepth());
		assertEquals(null, pOut1.getGranularDepth());
		
		OutputProcessorPort pOut2 = p.getOutputPorts().getByName("out2");
		assertEquals(0, pOut2.getDepth().intValue());
		assertEquals(null, pOut2.getGranularDepth());
		
		OutputProcessorPort pOut3 = p.getOutputPorts().getByName("out3");
		assertEquals(2, pOut3.getDepth().intValue());
		assertEquals(1, pOut3.getGranularDepth().intValue());		
	}

	@Test
	public void createProcessorPortsFromActivityWithOverwrite() throws Exception {
		Activity a = new Activity();

		new InputActivityPort(a, "in1");
		
		new OutputActivityPort(a, "out1");
		new OutputActivityPort(a, "out2").setDepth(1);

		Processor p = new Processor();
		new InputProcessorPort(p, "other");
		OutputProcessorPort toBeOverWritten = new OutputProcessorPort(p, "out1");
		toBeOverWritten.setDepth(1);
		assertEquals(p, toBeOverWritten.getParent());
		
		
		scufl2Tools.createProcessorPortsFromActivity(p, a);
		// Still there
		assertNotNull(p.getInputPorts().getByName("other"));
		
		// but out1 has been overwritten
 		OutputProcessorPort pOut1 = p.getOutputPorts().getByName("out1");
		assertNull(pOut1.getDepth());
		assertNotSame(toBeOverWritten, pOut1);
	}
	
	
	@Test
	public void bindActivityToProcessorByMatchingPorts() throws Exception {
		Processor p = new Processor();
		new InputProcessorPort(p, "in1");
		new InputProcessorPort(p, "in2");
		new OutputProcessorPort(p, "out1");
		new OutputProcessorPort(p, "out2");
		new OutputProcessorPort(p, "out3");
		
		Activity a = new Activity();
		new InputActivityPort(a, "in1");
		// in2 missing
		new InputActivityPort(a, "in3"); // additional in3
		new OutputActivityPort(a, "out1");
		// out2 missing
		new OutputActivityPort(a, "out3");
		new OutputActivityPort(a, "out4"); // additional out4

		ProcessorBinding binding = scufl2Tools.bindActivityToProcessorByMatchingPorts(a, p);
		assertEquals(a, binding.getBoundActivity());
		assertEquals(p, binding.getBoundProcessor());
		assertEquals(1, binding.getInputPortBindings().size());
		ProcessorInputPortBinding inBinding = binding.getInputPortBindings().iterator().next();
		assertEquals(p.getInputPorts().getByName("in1"), inBinding.getBoundProcessorPort());
		assertEquals(a.getInputPorts().getByName("in1"), inBinding.getBoundActivityPort());
		
		assertEquals(2, binding.getOutputPortBindings().size());
		// should be out1 and out3
		for (ProcessorOutputPortBinding outBinding : binding.getOutputPortBindings()) {
			assertEquals(outBinding.getBoundActivityPort().getName(),
						outBinding.getBoundProcessorPort().getName());
			assertEquals(a, outBinding.getBoundActivityPort().getParent());
			assertEquals(p, outBinding.getBoundProcessorPort().getParent());
		}
		
	}
}

