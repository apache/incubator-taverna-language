package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestActivityParsing {

	private static final String WF_ALL_ACTIVITIES = "/defaultActivitiesTaverna2.2.t2flow";
	private static final String WF_AS = "/as.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WF_ALL_ACTIVITIES);
		assertNotNull("Could not find workflow " + WF_ALL_ACTIVITIES, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
//		 parser.setStrict(true);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());
//		System.out.println(researchObj.getProfiles().iterator().next()
//				.getConfigurations());

	}
	
	@Test
	public void parseBeanshellScript() throws Exception {
		URL wfResource = getClass().getResource(WF_AS);
		assertNotNull("Could not find workflow " + WF_AS, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		// parser.setStrict(true);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());
//		System.out.println(researchObj.getProfiles().iterator().next()
//				.getConfigurations());
		Profile profile = researchObj.getProfiles().getByName("taverna-2.1.0");
		for (Configuration config : profile.getConfigurations()) {
			Activity a = (Activity) config.getConfigures();
			if (a.getType().getName())
			
			System.out.println(config.getConfigurationType());
		}
		System.out.println(profile.getConfigurations().getNames());
	}


}
