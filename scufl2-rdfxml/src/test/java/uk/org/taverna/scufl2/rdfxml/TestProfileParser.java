package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestProfileParser {
	private static final String PROFILE_RDF = "example/profile/tavernaWorkbench.rdf";

	protected ProfileParser profileParser = new ProfileParser();

	private URL profileUrl;

	private WorkflowBundle bundle;

	@Before
	public void loadProfileDocument() {
		profileUrl = getClass().getResource(PROFILE_RDF);
		assertNotNull("Could not find profile document " + PROFILE_RDF,
				profileUrl);
	}


	@Before
	public void prepareParserState() throws URISyntaxException {
		bundle = new ExampleWorkflow().makeWorkflowBundle();
		bundle.getProfiles().clear();
		bundle.setMainProfile(null);

		profileParser.getParserState().setLocation(URI.create("/"));
		profileParser.getParserState().push(bundle);
	}

	@Test
	public void testname() throws Exception {
		profileParser.readProfile(URI.create("/profile/tavernaWorkbench/"),
				URI.create("profile/tavernaWorkbench.rdf"),
				profileUrl.openStream());
	}
}
