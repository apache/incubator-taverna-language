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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.property.PropertyLiteral;

/**
 * Unit tests for {@link PropertyResourceDefinition}.
 * 
 * @author David Withers
 */
public class PropertyResourceDefinitionTest {

	private PropertyResourceDefinition propertyResourceDefinition;
	
	private List<PropertyDefinition> propertyDefinitions;

	private URI activityURI;

	@Before
	public void setUp() throws Exception {
		activityURI = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(new PropertyLiteralDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_STRING, "", "", "", true, false, false));
		propertyResourceDefinition = new PropertyResourceDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_STRING, "name", "label", "defn", true, false, false, propertyDefinitions);
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition#PropertyResourceDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean)}.
	 */
	@Test
	public void testObjectPropertyDefinitionURIURIStringStringBooleanBoolean() {
		propertyResourceDefinition = new PropertyResourceDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_STRING, "n", "a", "b", false, true, true);
		assertEquals(activityURI.resolve("#testProperty"), propertyResourceDefinition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, propertyResourceDefinition.getTypeURI());
		assertEquals("n", propertyResourceDefinition.getName());
		assertEquals("a", propertyResourceDefinition.getLabel());
		assertEquals("b", propertyResourceDefinition.getDescription());
		assertFalse(propertyResourceDefinition.isRequired());
		assertTrue(propertyResourceDefinition.isMultiple());
		assertTrue(propertyResourceDefinition.isOrdered());
		assertArrayEquals(new String[0], propertyResourceDefinition.getOptions());
		assertEquals(new ArrayList<PropertyDefinition>(), propertyResourceDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition#PropertyResourceDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean, java.util.Set)}.
	 */
	@Test
	public void testObjectPropertyDefinitionURIURIStringStringBooleanBooleanSetOfPropertyDefinition() {
		propertyResourceDefinition = new PropertyResourceDefinition(activityURI.resolve("#testProperty"), PropertyLiteral.XSD_STRING, "n", "a", "b", false, true, false, propertyDefinitions);
		assertEquals(activityURI.resolve("#testProperty"), propertyResourceDefinition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, propertyResourceDefinition.getTypeURI());
		assertEquals("n", propertyResourceDefinition.getName());
		assertEquals("a", propertyResourceDefinition.getLabel());
		assertEquals("b", propertyResourceDefinition.getDescription());
		assertFalse(propertyResourceDefinition.isRequired());
		assertTrue(propertyResourceDefinition.isMultiple());
		assertFalse(propertyResourceDefinition.isOrdered());
		assertArrayEquals(new String[0], propertyResourceDefinition.getOptions());
		assertEquals(propertyDefinitions, propertyResourceDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectDefinition#getTypeURI()}.
	 */
	@Test
	public void testGetTypeURI() {
		assertEquals(PropertyLiteral.XSD_STRING, propertyResourceDefinition.getTypeURI());
		assertEquals(PropertyLiteral.XSD_STRING, propertyResourceDefinition.getTypeURI());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectDefinition#setTypeURI(java.net.URI)}.
	 */
	@Test
	public void testSetTypeURI() {
		assertEquals(PropertyLiteral.XSD_STRING, propertyResourceDefinition.getTypeURI());
		propertyResourceDefinition.setTypeURI(PropertyLiteral.XSD_DOUBLE);
		assertEquals(PropertyLiteral.XSD_DOUBLE, propertyResourceDefinition.getTypeURI());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition#getPropertyDefinitions()}.
	 */
	@Test
	public void testGetPropertyDefinitions() {
		assertEquals(propertyDefinitions, propertyResourceDefinition.getPropertyDefinitions());
		assertEquals(propertyDefinitions, propertyResourceDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition#setPropertyDefinitions(java.util.Set)}.
	 */
	@Test
	public void testSetPropertyDefinitions() {
		propertyResourceDefinition.setPropertyDefinitions(new ArrayList<PropertyDefinition>());
		assertEquals(new ArrayList<PropertyDefinition>(), propertyResourceDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition#getPropertyDefinition(java.net.URI)}.
	 */
	@Test
	public void testGetPropertyDefinition() {
		assertNotNull(propertyResourceDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")));
		assertEquals(activityURI.resolve("#testProperty"), propertyResourceDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")).getPredicate());
		assertEquals(activityURI.resolve("#testProperty"), propertyResourceDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")).getPredicate());
	}

}
