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
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;

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
		checkT2Parsers();
	}
	
	private void checkT2Parsers() {
		for (T2Parser t2Parser : parser.getT2Parsers()) {
			if (t2Parser instanceof RESTActivityParser) {
				return;
			}
		}
		fail("Could not find REST activity parser, found " + parser.getT2Parsers());		
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
		assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
		// A sub-class of HTTP_URI.resolve("#Request")
		
		URI toolId = request.getPropertyAsResourceURI(
				HTTP_URI.resolve("#mthd"));
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
			String value;
			if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
				value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
			} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
				assertEquals(true, 
						reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
				value = "--use100Continue--";
			} else {
				value = "--undefinedValue--";
			}
			foundHeaders.put(fieldName, value); 
			assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
		}
		assertEquals("text/plain", foundHeaders.get("Accept"));
		// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
		assertFalse(foundHeaders.containsKey("Content-Type"));
		assertFalse(foundHeaders.containsKey("Expect"));
		//assertEquals("application/zip", foundHeaders.get("Content-Type"));
		// assertEquals("--use100Continue--", foundHeaders.get("Expect"))
		
	
		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
		//assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
		//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));

				
	}
	
}
