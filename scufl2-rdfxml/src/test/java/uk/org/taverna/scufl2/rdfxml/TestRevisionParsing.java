package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

import uk.org.taverna.scufl2.api.annotation.Revision;

public class TestRevisionParsing {
	
	private static final String ROEVO_TEST_XML = "/roevo-test.xml";
	RevisionParser parser = new RevisionParser();
	
	@Test
	public void parseTestXML() throws Exception {
		URL base = getClass().getResource(ROEVO_TEST_XML);
		assertNotNull("Could not find " + ROEVO_TEST_XML, base);
		InputStream inStream = base.openStream();
		Revision r = parser.readRevisionChain(inStream, base.toURI());
		assertNotNull("Did not return a Revision", r);
		assertEquals("http://example.com/test/v3", r.getIdentifier().toASCIIString());
	}
}
