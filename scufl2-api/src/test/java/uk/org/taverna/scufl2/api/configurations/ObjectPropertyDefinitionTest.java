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
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ObjectPropertyDefinition}.
 * 
 * @author David Withers
 */
public class ObjectPropertyDefinitionTest {

	private ObjectPropertyDefinition objectPropertyDefinition;
	
	private Set<PropertyDefinition> propertyDefinitions;

	private URI activityURI;

	@Before
	public void setUp() throws Exception {
		activityURI = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		propertyDefinitions = new HashSet<PropertyDefinition>();
		propertyDefinitions.add(new DataPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "", "", true, false));
		objectPropertyDefinition = new ObjectPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "label", "defn", true, false, propertyDefinitions);
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectPropertyDefinition#ObjectPropertyDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean)}.
	 */
	@Test
	public void testObjectPropertyDefinitionURIURIStringStringBooleanBoolean() {
		objectPropertyDefinition = new ObjectPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "a", "b", false, true);
		assertEquals(activityURI.resolve("#testProperty"), objectPropertyDefinition.getPredicate());
		assertEquals(PropertyDefinition.STRING, objectPropertyDefinition.getDataType());
		assertEquals("a", objectPropertyDefinition.getLabel());
		assertEquals("b", objectPropertyDefinition.getDescription());
		assertFalse(objectPropertyDefinition.isRequired());
		assertTrue(objectPropertyDefinition.isMultiple());
		assertArrayEquals(new String[0], objectPropertyDefinition.getOptions());
		assertEquals(new HashSet<PropertyDefinition>(), objectPropertyDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectPropertyDefinition#ObjectPropertyDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean, java.util.Set)}.
	 */
	@Test
	public void testObjectPropertyDefinitionURIURIStringStringBooleanBooleanSetOfPropertyDefinition() {
		objectPropertyDefinition = new ObjectPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "a", "b", false, true, propertyDefinitions);
		assertEquals(activityURI.resolve("#testProperty"), objectPropertyDefinition.getPredicate());
		assertEquals(PropertyDefinition.STRING, objectPropertyDefinition.getDataType());
		assertEquals("a", objectPropertyDefinition.getLabel());
		assertEquals("b", objectPropertyDefinition.getDescription());
		assertFalse(objectPropertyDefinition.isRequired());
		assertTrue(objectPropertyDefinition.isMultiple());
		assertArrayEquals(new String[0], objectPropertyDefinition.getOptions());
		assertEquals(propertyDefinitions, objectPropertyDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectPropertyDefinition#getPropertyDefinitions()}.
	 */
	@Test
	public void testGetPropertyDefinitions() {
		assertEquals(propertyDefinitions, objectPropertyDefinition.getPropertyDefinitions());
		assertEquals(propertyDefinitions, objectPropertyDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectPropertyDefinition#setPropertyDefinitions(java.util.Set)}.
	 */
	@Test
	public void testSetPropertyDefinitions() {
		objectPropertyDefinition.setPropertyDefinitions(new HashSet<PropertyDefinition>());
		assertEquals(new HashSet<PropertyDefinition>(), objectPropertyDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ObjectPropertyDefinition#getPropertyDefinition(java.net.URI)}.
	 */
	@Test
	public void testGetPropertyDefinition() {
		assertNotNull(objectPropertyDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")));
		assertEquals(activityURI.resolve("#testProperty"), objectPropertyDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")).getPredicate());
		assertEquals(activityURI.resolve("#testProperty"), objectPropertyDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")).getPredicate());
	}

}
