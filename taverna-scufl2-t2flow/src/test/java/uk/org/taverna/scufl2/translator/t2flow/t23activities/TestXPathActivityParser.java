package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestXPathActivityParser {

	private static final String XPATH_WORKFLOW_SANS_EXAMPLE_XML = "/xpath_workflow.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	@Test
	public void parseXPathActivityWorkflow() throws Exception {
		URL wfResource = getClass().getResource(XPATH_WORKFLOW_SANS_EXAMPLE_XML);
		assertNotNull("Could not find workflow " + XPATH_WORKFLOW_SANS_EXAMPLE_XML, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(false);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Profile profile = wfBundle.getMainProfile();
		//XPath_height has missing xmlDocument from its configuration
		Processor heightProc = wfBundle.getMainWorkflow().getProcessors().getByName("XPath_height");
		ObjectNode heightConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(heightProc, profile).getJsonAsObjectNode();
		assertNotNull(heightConfig);
		assertEquals("//height/text()",heightConfig.get("xpathExpression").textValue());
		assertFalse(heightConfig.has("exampleXmlDocument"));
		//XPath_width has xmlDocument
		Processor widthProc = wfBundle.getMainWorkflow().getProcessors().getByName("XPath_width");
		ObjectNode widthConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(widthProc, profile).getJsonAsObjectNode();		
		assertNotNull(widthConfig);		
		assertEquals("//width/text()",widthConfig.get("xpathExpression").asText());		
		assertTrue(widthConfig.has("exampleXmlDocument"));		
	}
	
}