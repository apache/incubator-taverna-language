package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class TestPropertyParsing {
	private static final String PROFILE_RDF = "megaProfile.rdf";

	protected ProfileParser profileParser = new ProfileParser();

	private URL profileUrl;

	private WorkflowBundle bundle;

	private Profile profile;

	@Test
	public void activity() throws Exception {
		assertEquals(17, profile.getActivities().size());
		Activity contentList = profile.getActivities()
				.getByName("Content_list");
		assertEquals("Content_list", contentList.getName());
		assertEquals("http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				contentList.getConfigurableType().toASCIIString());
		assertEquals(1, contentList.getInputPorts().size());
		InputActivityPort personName = contentList.getInputPorts().getByName(
				"WSArrayofData");
		assertEquals("WSArrayofData", personName.getName());
		assertEquals(1, personName.getDepth().intValue());

		assertEquals(1, contentList.getOutputPorts().size());
		OutputActivityPort hello = contentList.getOutputPorts().getByName(
				"output");
		assertEquals("output", hello.getName());
		assertEquals(0, hello.getDepth().intValue());
		assertEquals(0, hello.getGranularDepth().intValue());

	}

	@Test
	public void configuration() throws Exception {
		assertEquals(17, profile.getConfigurations().size());
		Configuration get_XML_result = profile.getConfigurations().getByName(
				"Get_XML_result");
		assertEquals("Get_XML_result", get_XML_result.getName());
		assertEquals(profile.getActivities().getByName("Get_XML_result"),
				get_XML_result.getConfigures());
		assertEquals("http://ns.taverna.org.uk/2010/activity/wsdl#Config",
				get_XML_result.getConfigurableType().toASCIIString());
	}

	public void loadProfileDocument() {
		profileUrl = getClass().getResource(PROFILE_RDF);
		assertNotNull("Could not find profile document " + PROFILE_RDF,
				profileUrl);
	}

	@Test
	public void parserStackEmpty() throws Exception {
		Stack<WorkflowBean> stack = profileParser.getParserState().getStack();
		assertEquals(1, stack.size());
		assertEquals(bundle, stack.peek());
	}

	public void prepareParserState() throws URISyntaxException {
		bundle = new ExampleWorkflow().makeWorkflowBundle();
		bundle.getProfiles().clear();
		bundle.setMainProfile(null);

		profileParser.getParserState().setLocation(URI.create("/"));
		profileParser.getParserState().push(bundle);
	}

	@Test
	public void propertyResource() throws Exception {
		Configuration get_XML_result = profile.getConfigurations().getByName(
				"Get_XML_result");
		PropertyResource propResource = get_XML_result.getPropertyResource();
		assertEquals("http://ns.taverna.org.uk/2010/activity/wsdl#Config",
				propResource.getTypeURI().toASCIIString());
		assertNull(propResource.getResourceURI());
		assertEquals(1, propResource.getProperties().size());
		URI wsdlOperation = URI
				.create("http://ns.taverna.org.uk/2010/activity/wsdl#operation");
		PropertyResource operation = (PropertyResource) propResource
				.getProperty(wsdlOperation);

		URI wsdl = URI
				.create("http://ns.taverna.org.uk/2010/activity/wsdl/operation");
		assertEquals(wsdl, operation.getTypeURI());
		assertEquals("poll",
				operation.getPropertyAsString(wsdl.resolve("#name")));
		assertEquals(
				"http://www.ebi.ac.uk/Tools/webservices/wsdl/WSInterProScan.wsdl",
				operation.getPropertyAsReference(wsdl.resolve("#wsdl"))
						.getResourceURI().toASCIIString());

	}

	@Before
	public void readProfile() throws Exception {
		loadProfileDocument();
		prepareParserState();
		profileParser.readProfile(URI.create("/profile/tavernaWorkbench/"),
				URI.create("profile/tavernaWorkbench.rdf"),
				profileUrl.openStream());
		profile = bundle.getProfiles().getByName("taverna-2.2.0");
		assertNotNull(profile);
	}

	@Test
	public void xmlOutput() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		new RDFXMLSerializer(bundle).profileDoc(output, profile,
				URI.create("profile/profile.rdf"));
		String profileStr = new String(output.toByteArray(), "UTF-8");

		String expectedProfile = IOUtils.toString(profileUrl.openStream(),
				"UTF-8");
		assertEquals(expectedProfile, profileStr);
		// System.out.println(profileStr);

	}

}
