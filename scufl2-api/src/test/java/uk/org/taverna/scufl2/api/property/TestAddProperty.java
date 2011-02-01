package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

import org.junit.Test;

public class TestAddProperty {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");
	private static final URI CONSTANT = EXAMPLE_COM.resolve("#constant");
	private static final URI PROPERTY = EXAMPLE_COM.resolve("#property");
	private static final URI SOME_TYPE = EXAMPLE_COM.resolve("#SomeType");

	@Test
	public void addProperty() throws Exception {
		PropertyResource propertyResource = new PropertyResource();

		PropertyLiteral literal = new PropertyLiteral("Hello there");
		propertyResource.addProperty(PROPERTY, literal);

		Set<PropertyObject> properties = propertyResource.getProperties().get(PROPERTY);
		assertEquals(1, properties.size());
		PropertyLiteral propertyLiteral = (PropertyLiteral) properties.iterator().next();
		assertEquals("Hello there", propertyLiteral.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_STRING, propertyLiteral.getLiteralType());
	}

	@Test
	public void addPropertyAsNewResource() throws Exception {
		PropertyResource propertyResource = new PropertyResource();
		PropertyResource propResource = propertyResource.addPropertyAsNewResource(PROPERTY, SOME_TYPE);
		// No need to fill in propResource as we can check same-ness below

		Set<PropertyObject> properties = propertyResource.getProperties().get(PROPERTY);
		assertEquals(1, properties.size());
		assertEquals(propResource, properties.iterator().next());
	}

	@Test
	public void addPropertyAsResourceURI() throws Exception {
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.addPropertyReference(PROPERTY, CONSTANT);

		Set<PropertyObject> properties = propertyResource.getProperties().get(
				PROPERTY);
		assertEquals(1, properties.size());
		PropertyReference propRes = (PropertyReference) properties.iterator()
		.next();
		assertEquals(CONSTANT, propRes.getResourceURI());
	}

	@Test
	public void addPropertyAsString() throws Exception {
		PropertyResource propertyResource = new PropertyResource();
		propertyResource.addPropertyAsString(PROPERTY, "Hello there");

		Set<PropertyObject> properties = propertyResource.getProperties().get(PROPERTY);
		assertEquals(1, properties.size());
		PropertyLiteral propertyLiteral = (PropertyLiteral) properties.iterator().next();
		assertEquals("Hello there", propertyLiteral.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_STRING, propertyLiteral.getLiteralType());
	}

}
