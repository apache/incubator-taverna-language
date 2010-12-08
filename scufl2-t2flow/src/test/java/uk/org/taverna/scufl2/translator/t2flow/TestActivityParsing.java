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
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ObjectProperty;
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


		Configuration concatConfig = scufl2Tools.configurationForActivityBoundToProcessor(concat, profile);
		Activity concatAct = (Activity) concatConfig.getConfigures();
		assertEquals(BeanshellActivityParser.ACTIVITY_URI, concatAct.getType());
		assertEquals(ACTIVITY_URI.resolve("#ConfigType"),
				concatConfig.getConfigurationType());
		String script = scufl2Tools.getPropertyData(concatConfig.getProperties(),
				ACTIVITY_URI.resolve("#script"));
		assertEquals("output = string1 + string2;", script);

		Set<String> expectedInputs = new HashSet<String>(Arrays.asList("string1", "string2"));
		assertEquals(expectedInputs, concatAct.getInputPorts().getNames());
		InputActivityPort s1 = concatAct.getInputPorts().getByName("string1");
		assertEquals(0, s1.getDepth().intValue());
		InputActivityPort s2 = concatAct.getInputPorts().getByName("string2");
		assertEquals(0, s2.getDepth().intValue());

		Set<ObjectProperty> inputDef = scufl2Tools.getPropertyObjects(
				concatConfig.getProperties(),
				ACTIVITY_URI.resolve("#inputPortDefinition"));
		assertEquals(2, inputDef.size());

		Set<URI> inputPorts = new HashSet<URI>();
		for (InputActivityPort inPort : concatAct.getInputPorts()) {
			inputPorts.add(new URITools().relativeUriForBean(inPort,
					concatConfig));
		}
		// assertEquals(2, inputPorts.size());

		for (ObjectProperty portDef : inputDef) {
			assertEquals(ACTIVITY_URI.resolve("#InputPortDefinition"),
					portDef.getObjectClass());
			assertNull(portDef.getObjectUri());

			ObjectProperty dataType = scufl2Tools.getPropertyObject(
					portDef.getObjectProperties(),
					ACTIVITY_URI.resolve("#dataType"));
			assertEquals("java", dataType.getObjectUri().getScheme());
			assertEquals("java.lang.String", dataType.getObjectUri()
					.getSchemeSpecificPart());

			ObjectProperty port = scufl2Tools.getPropertyObject(
					portDef.getObjectProperties(),
					ACTIVITY_URI.resolve("#definesInputPort"));
			System.out.println(port.getObjectUri());

		}



		// TODO: Is java class here OK? It's a beanshell script after all..

		Set<String> expectedOutputs = new HashSet<String>(Arrays.asList("output"));
		assertEquals(expectedOutputs, concatAct.getOutputPorts().getNames());
		OutputActivityPort out = concatAct.getOutputPorts().getByName("output");
		assertEquals(0, out.getDepth().intValue());

		Set<ObjectProperty> outputDef = scufl2Tools.getPropertyObjects(
				concatConfig.getProperties(),
				ACTIVITY_URI.resolve("#outputPortDefinition"));
		assertEquals(1, outputDef.size());
		ObjectProperty out1Def = outputDef.iterator().next();
		assertEquals(ACTIVITY_URI.resolve("#OutputPortDefinition"),
				out1Def.getObjectClass());

		// FIXME: mimetype should be an annotation?
		// FIXME: This should not be in the Beanshell namespace
		ObjectProperty mimeType = scufl2Tools.getPropertyObject(
				out1Def.getObjectProperties(),
				ACTIVITY_URI.resolve("#expectedMimeType"));
		assertEquals(URI.create("http://purl.org/NET/mediatypes/text/plain"), mimeType.getObjectUri());


		Processor echoList = researchObj.getMainWorkflow().getProcessors()
		.getByName("Echo_List");
		Configuration echoConfig = scufl2Tools.configurationForActivityBoundToProcessor(echoList, profile);
		Activity echoAct = (Activity) echoConfig.getConfigures();

		expectedInputs = new HashSet<String>(Arrays.asList("inputlist"));
		assertEquals(expectedInputs, echoAct.getInputPorts().getNames());
		InputActivityPort inputList = echoAct.getInputPorts().getByName("inputlist");
		assertEquals(1, inputList.getDepth().intValue());

		expectedOutputs = new HashSet<String>(Arrays.asList("outputlist"));
		assertEquals(expectedOutputs, echoAct.getOutputPorts().getNames());
		OutputActivityPort outputList = echoAct.getOutputPorts().getByName("outputlist");
		assertEquals(1, outputList.getDepth().intValue());



	}

}
