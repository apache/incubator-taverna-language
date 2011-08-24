package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestExternalToolActivityParser {
	private static final String WF_2_2 = "/tool-2-2.t2flow";
	private static final String WF_2_3 = "/tool-2-3.t2flow";
	private static final String WF_2_2_RESAVED_2_3 = "/tool-2-2-resaved-2-3.t2flow";
	

	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private T2FlowParser parser;

	@Before
	public void makeParser() throws JAXBException {
		parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		
	}
	
	
	@Test
	public void parse2_2() throws Exception {
		URL wfResource = getClass().getResource(WF_2_2);
		assertNotNull("Could not find workflow " + WF_2_2, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("cat");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		// TODO: Check data types defined (semantic types)
		
	}
	
}
