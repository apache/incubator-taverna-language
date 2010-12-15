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

import java.net.URI;
import java.util.List;

/**
 * Definition of the {@link Configuration} required to configure a
 * {@link uk.org.taverna.scufl2.api.common.Configurable Configurable}.
 * 
 * @author David Withers
 */
public class ConfigurationDefinition {

	private URI configurableType;

	private final ObjectPropertyDefinition objectPropertyDefinition = new ObjectPropertyDefinition();

	/**
	 * Creates the definition of a {@link Configuration}.
	 */
	public ConfigurationDefinition() {
	}

	/**
	 * Creates the definition of the {@link Configuration} required to configure the
	 * {@link uk.org.taverna.scufl2.api.common.Configurable Configurable} specified by the
	 * configurableType.
	 * 
	 * @param configurableType
	 *            the URI that identifies the <code>Configurable</code> type
	 * @param objectClass
	 */
	public ConfigurationDefinition(URI configurableType, URI objectClass) {
		this.configurableType = configurableType;
		objectPropertyDefinition.setObjectClass(objectClass);
	}

	/**
	 * Creates the definition of the {@link Configuration} required to configure the
	 * {@link uk.org.taverna.scufl2.api.common.Configurable Configurable} specified by the
	 * configurableType.
	 * 
	 * @param configurableType
	 *            the URI that identifies the <code>Configurable</code> type
	 * @param objectClass
	 * @param propertyDefinitions
	 */
	public ConfigurationDefinition(URI configurableType, URI objectClass,
			List<PropertyDefinition> propertyDefinitions) {
		this.configurableType = configurableType;
		objectPropertyDefinition.setObjectClass(objectClass);
		objectPropertyDefinition.setPropertyDefinitions(propertyDefinitions);
	}

	/**
	 * Returns the URI that identifies the {@link uk.org.taverna.scufl2.api.common.Configurable
	 * Configurable} type.
	 * 
	 * @return the URI that identifies the <code>Configurable</code> type
	 */
	public URI getConfigurableType() {
		return configurableType;
	}

	/**
	 * Sets the URI that identifies the {@link uk.org.taverna.scufl2.api.common.Configurable
	 * Configurable} type.
	 * 
	 * @param configurableType
	 *            the URI that identifies the <code>Configurable</code> type
	 */
	public void setConfigurableType(URI configurableType) {
		this.configurableType = configurableType;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public URI getObjectClass() {
		return objectPropertyDefinition.getObjectClass();
	}

	/**
	 * 
	 * 
	 * @param objectClass
	 */
	public void setObjectClass(URI objectClass) {
		objectPropertyDefinition.setObjectClass(objectClass);
	}

	/**
	 * Returns the {@link PropertyDefinition PropertyDefinitions} required by the
	 * {@link Configuration}.
	 * 
	 * @return the <code>PropertyDefinition</code>s required by the <code>Configuration</code>
	 */
	public List<PropertyDefinition> getPropertyDefinitions() {
		return objectPropertyDefinition.getPropertyDefinitions();
	}

	/**
	 * Sets the {@link PropertyDefinition PropertyDefinitions} required by the {@link Configuration}
	 * .
	 * 
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s required by the <code>Configuration</code>
	 */
	public void setPropertyDefinitions(List<PropertyDefinition> propertyDefinitions) {
		objectPropertyDefinition.setPropertyDefinitions(propertyDefinitions);
	}

	/**
	 * Returns a {@link PropertyDefinition PropertyDefinitions} with the specified predicate.
	 * 
	 * Return null if this <code>ConfigurationDefinition</code> does not contain a
	 * <code>PropertyDefinition</code> with the specified predicate.
	 * 
	 * @param predicate
	 *            the predicate of the <code>PropertyDefinition</code> to return
	 * @return a <code>PropertyDefinition</code> with the specified predicate
	 */
	public PropertyDefinition getPropertyDefinition(URI predicate) {
		return objectPropertyDefinition.getPropertyDefinition(predicate);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ConfigurationDefinition for ");
		sb.append(configurableType);
		sb.append("[\n");
		for (PropertyDefinition property : getPropertyDefinitions()) {
			sb.append(property);
			sb.append("\n");
		}
		sb.append("]\n");
		return sb.toString();
	}

}
