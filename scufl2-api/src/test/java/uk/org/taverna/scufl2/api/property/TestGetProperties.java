package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class TestGetProperties {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");
	private static final URI PROPERTY = EXAMPLE_COM.resolve("#property");

	@Test
	public void getProperties() throws Exception {
		PropertyResource resource = new PropertyResource();
		PropertyReference last = new PropertyReference(
				EXAMPLE_COM.resolve("#xx-last"));
		PropertyReference middle = new PropertyReference(
				EXAMPLE_COM.resolve("#mm-middle"));
		PropertyReference first = new PropertyReference(
				EXAMPLE_COM.resolve("#aa-first"));
		resource.getProperties().get(PROPERTY).add(last);
		resource.getProperties().get(PROPERTY).add(first);
		resource.getProperties().get(PROPERTY).add(middle);

		assertEquals(1, resource.getProperties().size());
		assertEquals(3, resource.getProperties().get(PROPERTY).size());

		Set<PropertyReference> expectedProperties = new LinkedHashSet<PropertyReference>(
				Arrays.asList(first, middle, last));

		Set<PropertyReference> propertyReferences = resource
				.getPropertiesAsReferences(PROPERTY);

		assertEquals(expectedProperties, propertyReferences);
	}

	@Test
	public void getPropertiesAsURIs() throws Exception {
		PropertyResource resource = new PropertyResource();
		PropertyResource prop1 = new PropertyResource();
		prop1.addPropertyAsString(PROPERTY.resolve("#yo"), "Hello");
		prop1.addPropertyAsString(PROPERTY.resolve("#yo"), "There");
		PropertyResource prop2 = new PropertyResource();
		prop2.addPropertyAsString(PROPERTY.resolve("#yo"), "Yo");

		resource.getProperties().get(PROPERTY).add(prop1);
		resource.getProperties().get(PROPERTY).add(prop2);
		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());

		Set<PropertyReference> expectedProperties = new HashSet<PropertyReference>(
				Arrays.asList(prop1, prop2));

		Set<PropertyResource> propertiesAsResources = resource
				.getPropertiesAsResources(PROPERTY);

		assertEquals(expectedProperties, propertiesAsResources);

		Set<String> expectedStrings = new HashSet<String>(Arrays.asList(
				"Hello", "There"));
		assertEquals(expectedStrings,
				prop1.getPropertiesAsStrings(PROPERTY.resolve("#yo")));
		assertEquals("Yo", prop2.getPropertyAsString(PROPERTY.resolve("#yo")));
	}

}
