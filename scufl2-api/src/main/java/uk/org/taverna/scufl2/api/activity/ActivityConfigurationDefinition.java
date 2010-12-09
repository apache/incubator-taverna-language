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

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;

/**
 * Definition of the configuration required by an Activity.
 * 
 * @author David Withers
 */
public class ActivityConfigurationDefinition {

	private URI activityType;

	private final Map<URI, PropertyDefinition> propertyDefinitions;

	/**
	 * Creates the definition of the configuration required by an Activity.
	 */
	public ActivityConfigurationDefinition() {
		propertyDefinitions = new HashMap<URI, PropertyDefinition>();
	}

	/**
	 * Creates the definition of the configuration required by the Activity specified by the URI.
	 * 
	 * @param activityType
	 *            the URI that identifies the Activity type
	 */
	public ActivityConfigurationDefinition(URI activityType) {
		this.activityType = activityType;
		propertyDefinitions = new HashMap<URI, PropertyDefinition>();
	}

	/**
	 * Creates the definition of the configuration required by the Activity specified by the URI.
	 * 
	 * @param activityType
	 *            the URI that identifies the Activity type
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s required to configure the Activity
	 */
	public ActivityConfigurationDefinition(URI activityType,
			Set<PropertyDefinition> propertyDefinitions) {
		this(activityType);
		setPropertyDefinitions(propertyDefinitions);
	}

	/**
	 * Returns the URI that identifies the Activity type.
	 * 
	 * @return the URI that identifies the Activity type
	 */
	public URI getActivityType() {
		return activityType;
	}

	/**
	 * Sets the URI that identifies the Activity type.
	 * 
	 * @param activityType
	 *            the URI that identifies the Activity type
	 */
	public void setActivityType(URI activityType) {
		this.activityType = activityType;
	}

	/**
	 * Returns the URI that identifies the configuration type.
	 * 
	 * @return the URI that identifies the configuration type
	 */
	public URI getConfigurationType() {
		return activityType.resolve("#ConfigType");
	}

	/**
	 * Returns the <code>PropertyDefinition</code>s required to configure the Activity.
	 * 
	 * @return the <code>PropertyDefinition</code>s required to configure the Activity
	 */
	public Set<PropertyDefinition> getPropertyDefinitions() {
		return new HashSet<PropertyDefinition>(propertyDefinitions.values());
	}

	/**
	 * Sets the <code>PropertyDefinition</code>s required to configure the Activity.
	 * 
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s required to configure the Activity
	 */
	public void setPropertyDefinitions(Set<PropertyDefinition> propertyDefinitions) {
		this.propertyDefinitions.clear();
		for (PropertyDefinition propertyDefinition : propertyDefinitions) {
			this.propertyDefinitions.put(propertyDefinition.getPredicate(), propertyDefinition);
		}
	}

	/**
	 * Returns a <code>PropertyDefinition</code> with the specified predicate.
	 * 
	 * Return null if this <code>ActivityConfigurationDefinition</code> does not contain a
	 * <code>PropertyDefinition</code> with the specified predicate.
	 * 
	 * @param predicate
	 *            the predicate of the <code>PropertyDefinition</code> to return
	 * @return a <code>PropertyDefinition</code> with the specified predicate
	 */
	public PropertyDefinition getPropertyDefinition(URI predicate) {
		return propertyDefinitions.get(predicate);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ActivityConfigurationDefinition for ");
		sb.append(activityType);
		sb.append("[\n");
		for (PropertyDefinition property : getPropertyDefinitions()) {
			sb.append(property);
			sb.append("\n");
		}
		sb.append("]\n");
		return sb.toString();
	}

}
