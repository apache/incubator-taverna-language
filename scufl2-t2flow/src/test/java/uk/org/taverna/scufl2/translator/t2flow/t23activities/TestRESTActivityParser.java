package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.ACTIVITY_URI;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.HTTP_METHODS_URI;
import static uk.org.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.HTTP_URI;

import java.net.URI;
import java.net.URL;
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
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestRESTActivityParser {

	private static final String WF_2_2 = "/rest-2-2.t2flow";
	private static final String WF_2_3 = "/rest-2-3.t2flow";
	private static final String WF_2_2_RESAVED_2_3 = "/rest-2-2-resaved-2-3.t2flow";
	private T2FlowParser parser;
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

	private static URITools uriTools = new URITools();

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
		//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
		// [default, post, put]
		Processor proc = bundle.getMainWorkflow().getProcessors()
				.getByName("default");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"), 
				config.getConfigurableType());
		
		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, activity.getConfigurableType());
		
		PropertyResource configResource = config.getPropertyResource();
		PropertyResource request = configResource.getPropertyAsResource(
				ACTIVITY_URI.resolve("#request"));
		
		URI toolId = request.getPropertyAsResourceURI(
				HTTP_URI.resolve("#mthd")); //
		assertEquals(HTTP_METHODS_URI.resolve("#GET"), 
				toolId);
		
		String urlSignature = request.getPropertyAsString(
				ACTIVITY_URI.resolve("#absoluteURITemplate"));		
		assertEquals("http://www.myexperiment.org/user.xml?id={userID}", urlSignature);
		
		Map<String, String> foundHeaders = new HashMap<String, String>();
		PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
		for (PropertyObject header : headers) {
			PropertyResource reqHeader = (PropertyResource) header;
			String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
			String value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
			foundHeaders.put(fieldName, value);
		}
		assertEquals("text/plain", foundHeaders.get(HTTP_METHODS_URI.resolve("#accept")));
		//assertEquals("application/zip", foundHeaders.get(HTTP_METHODS_URI.resolve("#content-type")));
		
		assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
		assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#sendHTTPExpectRequestHeader")).getLiteralValueAsBoolean());
		assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
		
		// Not much more to check as 2.2 does not include tool description
				
	}
	
}
