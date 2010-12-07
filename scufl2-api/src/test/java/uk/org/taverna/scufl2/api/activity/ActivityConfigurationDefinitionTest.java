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
package uk.org.taverna.scufl2.api.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.configurations.DataPropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;

/**
 * Unit tests for {@link ActivityConfigurationDefinition}.
 * 
 * @author David Withers
 */
public class ActivityConfigurationDefinitionTest {
	
	private ActivityConfigurationDefinition activityConfigurationDefinition;
	
	private Set<PropertyDefinition> propertyDefinitions;
	
	private URI activityURI;

	@Before
	public void setUp() throws Exception {
		activityURI = URI.create("http://ns.taverna.org.uk/2010/activity/test");
		propertyDefinitions = new HashSet<PropertyDefinition>();
		propertyDefinitions.add(new DataPropertyDefinition(activityURI.resolve("#testProperty"), PropertyDefinition.STRING, "", "", true, false));
		activityConfigurationDefinition = new ActivityConfigurationDefinition(activityURI, propertyDefinitions);
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#ActivityConfigurationDefinition(java.net.URI)}.
	 */
	@Test
	public void testActivityConfigurationDefinitionURI() {
		assertEquals(activityURI, new ActivityConfigurationDefinition(activityURI).getActivityType());
		assertEquals(new HashSet<PropertyDefinition>(), new ActivityConfigurationDefinition(activityURI).getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#ActivityConfigurationDefinition(java.net.URI, java.util.Set)}.
	 */
	@Test
	public void testActivityConfigurationDefinitionURISetOfPropertyDefinition() {
		assertEquals(activityURI, new ActivityConfigurationDefinition(activityURI, propertyDefinitions).getActivityType());
		assertEquals(propertyDefinitions, new ActivityConfigurationDefinition(activityURI, propertyDefinitions).getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#getActivityType()}.
	 */
	@Test
	public void testGetActivityType() {
		assertEquals(activityURI, activityConfigurationDefinition.getActivityType());
		assertEquals(activityURI, activityConfigurationDefinition.getActivityType());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#getConfigurationType()}.
	 */
	@Test
	public void testGetConfigurationType() {
		assertEquals(activityURI.resolve("#ConfigType"), activityConfigurationDefinition.getConfigurationType());
		assertEquals(activityURI.resolve("#ConfigType"), activityConfigurationDefinition.getConfigurationType());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#getPropertyDefinitions()}.
	 */
	@Test
	public void testGetPropertyDefinitions() {
		assertEquals(propertyDefinitions, activityConfigurationDefinition.getPropertyDefinitions());
		assertEquals(propertyDefinitions, activityConfigurationDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#setPropertyDefinitions(java.util.Set)}.
	 */
	@Test
	public void testSetPropertyDefinitions() {
		activityConfigurationDefinition.setPropertyDefinitions(new HashSet<PropertyDefinition>());
		assertEquals(new HashSet<PropertyDefinition>(), activityConfigurationDefinition.getPropertyDefinitions());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#getPropertyDefinition(java.net.URI)}.
	 */
	@Test
	public void testGetPropertyDefinition() {
		assertNotNull(activityConfigurationDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")));
		assertEquals(activityURI.resolve("#testProperty"), activityConfigurationDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")).getPredicate());
		assertEquals(activityURI.resolve("#testProperty"), activityConfigurationDefinition.getPropertyDefinition(activityURI.resolve("#testProperty")).getPredicate());
	}

	/**
	 * Test method for {@link uk.org.taverna.scufl2.api.activity.ActivityConfigurationDefinition#toString()}.
	 */
	@Test
	public void testToString() {
		assertNotNull(activityConfigurationDefinition.toString());
	}

}
