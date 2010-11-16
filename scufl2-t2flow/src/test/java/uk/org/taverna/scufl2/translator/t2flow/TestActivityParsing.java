package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;
import static uk.org.taverna.scufl2.translator.t2flow.defaultactivities.BeanshellActivityParser.ACTIVITY_URI;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.BeanshellActivityParser;

public class TestActivityParsing {

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
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		// System.out.println(researchObj.getProfiles().iterator().next()
		// .getConfigurations());

	}

	@Test
	public void parseBeanshellScript() throws Exception {
		URL wfResource = getClass().getResource(WF_AS);
		assertNotNull("Could not find workflow " + WF_AS, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		// parser.setStrict(true);
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		// System.out.println(researchObj.getProfiles().iterator().next()
		// .getConfigurations());
		Profile profile = researchObj.getProfiles().getByName("taverna-2.1.0");
		// Processors: [Workflow19, Echo_List, Concatenate_two_strings,
		// Concatenate_two_strings_2, Concatenate_two_strings_3,
		// Concatenate_two_strings_4, Create_Lots_Of_Strings, String_constant]
		Processor concat = researchObj.getMainWorkflow().getProcessors()
				.getByName("Concatenate_two_strings");
		
		ProcessorBinding binding = scufl2Tools.processorBindingForProcessor(concat, profile);
		Activity bindingAct = binding.getBoundActivity();
		Configuration config = scufl2Tools.configurationFor(binding.getBoundActivity(), profile);
		
		System.out.println(bindingAct.getName());
		assertEquals(BeanshellActivityParser.ACTIVITY_URI, bindingAct.getType());
		assertEquals(ACTIVITY_URI.resolve("#ConfigType"),
				config.getConfigurationType());
		String script = scufl2Tools.getPropertyData(config.getProperties(),
				ACTIVITY_URI.resolve("#script"));
		System.out.println(script);
		
		Set<String> expectedInputs = new HashSet<String>(Arrays.asList("string1", "string2"));
		assertEquals(expectedInputs, bindingAct.getInputPorts().getNames());
		InputActivityPort s1 = bindingAct.getInputPorts().getByName("string1");
		assertEquals(0, s1.getDepth().intValue());
		InputActivityPort s2 = bindingAct.getInputPorts().getByName("string2");
		assertEquals(0, s2.getDepth().intValue());		
		
		Set<String> expectedOutputs = new HashSet<String>(Arrays.asList("output"));		
		assertEquals(expectedOutputs, bindingAct.getOutputPorts().getNames());
		OutputActivityPort out = bindingAct.getOutputPorts().getByName("output");
		assertEquals(0, out.getDepth().intValue());
		
	}

}
