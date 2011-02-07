package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.*;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.xml.XMLConstants;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.icu.util.Calendar;

import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class TestPropertySerialisation {
	
	

	private PropertyResourceSerialiser serialiser;
	private List<Object> elements;
	private PropertyResource propResource;

	
	private URI property = URI.create("http://example.com/property");

	@Before
	public void makeVisitor() {
		elements = new ArrayList<Object>();
		serialiser = new PropertyResourceSerialiser(elements, URI.create("/"));
	}
	
	@Before
	public void makePropResource() {
		propResource = new PropertyResource();
	}
	
	
	@Test
	public void literal() throws Exception {
		propResource.addPropertyAsString(property.resolve("#simple"), "Simple string");
		
		propResource.accept(serialiser);
		Element elem = (Element) elements.get(0);
		assertEquals("simple", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("Simple string", elem.getTextContent());
		assertEquals(0, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());
		
	}
	
	
	@Test
	public void literalInteger() throws Exception {		
		PropertyLiteral literal = new PropertyLiteral(1337);
		propResource.addProperty(property.resolve("#number"), literal);
		propResource.accept(serialiser);
		Element elem = (Element) elements.get(0);
		assertEquals("number", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("1337", elem.getTextContent());
		String datatype = elem.getAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "datatype");
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
		Element elem = (Element) elements.get(0);
		assertEquals("when", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		assertEquals("1952-11-28T18:37:07Z", elem.getTextContent());
		String datatype = elem.getAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "datatype");
		assertEquals(PropertyLiteral.XSD_DATETIME.toASCIIString(), datatype);	
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());		
	}
	
	
	@Test
	public void literalXml() throws Exception {
		String innerXml = "<example xmlns:ns1='http://example.com/ns1'><soup xmlns='http://example.com/different' attrib='attribValue' ns1:attrib='otherValue'>  some  \n  whitespaced text\n</soup></example>";
		PropertyLiteral literal = new PropertyLiteral(innerXml, PropertyLiteral.XML_LITERAL);
		propResource.addProperty(property.resolve("#xmlValue"), literal);
		propResource.accept(serialiser);
		Element elem = (Element) elements.get(0);
		assertEquals("xmlValue", elem.getTagName());
		assertEquals("http://example.com/property#", elem.getNamespaceURI());
		
		
		Attr datatype = elem.getAttributeNodeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "datatype");
		assertNull(datatype);		
		String parseType = elem.getAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType");
		assertEquals(parseType, "Literal");
		
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals(1, elem.getChildNodes().getLength());
		
		Element firstElem = (Element)elem.getChildNodes().item(0);
		assertEquals("example", firstElem.getNodeName());
		assertNull(firstElem.getNamespaceURI());

		Element exampleElem = (Element)elem.getElementsByTagName("example").item(0);
		Element soupElem = (Element) exampleElem.getElementsByTagName("soup").item(0);
		assertEquals("http://example.com/different", soupElem.getNamespaceURI());
		assertEquals("attribValue", soupElem.getAttribute("attrib"));
		assertEquals("  some  \n  whitespaced text\n", soupElem.getTextContent());
		assertEquals("attrib", soupElem.getAttributes().item(0).getNodeName());
		assertEquals("ns1:attrib", soupElem.getAttributes().item(1).getNodeName());
		assertEquals("otherValue", soupElem.getAttributeNS("http://example.com/ns1", "attrib"));		
	}
	
	
}
