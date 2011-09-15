package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collections;

import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestScufl2Tools {

	private WorkflowBundle wfBundle = new ExampleWorkflow()
			.makeWorkflowBundle();
	private Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void controlLinksBlocking() {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		Processor wait4me = wfBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		ControlLink controlLink = wfBundle.getMainWorkflow().getControlLinks()
				.iterator().next();

		assertEquals(Collections.singletonList(controlLink),
				scufl2Tools.controlLinksBlocking(hello));
		assertTrue(scufl2Tools.controlLinksBlocking(wait4me).isEmpty());
	}

	@Test
	public void controlLinksWaitingFor() {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		Processor wait4me = wfBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		ControlLink controlLink = wfBundle.getMainWorkflow().getControlLinks()
				.iterator().next();

		assertEquals(Collections.singletonList(controlLink),
				scufl2Tools.controlLinksWaitingFor(wait4me));
		assertTrue(scufl2Tools.controlLinksWaitingFor(hello).isEmpty());
	}

	@Test
	public void processorPortBindingForInputActivityPort() throws Exception {
		Profile profile = wfBundle.getMainProfile();
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
		Profile profile = wfBundle.getMainProfile();
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
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
		Profile profile = wfBundle.getMainProfile();
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
		Profile profile = wfBundle.getMainProfile();
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
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
	public void dispatchStackForType() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertTrue("Need at least one layer", 
				! hello.getDispatchStack().isEmpty());
		for (DispatchStackLayer layer : hello.getDispatchStack()) {
			URI type = layer.getConfigurableType();
			assertNotNull(type);
			assertSame(layer, scufl2Tools.dispatchStackByType(hello, type));
		}
		assertNull(scufl2Tools.dispatchStackByType(hello, URI.create("http://example.com/unknown")));
	}
	
}
