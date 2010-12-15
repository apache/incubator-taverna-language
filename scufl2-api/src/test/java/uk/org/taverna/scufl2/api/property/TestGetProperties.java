package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestGetProperties {

	private static final URI EXAMPLE_COM = URI.create("http://example.com/");
	private static final URI CONSTANT = EXAMPLE_COM.resolve("#constant");
	private static final URI PROPERTY = EXAMPLE_COM.resolve("#property");

	@Test
	public void getProperties() throws Exception {
		PropertyResource resource = new PropertyResource();
		PropertyResource constant1 = new PropertyResource(
				EXAMPLE_COM.resolve("#constant1"));
		PropertyResource constant2 = new PropertyResource(
				EXAMPLE_COM.resolve("#constant2"));
		resource.getProperties().get(PROPERTY).add(constant1);
		resource.getProperties().get(PROPERTY).add(constant2);
		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());

		Set<PropertyResource> expectedProperties = new HashSet<PropertyResource>(
				Arrays.asList(constant1, constant2));

		Set<PropertyResource> propertiesAsResources = resource
		.getPropertiesAsResources(PROPERTY);

		assertEquals(expectedProperties, propertiesAsResources);
	}

	@Test
	public void getPropertiesAsURIs() throws Exception {
		PropertyResource resource = new PropertyResource();
		PropertyResource constant1 = new PropertyResource(
				EXAMPLE_COM.resolve("#constant1"));
		PropertyResource constant2 = new PropertyResource(
				EXAMPLE_COM.resolve("#constant2"));
		resource.getProperties().get(PROPERTY).add(constant1);
		resource.getProperties().get(PROPERTY).add(constant2);
		assertEquals(1, resource.getProperties().size());
		assertEquals(2, resource.getProperties().get(PROPERTY).size());

		Set<PropertyResource> expectedProperties = new HashSet<PropertyResource>(
				Arrays.asList(constant1, constant2));

		Set<PropertyResource> propertiesAsResources = resource
				.getPropertiesAsResources(PROPERTY);

		assertEquals(expectedProperties, propertiesAsResources);
	}

}
