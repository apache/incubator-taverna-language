package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.net.URI;

import org.junit.Test;

public class TestGetProperty {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");
	private static final URI CONSTANT = EXAMPLE_COM.resolve("#constant");
	private static final URI PROPERTY = EXAMPLE_COM.resolve("#property");

	@Test
	public void emptyObject() throws Exception {
		PropertyResource resource = new PropertyResource();
		assertNull(resource.getTypeURI());
		assertNull(resource.getResourceURI());
		assertEquals(0, resource.getProperties().size());
	}

	@Test
	public void getProperty() throws Exception {
		PropertyResource resource = new PropertyResource();
		PropertyResource propertyResource = new PropertyResource(CONSTANT);
		resource.getProperties().get(PROPERTY).add(propertyResource);
		assertSame(propertyResource, resource.getProperty(PROPERTY));
	}

	@Test
	public void getPropertyAsResourceURI() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(CONSTANT));
		assertEquals(CONSTANT, resource.getPropertyAsResourceURI(PROPERTY));
	}

	@Test(expected = MultiplePropertiesException.class)
	public void getPropertyAsResourceURIMultiple() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(EXAMPLE_COM.resolve("#constant1")));
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(EXAMPLE_COM.resolve("#constant2")));

		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());
		resource.getPropertyAsResourceURI(PROPERTY);
	}

	@Test(expected = PropertyNotFoundException.class)
	public void getPropertyAsResourceURINotFound() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getPropertyAsResourceURI(EXAMPLE_COM.resolve("#notFound"));
	}

	@Test(expected = UnexpectedPropertyException.class)
	public void getPropertyAsResourceURINullURI() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY).add(new PropertyResource());
		resource.getPropertyAsResourceURI(PROPERTY);
	}

	@Test(expected = UnexpectedPropertyException.class)
	public void getPropertyAsResourceURIWrongType() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyLiteral("Hello there"));
		resource.getPropertyAsResourceURI(PROPERTY);
	}

	@Test
	public void getPropertyAsString() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyLiteral("Hello there"));
		assertEquals("Hello there", resource.getPropertyAsString(PROPERTY));
	}

	@Test(expected = MultiplePropertiesException.class)
	public void getPropertyAsStringMultiple() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyLiteral("Hello 1"));
		resource.getProperties().get(PROPERTY)
		.add(new PropertyLiteral("Hello 2"));

		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());
		resource.getPropertyAsString(PROPERTY);
	}

	@Test(expected = PropertyNotFoundException.class)
	public void getPropertyAsStringNotFound() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getPropertyAsString(EXAMPLE_COM.resolve("#notFound"));
	}

	@Test(expected = UnexpectedPropertyException.class)
	public void getPropertyAsStringWrongType() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY).add(new PropertyResource());
		resource.getPropertyAsString(PROPERTY);
	}

	@Test
	public void getPropertyAsTypeLiteral() throws Exception {
		PropertyResource resource = new PropertyResource();
		PropertyLiteral propertyLiteral = new PropertyLiteral("Hello there");
		resource.getProperties().get(PROPERTY).add(propertyLiteral);
		assertSame(propertyLiteral,
				resource.getPropertyOfType(PROPERTY, PropertyLiteral.class));
	}

	@Test(expected = UnexpectedPropertyException.class)
	public void getPropertyAsTypeWrongType() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyLiteral("Hello there"));
		resource.getPropertyOfType(PROPERTY, PropertyResource.class);
	}

	@Test(expected = MultiplePropertiesException.class)
	public void getPropertyMultiple() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(EXAMPLE_COM.resolve("#constant1")));
		resource.getProperties().get(PROPERTY)
		.add(new PropertyLiteral("string2"));
		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());
		resource.getProperty(PROPERTY);
	}

	@Test(expected = PropertyNotFoundException.class)
	public void getPropertyNotFound() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperty(EXAMPLE_COM.resolve("#notFound"));
	}

	@Test(expected = MultiplePropertiesException.class)
	public void getPropertyOfTypeMultiple() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(EXAMPLE_COM.resolve("#constant1")));
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(EXAMPLE_COM.resolve("#constant2")));
		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());
		resource.getPropertyOfType(PROPERTY, PropertyResource.class);
	}

	@Test(expected = PropertyNotFoundException.class)
	public void getPropertyOfTypeNotFound() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getPropertyOfType(EXAMPLE_COM.resolve("#notFound"), PropertyLiteral.class);
	}

	@Test
	public void resourceConstructor() throws Exception {
		PropertyResource resource = new PropertyResource(
				EXAMPLE_COM.resolve("#resourceURI"));
		assertEquals(EXAMPLE_COM.resolve("#resourceURI"),
				resource.getResourceURI());
		assertNull(resource.getTypeURI());
	}

}
