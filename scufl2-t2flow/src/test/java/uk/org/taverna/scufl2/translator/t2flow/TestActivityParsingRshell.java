package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser.PORT_DEFINITION;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.Before;
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
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.RshellActivityParser;

public class TestActivityParsingRshell {

	private static final String WF_RSHELL_2_2 = "/rshell-2-2.t2flow";
	private static final String WF_RSHELL_2_3 = "/rshell-2-3.t2flow";
	private static final String WF_RSHELL_SIMPLE_2_3 = "/rshell-simple-2-3.t2flow";

	private static final String WF_ALL_ACTIVITIES = "/defaultActivitiesTaverna2.2.t2flow";
	
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private T2FlowParser parser;

	@Before
	public void makeParser() throws JAXBException {
		parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		
	}
	
	@Test
	public void parseRShell22WithReferences() throws Exception {
		URL wfResource = getClass().getResource(WF_RSHELL_2_2);
		assertNotNull("Could not find workflow " + WF_RSHELL_2_2, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		// TODO: Check data types defined (semantic types)
		
	}
	

	@Test
	public void parseRShell23() throws Exception {
		URL wfResource = getClass().getResource(WF_RSHELL_2_3);
		assertNotNull("Could not find workflow " + WF_RSHELL_2_3, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		
		// TODO: Check data types defined (semantic types)
	}
	


	@Test
	public void parseRShellAllActiv() throws Exception {
		URL wfResource = getClass().getResource(WF_ALL_ACTIVITIES);
		assertNotNull("Could not find workflow " + WF_ALL_ACTIVITIES, wfResource);
		parser.setStrict(false); // Ignore other broken activities
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		
	}
		
	@Test
	public void parseSimpleRShellScript() throws Exception {
		URL wfResource = getClass().getResource(WF_RSHELL_SIMPLE_2_3);
		assertNotNull("Could not find workflow " + WF_RSHELL_SIMPLE_2_3, wfResource);
		WorkflowBundle bundle = parser
				.parseT2Flow(wfResource.openStream());
		Profile profile = bundle.getMainProfile();
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("Rshell");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
				
		Activity activity = (Activity) config.getConfigures();
		assertEquals(RshellActivityParser.ACTIVITY_URI,
				activity.getConfigurableType());
		assertEquals(RshellActivityParser.ACTIVITY_URI.resolve("#Config"), config
				.getPropertyResource().getTypeURI());
		String script = config.getPropertyResource().getPropertyAsString(
				RshellActivityParser.ACTIVITY_URI.resolve("#script"));
		assertEquals("too\nsimple", script);

		Set<String> expectedInputs = new HashSet<String>(Arrays.asList(
				"in1", "in2", "in3"));
		assertEquals(expectedInputs, activity.getInputPorts().getNames());
		InputActivityPort in1 = activity.getInputPorts().getByName("in1");
		assertEquals(0, in1.getDepth().intValue());
		InputActivityPort in2 = activity.getInputPorts().getByName("in2");
		assertEquals(0, in2.getDepth().intValue());
		InputActivityPort in3 = activity.getInputPorts().getByName("in3");
		assertEquals(0, in3.getDepth().intValue());

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
		
		Map<URI, URI> dataTypes = new HashMap<URI, URI>();
		
		for (PropertyResource portDef : inputDef) {
			assertEquals(PORT_DEFINITION.resolve("#InputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesInputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
			
			URI dataType = portDef.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType"));
			assertEquals(RshellActivityParser.ACTIVITY_URI.resolve("#samePrefix"), dataType.resolve("#samePrefix"));			
			// For instance http://ns.taverna.org.uk/2010/activity/rshell#BOOL_LIST
			dataTypes.put(portURI, dataType);
			
		}


		Set<String> expectedOutputs = new HashSet<String>(
				Arrays.asList("out1", "out2", "out3"));
		assertEquals(expectedOutputs, activity.getOutputPorts().getNames());
		OutputActivityPort out1 = activity.getOutputPorts().getByName("out1");
		assertEquals(0, out1.getDepth().intValue());
		OutputActivityPort out2 = activity.getOutputPorts().getByName("out2");
		assertEquals(1, out2.getDepth().intValue());
		OutputActivityPort out3 = activity.getOutputPorts().getByName("out3");
		assertEquals(1, out3.getDepth().intValue());

		expectedPortUris.clear();
		for (OutputActivityPort inPort : activity.getOutputPorts()) {
			expectedPortUris.add(new URITools().relativeUriForBean(inPort,
					config));
		}
		
		Set<PropertyResource> outputDef = config.getPropertyResource()
				.getPropertiesAsResources(
						PORT_DEFINITION.resolve("#outputPortDefinition"));
		assertEquals(3, outputDef.size());
		for (PropertyResource portDef : outputDef) {
			assertEquals(PORT_DEFINITION.resolve("#OutputPortDefinition"),
					portDef.getTypeURI());
			assertNull(portDef.getResourceURI());
			URI portURI = portDef.getPropertyAsResourceURI(PORT_DEFINITION
					.resolve("#definesOutputPort"));
			assertTrue("Unknown port " + portURI,
					expectedPortUris.contains(portURI));
			
			URI dataType = portDef.getPropertyAsResourceURI(PORT_DEFINITION.resolve("#dataType"));
			assertEquals(RshellActivityParser.ACTIVITY_URI.resolve("#samePrefix"), dataType.resolve("#samePrefix"));			
			// For instance http://ns.taverna.org.uk/2010/activity/rshell#BOOL_LIST
			dataTypes.put(portURI, dataType);
			
		}


		System.out.println(dataTypes);
		
		

		PropertyResource connection = config.getPropertyResource().getPropertyAsResource(RshellActivityParser.ACTIVITY_URI.resolve("#connection"));
		assertEquals(RshellActivityParser.ACTIVITY_URI.resolve("#Connection"), connection.getTypeURI());

		assertEquals("localhost", connection.getPropertyAsString(RshellActivityParser.ACTIVITY_URI.resolve("#hostname")));
		
		PropertyLiteral portLiteral = connection.getPropertyAsLiteral(RshellActivityParser.ACTIVITY_URI.resolve("#port"));
		assertEquals(6311, portLiteral.getLiteralValueAsInt());
		assertEquals(PropertyLiteral.XSD_UNSIGNEDSHORT, portLiteral.getLiteralType());

		assertEquals(false, connection.getPropertyAsLiteral(RshellActivityParser.ACTIVITY_URI.resolve("#keepSessionAlive")).getLiteralValueAsBoolean());
		
		
		
		
	}

}
