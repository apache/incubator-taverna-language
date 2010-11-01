package uk.org.taverna.scufl2.api.configurations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Test;

import uk.org.taverna.scufl2.api.configurations.Property.PropertyType;

public class TestProperty {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");

	@Test
	public void equalsSame() throws Exception {
		Property property1 = new Property();
		assertTrue(property1.equals(property1));
	}

	@Test
	public void equalsSubjectUri() throws Exception {
		Property property1 = new Property();
		property1.setSubject(EXAMPLE_COM.resolve("#subject"));
		property1.setPredicate(EXAMPLE_COM.resolve("#property"));
		property1.setObjectUri(EXAMPLE_COM.resolve("#value"));

		Property property2 = new Property();
		property2.setSubject(EXAMPLE_COM.resolve("#subject"));
		property2.setPredicate(EXAMPLE_COM.resolve("#property"));
		property2.setObjectUri(EXAMPLE_COM.resolve("#value"));

		assertEquals(property1, property2);
		assertEquals(property1.hashCode(), property2.hashCode());
	}

	@Test
	public void noSubjectString() throws Exception {
		Property property = new Property();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectValue("Hello there");
		assertEquals("[ <http://example.com/#property> \"Hello there\" ] .",
				property.toString());
	}

	@Test
	public void noSubjectUri() throws Exception {
		Property property = new Property();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectUri(EXAMPLE_COM.resolve("#value"));
		assertEquals(PropertyType.ObjectProperty, property.getPropertyType());
		assertEquals(
				"[ <http://example.com/#property> <http://example.com/#value> ] .",
				property.toString());
	}

	@Test
	public void notEqualsNull() throws Exception {
		Property property1 = new Property();
		Property property2 = new Property();
		assertFalse(property1.equals(property2));
	}

	@Test
	public void notEqualsSubjectUri() throws Exception {
		Property property1 = new Property();
		property1.setSubject(EXAMPLE_COM.resolve("#subject"));
		property1.setPredicate(EXAMPLE_COM.resolve("#property"));
		property1.setObjectUri(EXAMPLE_COM.resolve("#value"));

		Property property2 = new Property();
		property2.setSubject(EXAMPLE_COM.resolve("#subject"));
		property2.setPredicate(EXAMPLE_COM.resolve("#property"));
		property2.setObjectUri(EXAMPLE_COM.resolve("#otherValue"));

		assertFalse("properties should differ", property1.equals(property2));
		assertNotSame(property1.hashCode(), property2.hashCode());
	}

	@Test
	public void notEqualsSubjectValue() throws Exception {
		Property property1 = new Property();
		property1.setSubject(EXAMPLE_COM.resolve("#subject"));
		property1.setPredicate(EXAMPLE_COM.resolve("#property"));
		property1.setObjectValue("Hello there");

		Property property2 = new Property();
		property2.setSubject(EXAMPLE_COM.resolve("#subject"));
		property2.setPredicate(EXAMPLE_COM.resolve("#property"));
		property1.setObjectValue("Something else");

		assertFalse("properties should differ", property1.equals(property2));
		assertNotSame(property1.hashCode(), property2.hashCode());
	}

	@Test
	public void nulls() throws Exception {
		Property property = new Property();
		assertEquals(PropertyType.ObjectProperty, property.getPropertyType());
		assertEquals("[ rdf:nil rdf:nil ] .", property.toString());
	}

	@Test
	public void stringEscaping() throws Exception {
		Property property = new Property();
		property.setSubject(EXAMPLE_COM.resolve("#subject"));
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectValue("A slash \\ and a \" single quote and a double escaped quote \\\" here");
		assertEquals(
				"<http://example.com/#subject> <http://example.com/#property> \"A slash \\\\ and"
				+ " a \\\" single quote and a double escaped quote \\\\\\\" here\" .",
				property.toString());
	}

	@Test
	public void subjectString() throws Exception {
		Property property = new Property();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectValue("Hello there");
		assertEquals(PropertyType.DataProperty, property.getPropertyType());
		assertEquals("[ <http://example.com/#property> \"Hello there\" ] .",
				property.toString());
	}

	@Test
	public void subjectUri() throws Exception {
		Property property = new Property();
		property.setSubject(EXAMPLE_COM.resolve("#subject"));
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectUri(EXAMPLE_COM.resolve("#value"));
		assertEquals(PropertyType.ObjectProperty, property.getPropertyType());
		assertEquals(
				"<http://example.com/#subject> <http://example.com/#property> <http://example.com/#value> .",
				property.toString());
	}

	@Test
	public void uriThenValue() throws Exception {
		Property property = new Property();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectValue("Hello there");
		assertEquals(PropertyType.DataProperty, property.getPropertyType());
		property.setObjectUri(EXAMPLE_COM.resolve("#value"));
		assertEquals(PropertyType.ObjectProperty, property.getPropertyType());
		assertNull(property.getObjectValue());
		assertEquals(
				"[ <http://example.com/#property> <http://example.com/#value> ] .",
				property.toString());
	}

	@Test
	public void valueThenUri() throws Exception {
		Property property = new Property();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectUri(EXAMPLE_COM.resolve("#value"));
		assertEquals(PropertyType.ObjectProperty, property.getPropertyType());
		assertNull(property.getObjectValue());
		property.setObjectValue("Hello there");
		assertEquals(PropertyType.DataProperty, property.getPropertyType());
		assertEquals("[ <http://example.com/#property> \"Hello there\" ] .",
				property.toString());
	}

}
