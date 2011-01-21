package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestT2FlowParser {

	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());
		Profile profile = researchObj.getMainProfile();
		assertEquals(1, researchObj.getProfiles().size());
		assertEquals(profile, researchObj.getProfiles().getByName("taverna-2.1.0"));
	}

}
