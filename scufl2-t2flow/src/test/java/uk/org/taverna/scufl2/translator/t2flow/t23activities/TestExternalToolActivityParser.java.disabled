package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static uk.org.taverna.scufl2.api.common.Scufl2Tools.PORT_DEFINITION;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.ExternalToolActivityParser.ACTIVITY_URI;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.ExternalToolActivityParser.CHARSET;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.ExternalToolActivityParser.CNT;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.ExternalToolActivityParser.DC;

import java.net.URI;
import java.net.URL;
import java.util.SortedSet;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;

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
		checkT2Parsers();

	}

	private void checkT2Parsers() {
		for (T2Parser t2Parser : parser.getT2Parsers()) {
			if (t2Parser instanceof ExternalToolActivityParser) {
				return;
			}
		}
		fail("Could not find ExternalToolActivityParser, found " + parser.getT2Parsers());
	}

	@Test
	public void parse2_2() throws Exception {
		URL wfResource = getClass().getResource(WF_2_2);
		assertNotNull("Could not find workflow " + WF_2_2, wfResource);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors().getByName("cat");
		assertNotNull(proc);
		Configuration config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());

		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, activity.getType());

		String repositoryUrl = config.getJson().getPropertyAsString(
				ACTIVITY_URI.resolve("#repositoryUrl"));
		assertEquals("http://taverna.nordugrid.org/sharedRepository/xml.php", repositoryUrl);
		String toolId = config.getJson().getPropertyAsString(
				ACTIVITY_URI.resolve("#toolId"));
		assertEquals("cat", toolId);

		// Not much more to check as 2.2 does not include tool description

	}

	@Test
	@Ignore("Parser changed, testing using parse2_2_resaved_simple instead")
	public void parse2_2_resaved() throws Exception {
		URL wfResource = getClass().getResource(WF_2_2_RESAVED_2_3);
		assertNotNull("Could not find workflow " + WF_2_2_RESAVED_2_3, wfResource);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());

		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors().getByName("cat");
		assertNotNull(proc);
		Configuration config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());
		PropertyResource resource = config.getJson();
		assertTrue(resource.hasProperty(ACTIVITY_URI.resolve("#toolId")));
		URI toolId = resource.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#toolId"));
		assertEquals("http://taverna.nordugrid.org/sharedRepository/xml.php#cat",
				toolId.toASCIIString());
		assertEquals(false, resource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#edited"))
				.getLiteralValueAsBoolean());

		PropertyResource invocation = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#invocation"));

		assertEquals(ACTIVITY_URI.resolve("#local"),
				invocation.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("default local",
				invocation.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));

		assertFalse(invocation.hasProperty(ACTIVITY_URI.resolve("#mechanismXML")));
		assertFalse(invocation.hasProperty(ACTIVITY_URI.resolve("#node")));

		PropertyResource description = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#toolDescription"));
		assertEquals("cat", description.getPropertyAsString(DC.resolve("title")));
		assertEquals("Testing", description.getPropertyAsString(ACTIVITY_URI.resolve("#category")));
		assertEquals("concatenation of two streams",
				description.getPropertyAsString(DC.resolve("description")));
		assertEquals("cat file1.txt file2.txt",
				description.getPropertyAsString(ACTIVITY_URI.resolve("#command")));
		assertEquals(1200,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#preparingTimeoutInSeconds"))
						.getLiteralValueAsInt());
		assertEquals(1800,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#executionTimeoutInSeconds"))
						.getLiteralValueAsInt());

		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#tag")).isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#runtimeEnvironment"))
				.isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#queue")).isEmpty());

		assertEquals(false, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdIn"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdOut"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdErr"))
				.getLiteralValueAsBoolean());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#validReturnCode"))
				.isEmpty());

		Activity activity = (Activity) config.getConfigures();
		assertEquals(2, activity.getInputPorts().size());
		InputActivityPort first_file = activity.getInputPorts().getByName("First_file");
		assertNotNull("Could not find activity input port first_file", first_file);
		assertEquals(Integer.valueOf(0), first_file.getDepth());

		InputActivityPort second_file = activity.getInputPorts().getByName("Second_file");
		assertNotNull("Could not find activity input port second_file", second_file);
		assertEquals(Integer.valueOf(0), second_file.getDepth());

		// No STDIN as includeStdIn is false

		assertEquals(2, activity.getOutputPorts().size());
		OutputActivityPort stdout = activity.getOutputPorts().getByName("STDOUT");
		assertNotNull("Could not find output port STDOUT", stdout);

		OutputActivityPort stderr = activity.getOutputPorts().getByName("STDERR");
		assertNotNull("Could not find output port STDERR", stderr);

		PropertyResource portDefinition = scufl2Tools.portDefinitionFor(first_file, profile);
		assertNotNull("Could not find port definition for first_file", portDefinition);
		assertEquals(PropertyLiteral.XSD_STRING,
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#File"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("file1.txt",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));

		assertEquals(CHARSET.resolve("#windows-1252"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#charset")));

		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		// Not translated:
		// assertNull(portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#concatenate")));
		// assertNull(portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#list")));
		// assertNull(portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#mime")));

		portDefinition = scufl2Tools.portDefinitionFor(second_file, profile);
		assertNotNull("Could not find port definition for first_file", portDefinition);
		assertEquals(PropertyLiteral.XSD_STRING,
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#File"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("file2.txt",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));

		assertEquals(CHARSET.resolve("#windows-1252"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#charset")));

		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

	}

	@Test
	public void parse2_2_resaved_simple() throws Exception {
		URL wfResource = getClass().getResource(WF_2_2_RESAVED_2_3);
		assertNotNull("Could not find workflow " + WF_2_2_RESAVED_2_3, wfResource);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());

		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors().getByName("cat");
		assertNotNull(proc);
		Configuration config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());
		PropertyResource resource = config.getJson();
		assertTrue(resource.hasProperty(ACTIVITY_URI.resolve("#repositoryUrl")));
		String repositoryUrl = resource.getPropertyAsString(ACTIVITY_URI.resolve("#repositoryUrl"));
		assertEquals("http://taverna.nordugrid.org/sharedRepository/xml.php", repositoryUrl);
		assertTrue(resource.hasProperty(ACTIVITY_URI.resolve("#toolId")));
		String toolId = resource.getPropertyAsString(ACTIVITY_URI.resolve("#toolId"));
		assertEquals("cat", toolId);
		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		assertEquals("789663B8-DA91-428A-9F7D-B3F3DA185FD4",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("default local",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
				"<localInvocation />\r\n" +
				"",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismXML")));

		PropertyResource description = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#toolDescription"));
		assertEquals("cat", description.getPropertyAsString(DC.resolve("title")));
		assertEquals("Testing", description.getPropertyAsString(ACTIVITY_URI.resolve("#group")));
		assertEquals("concatenation of two streams",
				description.getPropertyAsString(DC.resolve("description")));
		assertEquals("cat file1.txt file2.txt",
				description.getPropertyAsString(ACTIVITY_URI.resolve("#command")));
		assertEquals(1200,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#preparingTimeoutInSeconds"))
						.getLiteralValueAsInt());
		assertEquals(1800,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#executionTimeoutInSeconds"))
						.getLiteralValueAsInt());

		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#tag")).isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#runtimeEnvironment"))
				.isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#queue")).isEmpty());

		assertEquals(false, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdIn"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdOut"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdErr"))
				.getLiteralValueAsBoolean());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#validReturnCode"))
				.isEmpty());

		SortedSet<PropertyResource> inputs = description.getPropertiesAsResources(ACTIVITY_URI
				.resolve("#inputs"));
		assertEquals(2, inputs.size());

		for (PropertyResource inputMap : inputs) {
			String port = inputMap.getPropertyAsString(ACTIVITY_URI.resolve("#port"));

			if (port.equals("First_file")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("file1.txt", input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("Second_file")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("file2.txt",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			}
		}

	}

	@Test
	@Ignore("Parser changed, testing using parse2_3_simple instead")
	public void parse2_3() throws Exception {
		URL wfResource = getClass().getResource(WF_2_3);
		assertNotNull("Could not find workflow " + WF_2_3, wfResource);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());

		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors().getByName("Tool");
		assertNotNull(proc);
		Configuration config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());
		PropertyResource resource = config.getJson();
		URI toolId = resource.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#toolId"));
		assertEquals(ACTIVITY_URI.resolve("#2cd545bf-64ae-4cda-84fc-8cfe2faed772"), toolId);

		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		PropertyResource invocation = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#invocation"));
		assertEquals(ACTIVITY_URI.resolve("#Invocation"), invocation.getTypeURI());

		assertEquals(ACTIVITY_URI.resolve("#local"),
				invocation.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("default local",
				invocation.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));

		assertFalse(invocation.hasProperty(ACTIVITY_URI.resolve("#mechanismXML")));
		assertFalse(invocation.hasProperty(ACTIVITY_URI.resolve("#node")));

		PropertyResource description = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#toolDescription"));
		assertEquals("someName", description.getPropertyAsString(DC.resolve("title")));
		assertEquals("some group",
				description.getPropertyAsString(ACTIVITY_URI.resolve("#category")));
		assertEquals("the description\n  goes here",
				description.getPropertyAsString(DC.resolve("description")));
		assertEquals("cat %%theString%% to %%stringReplace%% file ",
				description.getPropertyAsString(ACTIVITY_URI.resolve("#command")));
		assertEquals(1200,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#preparingTimeoutInSeconds"))
						.getLiteralValueAsInt());
		assertEquals(1800,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#executionTimeoutInSeconds"))
						.getLiteralValueAsInt());

		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#tag")).isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#runtimeEnvironment"))
				.isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#queue")).isEmpty());

		assertEquals(false, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdIn"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdOut"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdErr"))
				.getLiteralValueAsBoolean());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#validReturnCode"))
				.isEmpty());

		// Check ports

		Activity activity = (Activity) config.getConfigures();
		assertEquals(6, activity.getInputPorts().size());

		InputActivityPort file1 = activity.getInputPorts().getByName("file1");
		assertNotNull("Could not find activity input port first_file", file1);
		assertEquals(Integer.valueOf(0), file1.getDepth());

		InputActivityPort file2 = activity.getInputPorts().getByName("file2");
		assertNotNull("Could not find activity input port first_file", file2);
		assertEquals(Integer.valueOf(0), file2.getDepth());

		InputActivityPort fileList = activity.getInputPorts().getByName("fileList");
		assertNotNull("Could not find activity input port first_file", fileList);
		assertEquals(Integer.valueOf(1), fileList.getDepth());

		InputActivityPort fileList2 = activity.getInputPorts().getByName("fileList2");
		assertNotNull("Could not find activity input port first_file", fileList2);
		assertEquals(Integer.valueOf(1), fileList2.getDepth());

		InputActivityPort stringReplace = activity.getInputPorts().getByName("stringReplace");
		assertNotNull("Could not find activity input port first_file", stringReplace);
		assertEquals(Integer.valueOf(0), stringReplace.getDepth());

		InputActivityPort stringReplaceWithDifferentPort = activity.getInputPorts().getByName(
				"stringReplaceWithDifferentPort");
		assertNotNull("Could not find activity input port first_file",
				stringReplaceWithDifferentPort);
		assertEquals(Integer.valueOf(0), stringReplaceWithDifferentPort.getDepth());

		// No STDIN as includeStdIn is false

		assertEquals(4, activity.getOutputPorts().size());
		OutputActivityPort stdout = activity.getOutputPorts().getByName("STDOUT");
		assertNotNull("Could not find output port STDOUT", stdout);
		assertEquals(Integer.valueOf(0), stdout.getDepth());

		OutputActivityPort stderr = activity.getOutputPorts().getByName("STDERR");
		assertNotNull("Could not find output port STDERR", stderr);
		assertEquals(Integer.valueOf(0), stderr.getDepth());

		OutputActivityPort out1 = activity.getOutputPorts().getByName("out1");
		assertNotNull("Could not find output port out1", out1);
		assertEquals(Integer.valueOf(0), out1.getDepth());

		OutputActivityPort out2 = activity.getOutputPorts().getByName("out2");
		assertNotNull("Could not find output port out2", out2);
		assertEquals(Integer.valueOf(0), out2.getDepth());

		// Check port definitions

		// 1 - file1
		PropertyResource portDefinition = scufl2Tools.portDefinitionFor(file1, profile);
		assertNotNull("Could not find port definition for file1", portDefinition);
		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#binary"),
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#File"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("file1",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));
		// System.out.println(portDefinition.getProperties().get(ACTIVITY_URI.resolve("#charset1337")));
		assertFalse("Binary files don't have charset",
				portDefinition.hasProperty(ACTIVITY_URI.resolve("#charset1337")));

		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		// 2 - file2
		portDefinition = scufl2Tools.portDefinitionFor(file2, profile);
		assertNotNull("Could not find port definition for file2", portDefinition);
		assertEquals(PropertyLiteral.XSD_STRING,
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#File"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("anotherFile.txt",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));
		assertTrue(portDefinition.hasProperty(ACTIVITY_URI.resolve("#charset")));
		assertEquals(CHARSET.resolve("#windows-1252"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#charset")));

		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		// 3 - fileList
		portDefinition = scufl2Tools.portDefinitionFor(fileList, profile);
		assertNotNull("Could not find port definition for fileList", portDefinition);
		assertEquals(PropertyLiteral.XSD_STRING,
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#File"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("fileList",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));
		assertTrue(portDefinition.hasProperty(ACTIVITY_URI.resolve("#charset")));
		assertEquals(CHARSET.resolve("#windows-1252"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#charset")));

		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		// 4 - fileList2
		portDefinition = scufl2Tools.portDefinitionFor(fileList2, profile);
		assertNotNull("Could not find port definition for fileList2", portDefinition);
		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#binary"),
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#File"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("another",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#charset")));

		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		// 5 - stringReplace
		portDefinition = scufl2Tools.portDefinitionFor(stringReplace, profile);
		assertNotNull("Could not find port definition for stringReplace", portDefinition);
		assertEquals(PropertyLiteral.XSD_STRING,
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#Parameter"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("stringReplace",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#charset")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		// 6 - stringReplaceWithDifferentPort
		portDefinition = scufl2Tools.portDefinitionFor(stringReplaceWithDifferentPort, profile);
		assertNotNull("Could not find port definition for stringReplaceWithDifferentPort",
				portDefinition);
		assertEquals(PropertyLiteral.XSD_STRING,
				portDefinition.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType")));
		assertEquals(ACTIVITY_URI.resolve("#Parameter"),
				portDefinition.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#substitutionType")));
		assertEquals("theString",
				portDefinition.getPropertyAsString(ACTIVITY_URI.resolve("#substitutes")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#charset")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#forceCopy")));
		assertFalse(portDefinition.hasProperty(ACTIVITY_URI.resolve("#concatenate")));

		SortedSet<PropertyResource> staticInputs = resource.getPropertiesAsResources(ACTIVITY_URI
				.resolve("#staticInput"));
		assertEquals(2, staticInputs.size());
		for (PropertyResource staticInput : staticInputs) {
			String substitutes = staticInput.getPropertyAsString(ACTIVITY_URI
					.resolve("#substitutes"));
			PropertyReference sourceRef = staticInput.getPropertyAsReference(ACTIVITY_URI
					.resolve("#source"));

			if (substitutes.equals("thefile.txt")) {
				PropertyResource source = (PropertyResource) sourceRef;
				assertEquals(CNT.resolve("#ContentAsText"), source.getTypeURI());
				assertEquals("A multi\n   line\n     string inserted here. \u0192(x).",
						source.getPropertyAsString(CNT.resolve("#chars")));
			} else if (substitutes.equals("downloaded.zip")) {
				assertEquals(URI.create("http://example.com/download#strange"),
						sourceRef.getResourceURI());
			} else {
				fail("Unexpected substitution " + substitutes);
			}
		}

		// Processor symbolicLocation
		proc = bundle.getMainWorkflow().getProcessors().getByName("symbolicLocation");
		assertNotNull(proc);
		config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);

		resource = config.getJson();

		toolId = resource.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#toolId"));
		assertEquals(ACTIVITY_URI.resolve("#5dd1fdb0-df3c-4fce-a856-29b4d0ac67bb"), toolId);
		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		invocation = resource.getPropertyAsResource(ACTIVITY_URI.resolve("#invocation"));

		assertEquals(ACTIVITY_URI.resolve("#InvocationGroup"), invocation.getTypeURI());

		assertEquals("asdfsadf", invocation.getPropertyAsString(DC.resolve("identifier")));

		assertEquals(ACTIVITY_URI.resolve("#ssh"),
				invocation.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("asdfasdg",
				invocation.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));

		assertFalse(invocation.hasProperty(ACTIVITY_URI.resolve("#mechanismXML")));

		PropertyResource node = invocation.getPropertyAsResource(ACTIVITY_URI.resolve("#node"));
		assertNotNull(node);
		assertEquals(ACTIVITY_URI.resolve("#SSHNode"), node.getTypeURI());

		assertEquals("127.0.0.1", node.getPropertyAsString(ACTIVITY_URI.resolve("#hostname")));

		assertEquals(22, node.getPropertyAsLiteral(ACTIVITY_URI.resolve("#port"))
				.getLiteralValueAsInt());
		assertEquals("/tmp/asdfasdf/", node.getPropertyAsString(ACTIVITY_URI.resolve("#directory")));
		assertEquals("/bin/ln -s %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%",
				node.getPropertyAsString(ACTIVITY_URI.resolve("#linkCommand")));
		assertEquals("/bin/cp %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%",
				node.getPropertyAsString(ACTIVITY_URI.resolve("#copyCommand")));

		// Processor explicitLocation
		proc = bundle.getMainWorkflow().getProcessors().getByName("explicitLocation");
		assertNotNull(proc);
		config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);

		resource = config.getJson();

		toolId = resource.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#toolId"));
		assertEquals(ACTIVITY_URI.resolve("#5dd1fdb0-df3c-4fce-a856-29b4d0ac67bb"), toolId);
		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		invocation = resource.getPropertyAsResource(ACTIVITY_URI.resolve("#invocation"));

		assertEquals(ACTIVITY_URI.resolve("#Invocation"), invocation.getTypeURI());

		assertFalse(invocation.hasProperty(DC.resolve("identifier")));

		assertEquals(ACTIVITY_URI.resolve("#ssh"),
				invocation.getPropertyAsResourceURI(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("asdfasdg",
				invocation.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));

		assertFalse(invocation.hasProperty(ACTIVITY_URI.resolve("#mechanismXML")));

		node = invocation.getPropertyAsResource(ACTIVITY_URI.resolve("#node"));
		assertNotNull(node);
		assertEquals(ACTIVITY_URI.resolve("#SSHNode"), node.getTypeURI());

		assertEquals("127.0.0.1", node.getPropertyAsString(ACTIVITY_URI.resolve("#hostname")));

		assertEquals(22, node.getPropertyAsLiteral(ACTIVITY_URI.resolve("#port"))
				.getLiteralValueAsInt());
		assertEquals("/tmp/asdfasdf/", node.getPropertyAsString(ACTIVITY_URI.resolve("#directory")));
		assertEquals("/bin/ln -s %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%",
				node.getPropertyAsString(ACTIVITY_URI.resolve("#linkCommand")));
		assertEquals("/bin/cp %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%",
				node.getPropertyAsString(ACTIVITY_URI.resolve("#copyCommand")));

	}

	@Test
	public void parse2_3_simple() throws Exception {
		URL wfResource = getClass().getResource(WF_2_3);
		assertNotNull("Could not find workflow " + WF_2_3, wfResource);
		WorkflowBundle bundle = parser.parseT2Flow(wfResource.openStream());

		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors().getByName("Tool");
		assertNotNull(proc);
		Configuration config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"), config.getType());
		PropertyResource resource = config.getJson();
		String toolId = resource.getPropertyAsString(ACTIVITY_URI.resolve("#toolId"));
		assertEquals("2cd545bf-64ae-4cda-84fc-8cfe2faed772", toolId);

		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		assertEquals("789663B8-DA91-428A-9F7D-B3F3DA185FD4",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("default local",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<localInvocation />\r\n",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismXML")));

		PropertyResource description = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#toolDescription"));
		assertEquals("someName",
				description.getPropertyAsString(DC.resolve("title")));
		assertEquals("some group", description.getPropertyAsString(ACTIVITY_URI.resolve("#group")));
		assertEquals("the description\n  goes here",
				description.getPropertyAsString(DC.resolve("description")));
		assertEquals("cat %%theString%% to %%stringReplace%% file ",
				description.getPropertyAsString(ACTIVITY_URI.resolve("#command")));
		assertEquals(1200,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#preparingTimeoutInSeconds"))
						.getLiteralValueAsInt());
		assertEquals(1800,
				description
						.getPropertyAsLiteral(ACTIVITY_URI.resolve("#executionTimeoutInSeconds"))
						.getLiteralValueAsInt());

		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#tag")).isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#runtimeEnvironment"))
				.isEmpty());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#queue")).isEmpty());

		assertEquals(false, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdIn"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdOut"))
				.getLiteralValueAsBoolean());
		assertEquals(true, description.getPropertyAsLiteral(ACTIVITY_URI.resolve("#includeStdErr"))
				.getLiteralValueAsBoolean());
		assertTrue(description.getProperties().get(ACTIVITY_URI.resolve("#validReturnCode"))
				.isEmpty());

		// Check ports
		SortedSet<PropertyResource> inputs = description.getPropertiesAsResources(ACTIVITY_URI
				.resolve("#inputs"));
		assertEquals(6, inputs.size());

		for (PropertyResource inputMap : inputs) {
			String port = inputMap.getPropertyAsString(ACTIVITY_URI.resolve("#port"));

			if (port.equals("file1")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("file1", input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("file2")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("anotherFile.txt",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("fileList")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("fileList", input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("fileList2")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("another", input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertTrue(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("stringReplaceWithDifferentPort")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("theString", input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("stringReplace")) {
				PropertyResource input = inputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#input"));
				assertEquals("stringReplace",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#tag")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						input.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#list"))
						.getLiteralValueAsBoolean());
				assertFalse(input.getPropertyAsLiteral(ACTIVITY_URI.resolve("#concatenate"))
						.getLiteralValueAsBoolean());
			}
		}

		SortedSet<PropertyResource> outputs = description.getPropertiesAsResources(ACTIVITY_URI
				.resolve("#outputs"));
		assertEquals(2, outputs.size());

		for (PropertyResource outputMap : outputs) {
			String port = outputMap.getPropertyAsString(ACTIVITY_URI.resolve("#port"));

			if (port.equals("out1")) {
				PropertyResource output = outputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#output"));
				assertEquals("out1", output.getPropertyAsString(ACTIVITY_URI.resolve("#path")));
				assertFalse(output.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
			} else if (port.equals("out2")) {
				PropertyResource output = outputMap.getPropertyAsResource(ACTIVITY_URI
						.resolve("#output"));
				assertEquals("../different/path/ and spaces.txt",
						output.getPropertyAsString(ACTIVITY_URI.resolve("#path")));
				assertTrue(output.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
			}
		}

		SortedSet<PropertyResource> staticInputs = description
				.getPropertiesAsResources(ACTIVITY_URI.resolve("#staticInputs"));
		assertEquals(2, staticInputs.size());
		for (PropertyResource staticInput : staticInputs) {
			String tag = staticInput.getPropertyAsString(ACTIVITY_URI.resolve("#tag"));
			if (tag.equals("thefile.txt")) {
				assertTrue(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						staticInput.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertEquals("A multi\n   line\n     string inserted here. \u0192(x).",
						staticInput.getPropertyAsString(ACTIVITY_URI.resolve("#content")));
			} else if (tag.equals("downloaded.zip")) {
				assertTrue(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#file"))
						.getLiteralValueAsBoolean());
				assertFalse(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#tempFile"))
						.getLiteralValueAsBoolean());
				assertFalse(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#binary"))
						.getLiteralValueAsBoolean());
				assertEquals("windows-1252",
						staticInput.getPropertyAsString(ACTIVITY_URI.resolve("#charsetName")));
				assertFalse(staticInput.getPropertyAsLiteral(ACTIVITY_URI.resolve("#forceCopy"))
						.getLiteralValueAsBoolean());
				assertEquals("http://example.com/download#strange",
						staticInput.getPropertyAsString(ACTIVITY_URI.resolve("#url")));
			} else {
				fail("Unexpected tag " + tag);
			}
		}

		// Processor symbolicLocation
		proc = bundle.getMainWorkflow().getProcessors().getByName("symbolicLocation");
		assertNotNull(proc);
		config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);

		resource = config.getJson();

		toolId = resource.getPropertyAsString(ACTIVITY_URI.resolve("#toolId"));
		assertEquals("5dd1fdb0-df3c-4fce-a856-29b4d0ac67bb", toolId);
		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		PropertyResource group = resource.getPropertyAsResource(ACTIVITY_URI
				.resolve("#invocationGroup"));

		assertEquals(ACTIVITY_URI.resolve("#InvocationGroup"), group.getTypeURI());

		assertEquals("asdfsadf", group.getPropertyAsString(ACTIVITY_URI.resolve("name")));
		assertEquals("D0A4CDEB-DD10-4A8E-A49C-8871003083D8",
				group.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("asdfasdg", group.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
				"<sshInvocation><sshNode><host>127.0.0.1</host><port>22</port><directory>/tmp/asdfasdf/</directory><linkCommand>/bin/ln -s %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%</linkCommand><copyCommand>/bin/cp %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%</copyCommand></sshNode></sshInvocation>\r\n",
				group.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismXML")));

		// Processor explicitLocation
		proc = bundle.getMainWorkflow().getProcessors().getByName("explicitLocation");
		assertNotNull(proc);
		config = scufl2Tools.configurationForActivityBoundToProcessor(proc, profile);

		resource = config.getJson();

		toolId = resource.getPropertyAsString(ACTIVITY_URI.resolve("#toolId"));
		assertEquals("5dd1fdb0-df3c-4fce-a856-29b4d0ac67bb", toolId);
		assertFalse(resource.hasProperty(ACTIVITY_URI.resolve("#edited")));

		assertEquals("D0A4CDEB-DD10-4A8E-A49C-8871003083D8",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismType")));
		assertEquals("asdfasdg",
				resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismName")));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
				"<sshInvocation><sshNode><host>127.0.0.1</host><port>22</port><directory>/tmp/asdfasdf/</directory><linkCommand>/bin/ln -s %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%</linkCommand><copyCommand>/bin/cp %%PATH_TO_ORIGINAL%% %%TARGET_NAME%%</copyCommand></sshNode></sshInvocation>\r\n" +
				"", resource.getPropertyAsString(ACTIVITY_URI.resolve("#mechanismXML")));

	}

}
