package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestDispatchLayerParsing {

	private static final String WF_DISPATCH_LAYERS = "/dispatchlayers.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private T2FlowParser parser;
	private WorkflowBundle wfBundle;
	private Profile profile;
	private Workflow workflow;
	private NamedSet<Processor> processors;
	
	@Before
	public void readWorkflow() throws Exception {		
		parser = new T2FlowParser();
		parser.setValidating(true);
		URL wfResource = getClass().getResource(WF_DISPATCH_LAYERS);
		assertNotNull("Could not find workflow " + WF_DISPATCH_LAYERS,
				wfResource);		
		// parser.setStrict(true);
		wfBundle = parser
				.parseT2Flow(wfResource.openStream());
		profile = wfBundle.getMainProfile();
		workflow = wfBundle.getMainWorkflow();
		processors = workflow.getProcessors();
	}
	
	@Test
	public void retries() throws Exception {
		Processor retries = processors.getByName("retries");
		
		DispatchStackLayer retry = retries.getDispatchStack().getByName("retry");
	}

	
}

