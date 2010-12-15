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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PropertyDefinition}.
 * 
 * @author David Withers
 */
public class PropertyDefinitionTest {
	
	private PropertyDefinition propertyDefinition;

	private URI activityURI;

	@Before
	public void setUp() throws Exception {
		activityURI = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		propertyDefinition = new PropertyDefinition(activityURI.resolve("#testProperty"), "name", "label", "description", true, false, new String[0]) {
			protected String toString(String indent) {
				return "";
			}			
		};
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#PropertyDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean, java.lang.String[])}.
	 */
	@Test
	public void testPropertyDefinition() {
		PropertyDefinition propertyDefinition = new PropertyDefinition(activityURI.resolve("#testProperty"), "name", "A", "B", true, true, new String[] {"yes", "no"}) {
			protected String toString(String indent) {
				return "";
			}			
		};
		assertEquals(activityURI.resolve("#testProperty"), propertyDefinition.getPredicate());
		assertEquals("name", propertyDefinition.getName());
		assertEquals("A", propertyDefinition.getLabel());
		assertEquals("B", propertyDefinition.getDescription());
		assertTrue(propertyDefinition.isRequired());
		assertTrue(propertyDefinition.isMultiple());
		assertArrayEquals(new String[] {"yes", "no"}, propertyDefinition.getOptions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#getPredicate()}.
	 */
	@Test
	public void testGetPredicate() {
		assertEquals(activityURI.resolve("#testProperty"), propertyDefinition.getPredicate());
		assertEquals(activityURI.resolve("#testProperty"), propertyDefinition.getPredicate());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("name", propertyDefinition.getName());
		assertEquals("name", propertyDefinition.getName());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#getLabel()}.
	 */
	@Test
	public void testGetLabel() {
		assertEquals("label", propertyDefinition.getLabel());
		assertEquals("label", propertyDefinition.getLabel());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertEquals("description", propertyDefinition.getDescription());
		assertEquals("description", propertyDefinition.getDescription());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#isRequired()}.
	 */
	@Test
	public void testIsRequired() {
		assertEquals(true, propertyDefinition.isRequired());
		assertEquals(true, propertyDefinition.isRequired());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#isMultiple()}.
	 */
	@Test
	public void testIsMultiple() {
		assertEquals(false, propertyDefinition.isMultiple());
		assertEquals(false, propertyDefinition.isMultiple());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#getOptions()}.
	 */
	@Test
	public void testGetOptions() {
		assertArrayEquals(new String[0], propertyDefinition.getOptions());
		assertArrayEquals(new String[0], propertyDefinition.getOptions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.PropertyDefinition#toString()}.
	 */
	@Test
	public void testToString() {
		assertNotNull(propertyDefinition.toString());
	}
	
}
