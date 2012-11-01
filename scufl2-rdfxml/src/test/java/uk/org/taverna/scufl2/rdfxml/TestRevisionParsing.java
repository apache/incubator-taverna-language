package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

import uk.org.taverna.scufl2.api.annotation.Revision;

public class TestRevisionParsing {
	
	private static final String ROEVO_TEST_XML = "roevo-test.xml";
	RevisionParser parser = new RevisionParser();
	
	@Test
	public void parseTestXML() throws Exception {
		URL base = getClass().getResource(ROEVO_TEST_XML);
		assertNotNull("Could not find " + ROEVO_TEST_XML, base);
		InputStream inStream = base.openStream();
		Revision r3 = parser.readRevisionChain(inStream, base.toURI());
		assertNotNull("Did not return a Revision", r3);
		assertEquals("http://example.com/test/v3", r3.getIdentifier().toASCIIString());	
		
		// WARNING: java.util.GregorianCalendar is the most broken Calendar API I've ever come across.		
		GregorianCalendar expectedTime = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));
		// 1) Months (but only months) start at 0, not 1!
		expectedTime.set(2012, 12-1, 24, 18, 0, 0);
		// 2) Even if you set the time explicitly, we have to set the millis as well
		// otherwise it would use the millisecond value at the point of construction (!!)
		expectedTime.set(Calendar.MILLISECOND, 0);
		// 3) Still they don't actually compare equal, even if they are at the point in time in
		// milliseconds since epochs
		assertEquals(expectedTime.getTimeInMillis(), r3.getGeneratedAtTime().getTimeInMillis());
		
		Revision r2 = r3.getPreviousRevision();
		assertEquals("http://example.com/test/v2", r2.getIdentifier().toASCIIString());	
		
	}
}
