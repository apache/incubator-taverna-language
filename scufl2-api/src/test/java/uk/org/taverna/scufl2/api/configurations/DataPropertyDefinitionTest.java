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
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DataPropertyDefinition}.
 * 
 * @author David Withers
 */
public class DataPropertyDefinitionTest {

	private DataPropertyDefinition dataPropertyDefinition;
	
	private URI activityURI;

	@Before
	public void setUp() throws Exception {
		activityURI = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		dataPropertyDefinition = new DataPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "n", "l", "d", false, false);
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.DataPropertyDefinition#DataPropertyDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean)}.
	 */
	@Test
	public void testDataPropertyDefinitionURIURIStringStringBooleanBoolean() {
		DataPropertyDefinition dataPropertyDefinition = new DataPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "n", "l", "d", false, false);
		assertEquals(activityURI.resolve("#testProperty"), dataPropertyDefinition.getPredicate());
		assertEquals(PropertyDefinition.STRING, dataPropertyDefinition.getDataValueType());
		assertEquals("n", dataPropertyDefinition.getName());
		assertEquals("l", dataPropertyDefinition.getLabel());
		assertEquals("d", dataPropertyDefinition.getDescription());
		assertFalse(dataPropertyDefinition.isRequired());
		assertFalse(dataPropertyDefinition.isMultiple());
		assertArrayEquals(new String[0], dataPropertyDefinition.getOptions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.DataPropertyDefinition#DataPropertyDefinition(java.net.URI, java.net.URI, java.lang.String, java.lang.String, boolean, boolean, java.lang.String[])}.
	 */
	@Test
	public void testDataPropertyDefinitionURIURIStringStringBooleanBooleanStringArray() {
		DataPropertyDefinition dataPropertyDefinition = new DataPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.INTEGER, "name", "x", "y", false, true, new String[] {"A", "Z"});
		assertEquals(activityURI.resolve("#testProperty"), dataPropertyDefinition.getPredicate());
		assertEquals(PropertyDefinition.INTEGER, dataPropertyDefinition.getDataValueType());
		assertEquals("name", dataPropertyDefinition.getName());
		assertEquals("x", dataPropertyDefinition.getLabel());
		assertEquals("y", dataPropertyDefinition.getDescription());
		assertFalse(dataPropertyDefinition.isRequired());
		assertTrue(dataPropertyDefinition.isMultiple());
		assertArrayEquals(new String[] {"A", "Z"}, dataPropertyDefinition.getOptions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.DataPropertyDefinition#getDataValueType()}.
	 */
	@Test
	public void testGetDataValueType() {
		assertEquals(PropertyDefinition.STRING, dataPropertyDefinition.getDataValueType());
		assertEquals(PropertyDefinition.STRING, dataPropertyDefinition.getDataValueType());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.DataDefinition#setDataValueType(java.net.URI)}.
	 */
	@Test
	public void testSetDataValueType() {
		assertEquals(PropertyDefinition.STRING, dataPropertyDefinition.getDataValueType());
		dataPropertyDefinition.setDataValueType(PropertyDefinition.FLOAT);
		assertEquals(PropertyDefinition.FLOAT, dataPropertyDefinition.getDataValueType());
	}


}
