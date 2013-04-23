package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestT2FlowParser {

	private static final String INTERACTION_WITH_LOOP = "/interaction-with-strange-loop.t2flow";
	private static final String AS_T2FLOW = "/as.t2flow";
	
	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Profile profile = wfBundle.getMainProfile();
		assertEquals(1, wfBundle.getProfiles().size());
		assertEquals(profile, wfBundle.getProfiles().getByName("taverna-2.1.0"));
		
	}
	
	@Test
	public void unconfigureLoopLayer() throws Exception {
		URL wfResource = getClass().getResource(INTERACTION_WITH_LOOP);
		assertNotNull("Could not find workflow " + INTERACTION_WITH_LOOP, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(false);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Scufl2Tools scufl2Tools = new Scufl2Tools();
		Processor interaction = wfBundle.getMainWorkflow().getProcessors().getByName("BioSTIFInteraction");
		DispatchStackLayer loopLayer = interaction.getDispatchStack().get(2);
		assertEquals("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Loop", 
				loopLayer.getConfigurableType().toString());
		List<Configuration> loopConfigs = scufl2Tools.configurationsFor(loopLayer, wfBundle.getMainProfile());
		// unconfigured
		assertTrue(loopConfigs.isEmpty());		
	}

	
}
