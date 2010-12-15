package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;

import org.junit.Test;

import uk.org.taverna.scufl2.api.property.PropertyNotFoundException;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class TestProperty {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");

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
		resource.getProperties().get(EXAMPLE_COM.resolve("#property"))
		.add(new PropertyResource(EXAMPLE_COM.resolve("#constant")));

		assertEquals(1, resource.getProperties().size());
		assertEquals(EXAMPLE_COM.resolve("#constant"),
				resource.getPropertyAsResourceURI(EXAMPLE_COM
						.resolve("#property")));
	}

	@Test(expected=PropertyNotFoundException.class)
	public void getPropertyAsResourceURINotFound() throws Exception {
		PropertyResource resource = new PropertyResource();
		resource.getPropertyAsResourceURI(EXAMPLE_COM.resolve("#notFound"));
	}

	@Test
	public void resourceConstructor() throws Exception {
		PropertyResource resource = new PropertyResource(
				EXAMPLE_COM.resolve("#fish"));
		assertEquals(EXAMPLE_COM.resolve("#fish"), resource.getResourceURI());
		assertNull(resource.getTypeURI());
	}

}
