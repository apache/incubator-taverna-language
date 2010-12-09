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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The definition of an {@link ObjectProperty}.
 * 
 * @author David Withers
 */
public class ObjectPropertyDefinition extends PropertyDefinition {

	private final Map<URI, PropertyDefinition> propertyDefinitions;

	/**
	 * Creates a definition of an <code>ObjectProperty</code>.
	 * 
	 */
	public ObjectPropertyDefinition() {
		propertyDefinitions = new HashMap<URI, PropertyDefinition>();
	}	
	
	/**
	 * Creates a definition of an <code>ObjectProperty</code>.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>Property</code> that this class defines
	 * @param dataType
	 *            the datatype of the <code>Property</code>
	 * @param label
	 *            a human readable label for the <code>Property</code>
	 * @param description
	 *            a description of the <code>Property</code>
	 * @param required
	 *            whether the <code>Property</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the <code>Property</code>
	 */
	public ObjectPropertyDefinition(URI predicate, URI dataType, String label, String description,
			boolean required, boolean multiple) {
		super(predicate, dataType, label, description, required, multiple, new String[0]);
		propertyDefinitions = new HashMap<URI, PropertyDefinition>();
	}

	/**
	 * Creates a definition of an <code>ObjectProperty</code>.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>Property</code> that this class defines
	 * @param dataType
	 *            the datatype of the <code>Property</code>
	 * @param label
	 *            a human readable label for the <code>Property</code>
	 * @param description
	 *            a description of the <code>Property</code>
	 * @param required
	 *            whether the <code>Property</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the <code>Property</code>
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s that define the <code>ObjectProperty</code>
	 */
	public ObjectPropertyDefinition(URI predicate, URI dataType, String label, String description,
			boolean required, boolean multiple, Set<PropertyDefinition> propertyDefinitions) {
		this(predicate, dataType, label, description, required, multiple);
		setPropertyDefinitions(propertyDefinitions);
	}

	/**
	 * Returns the <code>PropertyDefinition</code>s that define the <code>ObjectProperty</code>.
	 * 
	 * @return the <code>PropertyDefinition</code>s that define the <code>ObjectProperty</code>
	 */
	public Set<PropertyDefinition> getPropertyDefinitions() {
		return new HashSet<PropertyDefinition>(propertyDefinitions.values());
	}

	/**
	 * Sets the <code>PropertyDefinition</code>s that define the <code>ObjectProperty</code>.
	 * 
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s that define the <code>ObjectProperty</code>
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
	 * Return null if this <code>ObjectPropertyDefinition</code> does not contain a
	 * <code>PropertyDefinition</code> with the specified predicate.
	 * 
	 * @param predicate
	 *            the predicate of the <code>PropertyDefinition</code> to return
	 * @return a <code>PropertyDefinition</code> with the specified predicate
	 */
	public PropertyDefinition getPropertyDefinition(URI predicate) {
		return propertyDefinitions.get(predicate);
	}

	protected String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent);
		sb.append("ObjectPropertyDefinition ");
		sb.append(getPredicate());
		sb.append("\n");
		sb.append(indent);
		sb.append(" label=" + getLabel() + ", description=" + getDescription() + ", required="
				+ isRequired() + ", multiple=" + isMultiple() + ", options="
				+ Arrays.toString(getOptions()) + ", dataType=" + getDataType() + "\n");
		for (PropertyDefinition propertyDefinition : getPropertyDefinitions()) {
			sb.append(propertyDefinition.toString("  "));
		}
		return sb.toString();
	}

}
