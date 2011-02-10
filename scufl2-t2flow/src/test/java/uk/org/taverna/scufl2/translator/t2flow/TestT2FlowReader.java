package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.org.taverna.scufl2.translator.t2flow.T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.structure.StructureReader;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestT2FlowReader {
	
	WorkflowBundleIO io = new WorkflowBundleIO();
	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		WorkflowBundle researchObj = io.readBundle(wfResource,
				APPLICATION_VND_TAVERNA_T2FLOW_XML);
		Profile profile = researchObj.getMainProfile();
		assertEquals(1, researchObj.getProfiles().size());
		assertEquals(profile,
				researchObj.getProfiles().getByName("taverna-2.1.0"));
		
		String report = IOUtils.toString(getClass().getResourceAsStream("/as.txt"));
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		io.writeBundle(researchObj, byteStream, StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals(report, byteStream.toString("utf-8"));		
	}
}
