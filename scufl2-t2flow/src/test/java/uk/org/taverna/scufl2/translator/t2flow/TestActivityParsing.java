package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser.PORT_DEFINITION;

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
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.BeanshellActivityParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.RshellActivityParser;

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
		WorkflowBundle wfBundle = parser
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
		parser.setStrict(true);
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

		Configuration concatConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(concat, profile);
		Activity concatAct = (Activity) concatConfig.getConfigures();
		assertEquals(BeanshellActivityParser.ACTIVITY_URI,
				concatAct.getConfigurableType());
		assertEquals(BeanshellActivityParser.ACTIVITY_URI.resolve("#Config"), concatConfig
				.getPropertyResource().getTypeURI());
		String script = concatConfig.getPropertyResource().getPropertyAsString(
				BeanshellActivityParser.ACTIVITY_URI.resolve("#script"));
		assertEquals("output = string1 + string2;", script);

		Set<String> expectedInputs = new HashSet<String>(Arrays.asList(
				"string1", "string2"));
		assertEquals(expectedInputs, concatAct.getInputPorts().getNames());
		InputActivityPort s1 = concatAct.getInputPorts().getByName("string1");
		assertEquals(0, s1.getDepth().intValue());
		InputActivityPort s2 = concatAct.getInputPorts().getByName("string2");
		assertEquals(0, s2.getDepth().intValue());

		Set<PropertyResource> inputDef = concatConfig.getPropertyResource()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#inputPortDefinition"));
		assertEquals(2, inputDef.size());

		Set<URI> expectedPortUris = new HashSet<URI>();
		for (InputActivityPort inPort : concatAct.getInputPorts()) {
			expectedPortUris.add(new URITools().relativeUriForBean(inPort,
					concatConfig));
		}
		assertEquals(2, expectedPortUris.size());
		assertEquals(2, inputDef.size());
		for (PropertyResource portDef : inputDef) {
			assertEquals(PORT_DEFINITION.resolve("#InputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI dataType = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#dataType"));

			assertEquals("java", dataType.getScheme());
			assertEquals("java.lang.String", dataType
					.getSchemeSpecificPart());

			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesInputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
		}

		// TODO: Is java class here OK? It's a beanshell script after all..

		Set<String> expectedOutputs = new HashSet<String>(
				Arrays.asList("output"));
		assertEquals(expectedOutputs, concatAct.getOutputPorts().getNames());
		OutputActivityPort out = concatAct.getOutputPorts().getByName("output");
		assertEquals(0, out.getDepth().intValue());

		Set<PropertyResource> outputDef = concatConfig.getPropertyResource()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#outputPortDefinition"));
		assertEquals(1, outputDef.size());
		PropertyResource out1Def = outputDef.iterator().next();

		assertEquals(PORT_DEFINITION.resolve("#OutputPortDefinition"),
				out1Def.getTypeURI());

		Set<URI> mimeTypes = out1Def.getPropertiesAsResourceURIs(PORT_DEFINITION
				.resolve("#expectedMimeType"));
		assertEquals(1, mimeTypes.size());

		assertEquals(URI.create("http://purl.org/NET/mediatypes/text/plain"),
				mimeTypes.iterator().next());


		Processor echoList = researchObj.getMainWorkflow().getProcessors()
				.getByName("Echo_List");
		Configuration echoConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(echoList, profile);
		Activity echoAct = (Activity) echoConfig.getConfigures();

		expectedInputs = new HashSet<String>(Arrays.asList("inputlist"));
		assertEquals(expectedInputs, echoAct.getInputPorts().getNames());
		InputActivityPort inputList = echoAct.getInputPorts().getByName(
				"inputlist");
		assertEquals(1, inputList.getDepth().intValue());

		expectedOutputs = new HashSet<String>(Arrays.asList("outputlist"));
		assertEquals(expectedOutputs, echoAct.getOutputPorts().getNames());
		OutputActivityPort outputList = echoAct.getOutputPorts().getByName(
				"outputlist");
		assertEquals(1, outputList.getDepth().intValue());

	}
	
	@Test
	public void parseRShellScript() throws Exception {
		URL wfResource = getClass().getResource(WF_ALL_ACTIVITIES);
		assertNotNull("Could not find workflow " + WF_ALL_ACTIVITIES, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(false);
		WorkflowBundle researchObj = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = researchObj.getMainProfile();
		Processor proc = researchObj.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);

		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		Activity activity = (Activity) config.getConfigures();
		assertEquals(RshellActivityParser.ACTIVITY_URI,
				activity.getConfigurableType());
		assertEquals(RshellActivityParser.ACTIVITY_URI.resolve("#Config"), config
				.getPropertyResource().getTypeURI());
		String script = config.getPropertyResource().getPropertyAsString(
				RshellActivityParser.ACTIVITY_URI.resolve("#script"));
		assertEquals("rshell\nscript", script);

		Set<String> expectedInputs = new HashSet<String>(Arrays.asList(
				"in1", "in2", "in3"));
		assertEquals(expectedInputs, activity.getInputPorts().getNames());
		InputActivityPort in1 = activity.getInputPorts().getByName("in1");
		assertEquals(0, in1.getDepth().intValue());
		InputActivityPort in2 = activity.getInputPorts().getByName("in2");
		assertEquals(0, in2.getDepth().intValue());
		InputActivityPort in3 = activity.getInputPorts().getByName("in3");
		assertEquals(1, in3.getDepth().intValue());

		Set<PropertyResource> inputDef = config.getPropertyResource()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#inputPortDefinition"));
		assertEquals(3, inputDef.size());

		Set<URI> expectedPortUris = new HashSet<URI>();
		for (InputActivityPort inPort : activity.getInputPorts()) {
			expectedPortUris.add(new URITools().relativeUriForBean(inPort,
					config));
		}
		assertEquals(3, expectedPortUris.size());
		assertEquals(3, inputDef.size());		
		for (PropertyResource portDef : inputDef) {
			assertEquals(PORT_DEFINITION.resolve("#InputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesInputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
		}


		Set<String> expectedOutputs = new HashSet<String>(
				Arrays.asList("out1", "out2", "out3", "out4"));
		assertEquals(expectedOutputs, activity.getOutputPorts().getNames());
		OutputActivityPort out1 = activity.getOutputPorts().getByName("out1");
		assertEquals(0, out1.getDepth().intValue());
		OutputActivityPort out2 = activity.getOutputPorts().getByName("out2");
		assertEquals(0, out2.getDepth().intValue());
		OutputActivityPort out3 = activity.getOutputPorts().getByName("out3");
		assertEquals(1, out3.getDepth().intValue());
		OutputActivityPort out4 = activity.getOutputPorts().getByName("out4");
		assertEquals(0, out4.getDepth().intValue());

		Set<PropertyResource> outputDef = config.getPropertyResource()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#outputPortDefinition"));
		assertEquals(4, outputDef.size());
		PropertyResource out1Def = outputDef.iterator().next();

		assertEquals(PORT_DEFINITION.resolve("#OutputPortDefinition"),
				out1Def.getTypeURI());

		PropertyResource connection = config.getPropertyResource().getPropertyAsResource(RshellActivityParser.ACTIVITY_URI.resolve("#connection"));
		assertEquals(RshellActivityParser.ACTIVITY_URI.resolve("#Connection"), connection.getTypeURI());

		assertEquals("localhost", connection.getPropertyAsString(RshellActivityParser.ACTIVITY_URI.resolve("#hostname")));
		
		PropertyLiteral portLiteral = connection.getPropertyAsLiteral(RshellActivityParser.ACTIVITY_URI.resolve("#port"));
		assertEquals(6311, portLiteral.getLiteralValueAsInt());
		assertEquals(PropertyLiteral.XSD_UNSIGNEDSHORT, portLiteral.getLiteralType());

		assertTrue(connection.getPropertyAsLiteral(RshellActivityParser.ACTIVITY_URI.resolve("#keepSessionAlive")).getLiteralValueAsBoolean());
		
		// TODO Check semantic types
	}

}
