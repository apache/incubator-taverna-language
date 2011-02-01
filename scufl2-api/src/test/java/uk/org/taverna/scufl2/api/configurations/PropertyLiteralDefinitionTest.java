/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package uk.org.taverna.scufl2.api.configurations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.property.PropertyLiteral;

/**
 * Unit tests for {@link PropertyLiteralDefinition}.
 *
 * @author David Withers
 */
public class PropertyLiteralDefinitionTest {

	private PropertyLiteralDefinition propertyLiteralDefinition;

	private URI activityURI;

	@Before
	public void setUp() throws Exception {
		activityURI = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		propertyLiteralDefinition = new PropertyLiteralDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_STRING, "n", "l", "d", false, false, false);
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition#getLiteralType()}.
	 */
	@Test
	public void testGetLiteralType() {
		assertEquals(PropertyLiteral.XSD_STRING, propertyLiteralDefinition.getLiteralType());
		assertEquals(PropertyLiteral.XSD_STRING, propertyLiteralDefinition.getLiteralType());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#PropertyDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean, java.lang.String[])}.
	 */
	@Test
	public void testPropertyDefinition() {
		LinkedHashSet<String> options = new LinkedHashSet<String>();
		options.addAll(Arrays.asList("Yes", "No"));
		PropertyLiteralDefinition propertyDefinition = new PropertyLiteralDefinition(
				activityURI.resolve("#testProperty"), null, "name", "label",
				"description", true, true, true, options) {
			@Override
			protected String toString(String indent) {
				return "";
			}
		};
		assertEquals(activityURI.resolve("#testProperty"), propertyDefinition.getPredicate());
		assertEquals("name", propertyDefinition.getName());
		assertEquals("label", propertyDefinition.getLabel());
		assertEquals("description", propertyDefinition.getDescription());
		assertTrue(propertyDefinition.isRequired());
		assertTrue(propertyDefinition.isMultiple());
		assertTrue(propertyDefinition.isOrdered());
		assertEquals(options, propertyDefinition.getOptions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition#PropertyLiteralDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean)}.
	 */
	@Test
	public void testPropertyLiteralDefinitionURIURIStringStringBooleanBoolean() {
		PropertyLiteralDefinition propertyLiteralDefinition = new PropertyLiteralDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_STRING, "n", "l", "d", false, false, false);
		assertEquals(activityURI.resolve("#testProperty"), propertyLiteralDefinition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, propertyLiteralDefinition.getLiteralType());
		assertEquals("n", propertyLiteralDefinition.getName());
		assertEquals("l", propertyLiteralDefinition.getLabel());
		assertEquals("d", propertyLiteralDefinition.getDescription());
		assertFalse(propertyLiteralDefinition.isRequired());
		assertFalse(propertyLiteralDefinition.isMultiple());
		assertFalse(propertyLiteralDefinition.isOrdered());
		assertTrue(propertyLiteralDefinition.getOptions().isEmpty());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition#PropertyLiteralDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean, java.lang.String[])}.
	 */
	@Test
	public void testPropertyLiteralDefinitionURIURIStringStringBooleanBooleanStringArray() {
		LinkedHashSet<String> options = new LinkedHashSet<String>();
		options.addAll(Arrays.asList("Yes", "No"));

		PropertyLiteralDefinition propertyLiteralDefinition = new PropertyLiteralDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_INTEGER, "name", "x", "y", false, true, false, options);
		assertEquals(activityURI.resolve("#testProperty"), propertyLiteralDefinition.getPredicate());
		assertEquals(PropertyLiteral.XSD_INTEGER, propertyLiteralDefinition.getLiteralType());
		assertEquals("name", propertyLiteralDefinition.getName());
		assertEquals("x", propertyLiteralDefinition.getLabel());
		assertEquals("y", propertyLiteralDefinition.getDescription());
		assertFalse(propertyLiteralDefinition.isRequired());
		assertTrue(propertyLiteralDefinition.isMultiple());
		assertFalse(propertyLiteralDefinition.isOrdered());
		assertEquals(options, propertyLiteralDefinition.getOptions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.DataDefinition#setLiteralType(java.net.URI)}.
	 */
	@Test
	public void testSetLiteralType() {
		assertEquals(PropertyLiteral.XSD_STRING, propertyLiteralDefinition.getLiteralType());
		propertyLiteralDefinition.setLiteralType(PropertyLiteral.XSD_FLOAT);
		assertEquals(PropertyLiteral.XSD_FLOAT, propertyLiteralDefinition.getLiteralType());
	}


}
