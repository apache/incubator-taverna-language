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

import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 * Definition of the {@link Configuration} required to configure a
 * {@link uk.org.taverna.scufl2.api.common.Configurable Configurable}.
 * 
 * @author David Withers
 */
public class ConfigurationDefinition {

	private URI configurableType;

	private final PropertyResourceDefinition propertyResourceDefinition;

	/**
	 * Creates the definition of a {@link Configuration}.
	 */
	public ConfigurationDefinition() {
		propertyResourceDefinition = new PropertyResourceDefinition();
	}

	/**
	 * Creates the definition of the {@link Configuration} required to configure the
	 * {@link uk.org.taverna.scufl2.api.common.Configurable Configurable} specified by the
	 * configurableType.
	 * 
	 * @param configurableType
	 *            the URI that identifies the <code>Configurable</code> type
	 */
	public ConfigurationDefinition(URI configurableType) {
		this.configurableType = configurableType;
		propertyResourceDefinition = new PropertyResourceDefinition();
		propertyResourceDefinition.setTypeURI(configurableType.resolve("#ConfigType"));
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
	 * Returns the definition of the {@link PropertyResource} required by the
	 * <code>Configuration</code>.
	 * 
	 * @return the definition of the {@link PropertyResource} required by the
	 *         <code>Configuration</code>
	 */
	public PropertyResourceDefinition getPropertyResourceDefinition() {
		return propertyResourceDefinition;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ConfigurationDefinition for ");
		sb.append(configurableType);
		sb.append("[\n");
		sb.append(propertyResourceDefinition);
		sb.append("]\n");
		return sb.toString();
	}

}
