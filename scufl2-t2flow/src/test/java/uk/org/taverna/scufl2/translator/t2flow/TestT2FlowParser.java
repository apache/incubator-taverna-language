package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.net.URL;


import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestT2FlowParser {

	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(false);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());
		System.out.println(researchObj);
	}
	
}
