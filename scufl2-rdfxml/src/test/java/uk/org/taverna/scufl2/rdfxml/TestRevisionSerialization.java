package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import uk.org.taverna.scufl2.api.annotation.Revision;

public class TestRevisionSerialization {

	private static final String PROV = "http://www.w3.org/ns/prov#";
	protected URI example = URI.create("http://example.com/");
	protected URI r0 = example.resolve("r0");
	protected URI r1 = example.resolve("r1");
	protected URI r2 = example.resolve("r2");
	protected URI r3 = example.resolve("r3");

	private PropertyResourceSerialiser serialiser;
	private Revision rev1;
	private Revision rev2;
	private Revision rev3;
	private GregorianCalendar now;

	@Before
	public void makeVisitor() {
		serialiser = new PropertyResourceSerialiser(URI.create("/"));
	}

	@Before
	public void makePropResource() {
		rev1 = new Revision(r1);
		rev1.setPreviousRevision(r0);
		rev2 = new Revision(r2, rev1);
		rev3 = new Revision(r3, rev2);
		rev3.addCreator(URI.create("http://example.com/bob"));
		now = new GregorianCalendar();
		rev3.setCreated(now);
	}

	@Test
	public void serializingRevision() throws Exception {
		rev3.accept(serialiser);
		Element rev3Entity = serialiser.getRootElement();
		assertEquals(r3.toASCIIString(),
				rev3Entity.getAttributeNS(PropertyResourceSerialiser.RDF, "about"));
		assertEquals(3, rev3Entity.getChildNodes().getLength());

		assertEquals(PROV, rev3Entity.getNamespaceURI());
		assertEquals("Entity", rev3Entity.getTagName());
		
		Element wasRevisionOf = (Element) rev3Entity.getElementsByTagNameNS(PROV,
				"wasRevisionOf").item(0);
		Element rev2Entity = (Element) wasRevisionOf.getChildNodes().item(0);
		assertEquals(PROV, rev2Entity.getNamespaceURI());
		assertEquals("Entity", rev2Entity.getTagName());
		assertEquals(r2.toASCIIString(),
				rev2Entity.getAttributeNS(PropertyResourceSerialiser.RDF, "about"));
		
		Element wasRevisionOf2 = (Element) rev2Entity.getElementsByTagNameNS(PROV,
				"wasRevisionOf").item(0);
		Element rev1Entity = (Element) wasRevisionOf2.getChildNodes().item(0);
		assertEquals(PROV, rev1Entity.getNamespaceURI());
		assertEquals("Entity", rev1Entity.getTagName());
		assertEquals(r1.toASCIIString(),
				rev1Entity.getAttributeNS(PropertyResourceSerialiser.RDF, "about"));
		
		
		Element qualifiedGeneration = (Element) rev3Entity.getElementsByTagNameNS(PROV,
				"qualifiedGeneration").item(0);
		assertEquals(0, qualifiedGeneration.getAttributes().getLength());
		assertEquals(1, qualifiedGeneration.getChildNodes().getLength());
		Element atTime = (Element) qualifiedGeneration.getChildNodes().item(0).getChildNodes().item(0);
		assertEquals("atTime", atTime.getTagName());
		
		
		
	}

}
