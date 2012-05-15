package uk.org.taverna.scufl2.api.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.Test;

import uk.org.taverna.scufl2.api.property.PropertyResource;

public class TestRevision {

	URI example = URI.create("http://example.com/");
	URI r0 = example.resolve("r0");
	URI r1 = example.resolve("r1");
	URI r2 = example.resolve("r2");

	@Test
	public void constructorDefault() throws Exception {
		Revision rev = new Revision();
		assertTrue(rev.getProperties().isEmpty());
		assertNull(rev.getResourceURI());
		assertEquals(URI
				.create("http://www.w3.org/ns/prov#Entity"), rev.getTypeURI());
	}

	@Test
	public void constructorUri() throws Exception {
		Revision rev = new Revision(r1);
		assertTrue(rev.getProperties().isEmpty());
		assertEquals(r1, rev.getResourceURI());
		assertEquals(URI
				.create("http://www.w3.org/ns/prov#Entity"), rev.getTypeURI());
	}

	@Test
	public void constructorUriPrevious() throws Exception {
		Revision rev1 = new Revision(r1);
		Revision rev2 = new Revision(r2, rev1);
		assertEquals(1, rev2.getProperties().size());
		assertEquals(rev1, rev2.getPreviousRevision());
		assertEquals(rev1, rev2.getProperty(URI
				.create("http://www.w3.org/ns/prov#wasRevisionOf")));
		assertEquals(URI
				.create("http://www.w3.org/ns/prov#Entity"), rev2.getTypeURI());

	}
	
	@Test
	public void getPreviousFromReference() throws Exception {
		Revision rev2 = new Revision(r2);		
		rev2.addPropertyReference(URI
				.create("http://www.w3.org/ns/prov#wasRevisionOf"), r1);
		Revision rev1 = rev2.getPreviousRevision();		
		assertEquals(r1, rev1.getResourceURI());
		assertEquals(URI
				.create("http://www.w3.org/ns/prov#Entity"), rev1.getTypeURI());
	}
	
	@Test
	public void getPreviousFromResource() throws Exception {
		Revision rev2 = new Revision(r2);		
		PropertyResource custom = rev2.addPropertyAsNewResource(URI
				.create("http://www.w3.org/ns/prov#wasRevisionOf"), example.resolve("custom"));
		custom.setResourceURI(r1);
		custom.addPropertyAsString(URI.create("http://purl.org/dc/terms/description"), "Custom revision");
		Revision rev1 = rev2.getPreviousRevision();
		assertEquals(r1, rev1.getResourceURI());
		assertEquals(example.resolve("custom"), rev1.getTypeURI());
		assertEquals("Custom revision", rev1.getPropertyAsString(URI.create("http://purl.org/dc/terms/description")));
	}
	
	

	@Test
	public void setPrevious() throws Exception {
		Revision rev1 = new Revision(r1);
		Revision rev2 = new Revision(r2);
		rev2.setPreviousRevision(rev1);
		assertEquals(1, rev2.getProperties().size());
		assertEquals(rev1, rev2.getPreviousRevision());
		assertEquals(rev1, rev2.getProperty(URI
				.create("http://www.w3.org/ns/prov#wasRevisionOf")));

	}

	@Test
	public void setPreviousAgain() throws Exception {
		Revision rev0 = new Revision(r0);
		Revision rev1 = new Revision(r1);
		Revision rev2 = new Revision(r2, rev1);
		rev2.setPreviousRevision(rev0);
		assertEquals(1, rev2.getProperties().size());
		assertEquals(rev0, rev2.getPreviousRevision());
		assertEquals(rev0, rev2.getProperty(URI
				.create("http://www.w3.org/ns/prov#wasRevisionOf")));
	}

	@Test
	public void created() throws Exception {
		Revision rev1 = new Revision(r1);
		GregorianCalendar now = new GregorianCalendar();
		rev1.setCreated(now);
		
		assertEquals(now, rev1.getCreated());
		assertEquals(1, rev1.getProperties().size());
		PropertyResource generation = rev1.getPropertyAsResource(URI
				.create("http://www.w3.org/ns/prov#qualifiedGeneration"));
		assertEquals(URI
				.create("http://www.w3.org/ns/prov#Generation"), generation.getTypeURI());
		//assertNull(generation.getResourceURI());
		assertEquals(1, generation.getProperties().size());
		assertEquals(now, generation.getPropertyAsLiteral(URI
				.create("http://www.w3.org/ns/prov#atTime")).getLiteralValueAsCalendar());
	}

	@Test
	public void createdAgain() throws Exception {
		Revision rev1 = new Revision(r1);
		GregorianCalendar first = new GregorianCalendar();
		
		rev1.setCreated(first);
		PropertyResource generation = rev1.getPropertyAsResource(URI
				.create("http://www.w3.org/ns/prov#qualifiedGeneration"));

		GregorianCalendar again = new GregorianCalendar();
		rev1.setCreated(again);		
		assertEquals(1, generation.getProperties().size());
		
		// Should have kept the generation
		assertEquals(generation, rev1.getPropertyAsResource(URI
				.create("http://www.w3.org/ns/prov#qualifiedGeneration")));
		// But replaced the literal		
		assertEquals(again, generation.getPropertyAsLiteral(URI
				.create("http://www.w3.org/ns/prov#atTime")).getLiteralValueAsCalendar());
	}

	
	
	@Test
	public void addCreator() throws Exception {
		Revision rev1 = new Revision(r1);
		URI alice = example.resolve("alice");
		rev1.addCreator(alice);
		assertEquals(Collections.singleton(alice), rev1.getCreators());
		assertEquals(1, rev1.getProperties().size());
		assertEquals(alice, rev1.getPropertyAsResourceURI(URI
				.create("http://www.w3.org/ns/prov#wasAttributedTo")));
	}

	@Test
	public void multipleAddCreator() throws Exception {
		Revision rev1 = new Revision(r1);
		URI alice = example.resolve("alice");
		URI bob = example.resolve("bob");
		rev1.addCreator(alice);
		rev1.addCreator(bob);
		HashSet<URI> aliceAndBob = new HashSet<URI>(Arrays.asList(alice, bob));
		assertEquals(aliceAndBob, rev1.getCreators());
		assertEquals(1, rev1.getProperties().size());
		assertEquals(aliceAndBob, rev1.getPropertiesAsResourceURIs((URI
				.create("http://www.w3.org/ns/prov#wasAttributedTo"))));
	}

	@Test
	public void setCreators() throws Exception {
		Revision rev1 = new Revision(r1);
		URI alice = example.resolve("alice");
		URI bob = example.resolve("bob");
		HashSet<URI> aliceAndBob = new HashSet<URI>(Arrays.asList(alice, bob));
		rev1.setCreators(aliceAndBob);
		assertEquals(aliceAndBob, rev1.getCreators());		
		assertEquals(aliceAndBob, rev1.getPropertiesAsResourceURIs((URI
				.create("http://www.w3.org/ns/prov#wasAttributedTo"))));
	}

	@Test
	public void resetCreators() throws Exception {
		Revision rev1 = new Revision(r1);
		URI alice = example.resolve("alice");
		rev1.addCreator(alice);

		URI bob = example.resolve("bob");
		HashSet<URI> justBob = new HashSet<URI>(Arrays.asList(bob));
		rev1.setCreators(justBob);
		assertEquals(justBob, rev1.getCreators());		
		assertEquals(bob, rev1.getPropertyAsResourceURI((URI
				.create("http://www.w3.org/ns/prov#wasAttributedTo"))));
	}

}
