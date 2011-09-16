package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class TestPropertySerialisation {

	private PropertyResourceSerialiser serialiser;
	private PropertyResource propResource;

	private URI property = URI.create("http://example.com/property");

	@Before
	public void makeVisitor() {
		serialiser = new PropertyResourceSerialiser(URI.create("/"));
	}

	@Before
	public void makePropResource() {
		propResource = new PropertyResource();
	}

	@Test
	public void reference() throws Exception {
		propResource.addPropertyReference(property.resolve("#reference"),
				property.resolve("#object"));

		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);
		assertEquals("reference", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(0, elem.getChildNodes().getLength());
		assertEquals("http://example.com/property#object",
				elem.getAttributeNS(PropertyResourceSerialiser.RDF, "resource"));
	}

	@Test
	public void twoReferences() throws Exception {
		propResource.addPropertyReference(property.resolve("#reference"),
				property.resolve("#object"));
		propResource.addPropertyReference(property.resolve("#reference"),
				property.resolve("#secondObject"));

		propResource.accept(serialiser);

		Set<String> values = new HashSet<String>();

		Element rootElem = serialiser.getRootElement();		

		for (int i = 0; i < rootElem.getChildNodes().getLength(); i++) {
			Element elem = (Element) rootElem.getChildNodes().item(i);
			assertEquals("reference", elem.getTagName());
			assertEquals("http://example.com/property#", elem.getNamespaceURI());
			assertEquals(1, elem.getAttributes().getLength());
			assertEquals(0, elem.getChildNodes().getLength());
			values.add(elem.getAttributeNS(PropertyResourceSerialiser.RDF,
					"resource"));
		}

		Set<String> expectedValues = new HashSet<String>();
		expectedValues.add("http://example.com/property#object");
		expectedValues.add("http://example.com/property#secondObject");
		assertEquals(expectedValues, values);

	}

	@Test
	public void property() throws Exception {
		PropertyResource p = propResource.addPropertyAsNewResource(
				property.resolve("#prop"), property.resolve("#Type"));
		p.addPropertyAsString(property.resolve("#str"), "Some String");

		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);
		assertEquals("prop", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals(0, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());
		Element typeElem = (Element) elem.getElementsByTagNameNS(
				"http://example.com/property#", "Type").item(0);
		assertEquals(0, typeElem.getAttributes().getLength());
		assertEquals(1, typeElem.getChildNodes().getLength());
		Element strElem = (Element) typeElem.getElementsByTagNameNS(
				"http://example.com/property#", "str").item(0);
		assertEquals("Some String", strElem.getTextContent());
	}
	
	@Test
	public void propertyWithAbout() throws Exception {
		PropertyResource p = propResource.addPropertyAsNewResource(
				property.resolve("#prop"), property.resolve("#Type"));

		p.setResourceURI(property.resolve("#id"));
		
		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);
		assertEquals("prop", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals(0, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());
		Element typeElem = (Element) elem.getElementsByTagNameNS(
				"http://example.com/property#", "Type").item(0);
		assertEquals(1, typeElem.getAttributes().getLength());
		assertEquals(0, typeElem.getChildNodes().getLength());
		assertEquals("http://example.com/property#id", typeElem.getAttributeNS(PropertyResourceSerialiser.RDF, "about"));
	}
	

	@Test
	public void propertyAnonymous() throws Exception {
		
		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		assertEquals("Description", elem.getTagName());
		assertEquals(PropertyResourceSerialiser.RDF, elem.getNamespaceURI());
		assertEquals(0, elem.getAttributes().getLength());
		assertEquals(0, elem.getChildNodes().getLength());		
	}

	@Test
	public void propertyAnonymousWithAbout() throws Exception {
		propResource.setResourceURI(property.resolve("#id"));
		
		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		assertEquals("Description", elem.getTagName());
		assertEquals(PropertyResourceSerialiser.RDF, elem.getNamespaceURI());
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(0, elem.getChildNodes().getLength());
		assertEquals("http://example.com/property#id", elem.getAttributeNS(PropertyResourceSerialiser.RDF, "about"));
	}


	

	@Test
	public void propertyListLiterals() throws Exception {
		PropertyList pList = new PropertyList();
		pList.add(new PropertyLiteral("First"));
		pList.add(new PropertyLiteral("Second"));
		pList.add(new PropertyLiteral("Third"));

		propResource.addProperty(property.resolve("#somelist"), pList);
		propResource.accept(serialiser);

		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);

		assertEquals("somelist", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("Collection", elem.getAttributeNS(
				PropertyResourceSerialiser.RDF, "parseType"));
		assertEquals(1, elem.getAttributes().getLength());
//		System.out.println(elem.getChildNodes().item(0).getNodeName());
		assertEquals(3, elem.getChildNodes().getLength());

	}

	@Test
	public void literal() throws Exception {
		propResource.addPropertyAsString(property.resolve("#simple"),
				"Simple string");

		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);

		assertEquals("simple", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("Simple string", elem.getTextContent());
		assertEquals(0, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());

	}

	@Test
	public void twoLiterals() throws Exception {
		propResource.addPropertyAsString(property.resolve("#simple"), "One");
		propResource
				.addPropertyAsString(property.resolve("#simple"), "Another");

		propResource.accept(serialiser);
		Set<String> values = new HashSet<String>();

		Element rootElem = serialiser.getRootElement();

		for (int i = 0; i < rootElem.getChildNodes().getLength(); i++) {
			Element elem = (Element) rootElem.getChildNodes().item(i);
			assertEquals("simple", elem.getTagName());
			assertEquals("http://example.com/property#", elem.getNamespaceURI());
			values.add(elem.getTextContent());
			assertEquals(0, elem.getAttributes().getLength());
			assertEquals(1, elem.getChildNodes().getLength());
		}
		Set<String> expectedValues = new HashSet<String>();
		expectedValues.add("One");
		expectedValues.add("Another");
		assertEquals(expectedValues, values);

	}

	@Test
	public void literalInteger() throws Exception {
		PropertyLiteral literal = new PropertyLiteral(1337);
		propResource.addProperty(property.resolve("#number"), literal);
		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);
		assertEquals("number", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("1337", elem.getTextContent());
		String datatype = elem.getAttributeNS(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "datatype");
		assertEquals(PropertyLiteral.XSD_INT.toASCIIString(), datatype);
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());
	}

	@Test
	public void literalDate() throws Exception {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.clear(); // to reset millis
		// NOTE: java.util.Calendar Month is 0-based!
		cal.set(1952, 10, 28, 18, 37, 07);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		PropertyLiteral literal = new PropertyLiteral(cal);
		propResource.addProperty(property.resolve("#when"), literal);
		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);
		assertEquals("when", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("1952-11-28T18:37:07Z", elem.getTextContent());
		String datatype = elem.getAttributeNS(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "datatype");
		assertEquals(PropertyLiteral.XSD_DATETIME.toASCIIString(), datatype);
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());
	}

	@Test
	public void literalXml() throws Exception {
		String innerXml = "<example xmlns:ns1='http://example.com/ns1'><soup xmlns='http://example.com/different' attrib='attribValue' ns1:attrib='otherValue'>  some  \n  whitespaced text\n</soup></example>";
		PropertyLiteral literal = new PropertyLiteral(innerXml,
				PropertyLiteral.XML_LITERAL);
		propResource.addProperty(property.resolve("#xmlValue"), literal);
		propResource.accept(serialiser);
		Element elem = serialiser.getRootElement();
		elem = (Element) elem.getChildNodes().item(0);
		assertEquals("xmlValue", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());

		Attr datatype = elem.getAttributeNodeNS(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "datatype");
		assertNull(datatype);
		String parseType = elem.getAttributeNS(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType");
		assertEquals(parseType, "Literal");

		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());

		Element firstElem = (Element) elem.getChildNodes().item(0);
		assertEquals("example", firstElem.getNodeName());
		assertNull(firstElem.getNamespaceURI());

		Element exampleElem = (Element) elem.getElementsByTagName("example")
				.item(0);
		Element soupElem = (Element) exampleElem.getElementsByTagName("soup")
				.item(0);
		assertEquals("http://example.com/different", soupElem.getNamespaceURI());
		assertEquals("attribValue", soupElem.getAttribute("attrib"));
		assertEquals("  some  \n  whitespaced text\n",
				soupElem.getTextContent());
		assertEquals("attrib", soupElem.getAttributes().item(0).getNodeName());
		assertEquals("ns1:attrib", soupElem.getAttributes().item(1)
				.getNodeName());
		assertEquals("otherValue",
				soupElem.getAttributeNS("http://example.com/ns1", "attrib"));
	}

}
