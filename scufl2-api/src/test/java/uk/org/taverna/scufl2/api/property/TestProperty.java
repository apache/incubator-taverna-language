package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;

import org.junit.Test;

public class TestProperty {

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
	public void getPropertyAsResourceURI() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getProperties().get(PROPERTY)
		.add(new PropertyResource(CONSTANT));

		assertEquals(1, resource.getProperties().size());
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

	@Test
	public void resourceConstructor() throws Exception {
		PropertyResource resource = new PropertyResource(
				EXAMPLE_COM.resolve("#resourceURI"));
		assertEquals(EXAMPLE_COM.resolve("#resourceURI"),
				resource.getResourceURI());
		assertNull(resource.getTypeURI());
	}

}
