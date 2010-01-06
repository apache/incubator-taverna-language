package net.sf.taverna.scufl2.api;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import net.sf.taverna.scufl2.api.activity.Activity;
import net.sf.taverna.scufl2.api.activity.ActivityType;
import net.sf.taverna.scufl2.api.container.TavernaResearchObject;
import net.sf.taverna.scufl2.api.core.DataLink;
import net.sf.taverna.scufl2.api.core.Processor;
import net.sf.taverna.scufl2.api.core.Workflow;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.port.InputWorkflowPort;
import net.sf.taverna.scufl2.api.port.OutputProcessorPort;
import net.sf.taverna.scufl2.api.port.OutputWorkflowPort;

import org.junit.Test;

public class TestAPICreation {

	@Test
	public void makeExampleWorkflow() throws Exception {

		ScuflFactory scuflFactory = new ScuflFactory();

		TavernaResearchObject ro = new TavernaResearchObject();
		Workflow wf1 = ro.getMainWorkflow();
		
		assertEquals("Non-empty input ports", Collections.EMPTY_SET, wf1.getInputPorts());
		
		InputWorkflowPort i = wf1.addInputPort("I");
		assertEquals("Did not add input port 'I'", Collections.singleton(i), wf1
				.getInputPorts());
		
		OutputWorkflowPort wf1_out1 = wf1.addOutputPort("out1");		
		assertTrue("Did not add 'out1' to list of output ports", wf1
				.getOutputPorts().contains(wf1_out1));
		
		wf1.addOutputPort("out1");
		
		assertTrue("Unexpected processors", wf1.getProcessors().isEmpty());
		Processor p1 = wf1.addProcessor("p1");
		assertTrue("Did not add processor", wf1.getProcessors().contains(p1));
				
		InputProcessorPort p1_y1 = p1.addInputPort("Y1");
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

		wf1.getDatalinks().add(new DataLink(p1_y2, pNested_i));

		wf1.getDatalinks().add(new DataLink(p1_y2, p4_x2));

		wf1.getDatalinks().add(new DataLink(pNested_o, p1_y1));

		wf1.getDatalinks().add(new DataLink(p4_y, wf1_out1));

		Activity activity = new Activity("act0");
		ro.getActivities().add(activity);
		activity.setType(new ActivityType("http://taverna.sf.net/2009/2.1/activity/beanshell"));


	}

}
