package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.api.common.Scufl2Tools.PORT_DEFINITION;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.BeanshellActivityParser;

public class TestFastaWorkflow {

	private static final String WF_ALL_ACTIVITIES = "/defaultActivitiesTaverna2.2.t2flow";
	private static final String WF_AS = "/as.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WF_ALL_ACTIVITIES);
		assertNotNull("Could not find workflow " + WF_ALL_ACTIVITIES,
				wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		// parser.setStrict(true);
		WorkflowBundle wfBundle = parser
				.parseT2Flow(wfResource.openStream());
		// System.out.println(researchObj.getProfiles().iterator().next()
		// .getConfigurations());

	}

}
