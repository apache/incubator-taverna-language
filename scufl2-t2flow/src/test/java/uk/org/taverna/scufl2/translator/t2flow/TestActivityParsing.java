package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestActivityParsing {

	private static final String WORKFLOW = "/defaultActivitiesTaverna2.2.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WORKFLOW);
		assertNotNull("Could not find workflow " + WORKFLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		// parser.setStrict(true);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());
//		System.out.println(researchObj.getProfiles().iterator().next()
//				.getConfigurations());

	}

}
