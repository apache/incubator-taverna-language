package uk.org.taverna.scufl2.api.configurations;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

public class TestProperty {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");

	@Test
	public void classObject() throws Exception {
		ObjectProperty property = new ObjectProperty();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectClass(EXAMPLE_COM.resolve("#class"));
		assertEquals("[ <http://example.com/#property> [\n"
				+ "    a <http://example.com/#class>] ] .", property.toString());
	}

	@Test
	public void deeplyNested() throws Exception {
		ObjectProperty prop_1 = new ObjectProperty();
		prop_1.setPredicate(EXAMPLE_COM.resolve("#multiple"));

		DataProperty prop_1_1 = new DataProperty(EXAMPLE_COM.resolve("#str1"),
		"string");
		DataProperty prop_1_2 = new DataProperty(EXAMPLE_COM.resolve("#num2"),
				"123", DataProperty.XSD.resolve("#integer"));

		prop_1.getObjectProperties().add(prop_1_1);
		prop_1.getObjectProperties().add(prop_1_2);

		ObjectProperty prop_2 = new ObjectProperty();
		prop_1.setPredicate(EXAMPLE_COM.resolve("#multiple"));

		ObjectProperty prop_2_1 = new ObjectProperty(
				EXAMPLE_COM.resolve("#ref1"), EXAMPLE_COM.resolve("#url1"));
		ObjectProperty prop_2_2 = new ObjectProperty(
				EXAMPLE_COM.resolve("#ref2"), EXAMPLE_COM.resolve("#url1"),
				EXAMPLE_COM.resolve("#class"));

		prop_2.getObjectProperties().add(prop_2_1);
		prop_1.getObjectProperties().add(prop_2_2);

		ObjectProperty prop = new ObjectProperty();
		prop.setPredicate(EXAMPLE_COM.resolve("#prop1"));
		prop.getObjectProperties().add(prop_1);
		prop.getObjectProperties().add(prop_2);

		assertEquals(
				"[ <http://example.com/#multiple> [\n"
						+ "    <http://example.com/#str1> \"string\";\n"
						+ "    <http://example.com/#num2> \"123\"^^<http://www.w3.org/2001/XMLSchema#integer>;\n"
						+ "    <http://example.com/#ref2> [\n"
						+ "        a <http://example.com/#class>;\n"
						+ "        = <http://example.com/#url1>]] ] .",
				prop_1.toString());
	}

	@Test
	public void multipleStrings() throws Exception {
		ObjectProperty property = new ObjectProperty();
		property.setPredicate(EXAMPLE_COM.resolve("#multiple"));

		DataProperty prop1 = new DataProperty(EXAMPLE_COM.resolve("#str1"),
		"string");
		DataProperty prop2 = new DataProperty(EXAMPLE_COM.resolve("#num2"),
				"123", DataProperty.XSD.resolve("#integer"));

		property.getObjectProperties().add(prop1);
		property.getObjectProperties().add(prop2);
		assertEquals(
				"[ <http://example.com/#multiple> [\n"
				+ "    <http://example.com/#str1> \"string\";\n"
				+ "    <http://example.com/#num2> \"123\"^^<http://www.w3.org/2001/XMLSchema#integer>] ] ."
				+ "", property.toString());
	}

	@Test
	public void stringEscaping() throws Exception {
		DataProperty property = new DataProperty();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setDataValue("A slash \\ and a \" single quote and a double escaped quote \\\" here");
		assertEquals(
				"[ <http://example.com/#property> \"A slash \\\\ and"
				+ " a \\\" single quote and a double escaped quote \\\\\\\" here\" ] .",
				property.toString());
	}

	@Test
	public void stringObject() throws Exception {
		DataProperty property = new DataProperty();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setDataValue("Hello there");
		assertEquals("[ <http://example.com/#property> \"Hello there\" ] .",
				property.toString());
	}

	@Test
	public void uriAndClassObject() throws Exception {
		ObjectProperty property = new ObjectProperty();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectUri(EXAMPLE_COM.resolve("#value"));
		property.setObjectClass(EXAMPLE_COM.resolve("#class"));
		assertEquals("[ <http://example.com/#property> [\n"
				+ "    a <http://example.com/#class>;\n"
				+ "    = <http://example.com/#value>] ] .", property.toString());
	}

	@Test
	public void uriObject() throws Exception {
		ObjectProperty property = new ObjectProperty();
		property.setPredicate(EXAMPLE_COM.resolve("#property"));
		property.setObjectUri(EXAMPLE_COM.resolve("#value"));
		assertEquals(
				"[ <http://example.com/#property> <http://example.com/#value> ] .",
				property.toString());
	}

}
