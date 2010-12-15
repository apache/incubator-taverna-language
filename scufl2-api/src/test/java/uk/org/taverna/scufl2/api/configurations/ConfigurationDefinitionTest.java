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
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ConfigurationDefinition}.
 * 
 * @author David Withers
 */
public class ConfigurationDefinitionTest {
	
	private ConfigurationDefinition configurationDefinition;
	
	private List<PropertyDefinition> propertyDefinitions;
		
	private URI configurableType, objectType;

	@Before
	public void setUp() throws Exception {
		configurableType = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		objectType = configurableType.resolve("#ConfigType");
		propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(new DataPropertyDefinition(configurableType.resolve("#testProperty"), PropertyDefinition.STRING, "", "", "", true, false));
		configurationDefinition = new ConfigurationDefinition(configurableType, objectType, propertyDefinitions);
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#ActivityConfigurationDefinition(java.net.URI, java.net.URI)}.
	 */
	@Test
	public void testActivityConfigurationDefinitionURIURI() {
		assertEquals(configurableType, new ConfigurationDefinition(configurableType, objectType).getConfigurableType());
		assertEquals(objectType, new ConfigurationDefinition(configurableType, objectType).getObjectClass());
		assertEquals(new ArrayList<PropertyDefinition>(), new ConfigurationDefinition(configurableType, objectType).getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#ActivityConfigurationDefinition(java.net.URI, java.net.URI, java.util.List)}.
	 */
	@Test
	public void testActivityConfigurationDefinitionURIURIList() {
		assertEquals(configurableType, new ConfigurationDefinition(configurableType, objectType, propertyDefinitions).getConfigurableType());
		assertEquals(objectType, new ConfigurationDefinition(configurableType, objectType).getObjectClass());
		assertEquals(propertyDefinitions, new ConfigurationDefinition(configurableType, objectType, propertyDefinitions).getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#getConfigurableType()}.
	 */
	@Test
	public void testGetConfigurableType() {
		assertEquals(configurableType, configurationDefinition.getConfigurableType());
		assertEquals(configurableType, configurationDefinition.getConfigurableType());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#setConfigurableType(java.net.URI)}.
	 */
	@Test
	public void testSetConfigurableType() {
		assertEquals(configurableType, configurationDefinition.getConfigurableType());
		configurationDefinition.setConfigurableType(URI.create("test:test"));
		assertEquals(URI.create("test:test"), configurationDefinition.getConfigurableType());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#getObjectClass()}.
	 */
	@Test
	public void testGetObjectClass() {
		assertEquals(objectType, configurationDefinition.getObjectClass());
		assertEquals(objectType, configurationDefinition.getObjectClass());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#setObjectClass(java.net.URI)}.
	 */
	@Test
	public void testSetObjectClass() {
		assertEquals(objectType, configurationDefinition.getObjectClass());
		configurationDefinition.setObjectClass(URI.create("test:test"));
		assertEquals(URI.create("test:test"), configurationDefinition.getObjectClass());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#getPropertyDefinitions()}.
	 */
	@Test
	public void testGetPropertyDefinitions() {
		assertEquals(propertyDefinitions, configurationDefinition.getPropertyDefinitions());
		assertEquals(propertyDefinitions, configurationDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#setPropertyDefinitions(java.util.Set)}.
	 */
	@Test
	public void testSetPropertyDefinitions() {
		configurationDefinition.setPropertyDefinitions(new ArrayList<PropertyDefinition>());
		assertEquals(new ArrayList<PropertyDefinition>(), configurationDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#getPropertyDefinition(java.net.URI)}.
	 */
	@Test
	public void testGetPropertyDefinition() {
		assertNotNull(configurationDefinition.getPropertyDefinition(configurableType.resolve("#testProperty")));
		assertEquals(configurableType.resolve("#testProperty"), configurationDefinition.getPropertyDefinition(configurableType.resolve("#testProperty")).getPredicate());
		assertEquals(configurableType.resolve("#testProperty"), configurationDefinition.getPropertyDefinition(configurableType.resolve("#testProperty")).getPredicate());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition#toString()}.
	 */
	@Test
	public void testToString() {
		assertNotNull(configurationDefinition.toString());
	}

}
