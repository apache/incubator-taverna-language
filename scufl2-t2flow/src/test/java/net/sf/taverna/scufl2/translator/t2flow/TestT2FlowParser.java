package net.sf.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.net.URL;

import net.sf.taverna.scufl2.api.container.TavernaResearchObject;

import org.junit.Test;

public class TestT2FlowParser {

	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		TavernaResearchObject researchObj = parser.parseT2Flow(wfResource.openStream());
		System.out.println(researchObj);
	}
	
}
