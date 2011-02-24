package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

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
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class TestPropertyParsing {
	private static final String PROFILE_RDF = "megaProfile.rdf";

	protected ProfileParser profileParser = new ProfileParser();

	private URL profileUrl;

	private WorkflowBundle bundle;

	private Profile profile;

	@Test
	public void activity() throws Exception {
		assertEquals(1, profile.getActivities().size());
		Activity helloScript = profile.getActivities().getByName("HelloScript");
		assertEquals("HelloScript", helloScript.getName());
		assertEquals(
				"http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Activity",
				helloScript.getConfigurableType().toASCIIString());
		assertEquals(1, helloScript.getInputPorts().size());
		InputActivityPort personName = helloScript.getInputPorts().getByName(
				"personName");
		assertEquals("personName", personName.getName());
		assertEquals(0, personName.getDepth().intValue());

		assertEquals(1, helloScript.getOutputPorts().size());
		OutputActivityPort hello = helloScript.getOutputPorts().getByName(
				"hello");
		assertEquals("hello", hello.getName());
		assertEquals(0, hello.getDepth().intValue());
		assertEquals(0, hello.getGranularDepth().intValue());

	}

	@Test
	public void configuration() throws Exception {
		assertEquals(1, profile.getConfigurations().size());
		Configuration hello = profile.getConfigurations().getByName("Hello");
		assertEquals("Hello", hello.getName());
		assertEquals(profile.getActivities().getByName("HelloScript"),
				hello.getConfigures());
		assertEquals(
				"http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Configuration",
				hello.getConfigurableType().toASCIIString());
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
		Configuration hello = profile.getConfigurations().getByName("Hello");
		PropertyResource propResource = hello.getPropertyResource();
		assertEquals(
				"http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Configuration",
				propResource.getTypeURI().toASCIIString());
		assertNull(propResource.getResourceURI());
		assertEquals(1, propResource.getProperties().size());
		URI scriptUri = URI
				.create("http://ns.taverna.org.uk/2010/taverna/activities/beanshell#script");
		String script = propResource.getPropertyAsString(scriptUri);
		assertEquals("hello = \"Hello, \" + personName;\n"
				+ "JOptionPane.showMessageDialog(null, hello);", script);
		PropertyLiteral literal = propResource.getPropertyAsLiteral(scriptUri);
		assertEquals(PropertyLiteral.XSD_STRING, literal.getLiteralType());
	}

	@Before
	public void readProfile() throws Exception {
		loadProfileDocument();
		prepareParserState();
		profileParser.readProfile(URI.create("/profile/tavernaWorkbench/"),
				URI.create("profile/tavernaWorkbench.rdf"),
				profileUrl.openStream());
		profile = bundle.getProfiles().getByName("tavernaWorkbench");
		assertNotNull(profile);
	}

}
