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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 * The definition of an {@link PropertyResource}.
 * 
 * @author David Withers
 */
public class PropertyResourceDefinition extends PropertyDefinition {

	private URI typeURI;

	private List<PropertyDefinition> propertyDefinitions;
	
	/**
	 * Creates a definition of an <code>PropertyResource</code>.
	 * 
	 */
	public PropertyResourceDefinition() {
		propertyDefinitions = new ArrayList<PropertyDefinition>();
	}	
	
	/**
	 * Creates a definition of an <code>PropertyResource</code>.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>PropertyResource</code> that this class defines
	 * @param typeURI
	 *            the typeURI of the <code>PropertyResource</code>
	 * @param name
	 *            the name of the <code>PropertyResource</code>
	 * @param label
	 *            a human readable label for the <code>PropertyResource</code>
	 * @param description
	 *            a description of the <code>PropertyResource</code>
	 * @param required
	 *            whether the <code>PropertyResource</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the <code>PropertyResource</code>
	 * @param ordered
	 *            whether the order of multiple instances of the <code>Property</code> is significant
	 */
	public PropertyResourceDefinition(URI predicate, URI typeURI, String name, String label, String description,
			boolean required, boolean multiple, boolean ordered) {
		super(predicate, name, label, description, required, multiple, ordered, new String[0]);
		this.typeURI = typeURI;
		propertyDefinitions = new ArrayList<PropertyDefinition>();
	}

	/**
	 * Creates a definition of an <code>PropertyResource</code>.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>PropertyResource</code> that this class defines
	 * @param typeURI
	 *            the typeURI of the <code>PropertyResource</code>
	 * @param name
	 *            the name of the <code>PropertyResource</code>
	 * @param label
	 *            a human readable label for the <code>PropertyResource</code>
	 * @param description
	 *            a description of the <code>PropertyResource</code>
	 * @param required
	 *            whether the <code>PropertyResource</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the <code>PropertyResource</code>
	 * @param ordered
	 *            whether the order of multiple instances of the <code>Property</code> is significant
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s that define the <code>PropertyResource</code>
	 */
	public PropertyResourceDefinition(URI predicate, URI typeURI, String name, String label, String description,
			boolean required, boolean multiple, boolean ordered, List<PropertyDefinition> propertyDefinitions) {
		this(predicate, typeURI, name, label, description, required, multiple, ordered);
		setPropertyDefinitions(propertyDefinitions);
	}

	/**
	 * Returns the typeURI of the <code>PropertyResource</code>.
	 * 
	 * @return the typeURI of the <code>PropertyResource</code>
	 */
	public URI getTypeURI() {
		return typeURI;
	}

	/**
	 * Sets the typeURI of the <code>PropertyResource</code>.
	 * 
	 * @param typeURI the typeURI of the <code>PropertyResource</code>
	 */
	public void setTypeURI(URI typeURI) {
		this.typeURI = typeURI;
	}

	/**
	 * Returns the <code>PropertyDefinition</code>s that define the <code>PropertyResource</code>.
	 * 
	 * @return the <code>PropertyDefinition</code>s that define the <code>PropertyResource</code>
	 */
	public List<PropertyDefinition> getPropertyDefinitions() {
		return propertyDefinitions;
	}

	/**
	 * Sets the <code>PropertyDefinition</code>s that define the <code>PropertyResource</code>.
	 * 
	 * @param propertyDefinitions
	 *            the <code>PropertyDefinition</code>s that define the <code>PropertyResource</code>
	 */
	public void setPropertyDefinitions(List<PropertyDefinition> propertyDefinitions) {
		this.propertyDefinitions = propertyDefinitions;
	}

	/**
	 * Returns a <code>PropertyDefinition</code> with the specified predicate.
	 * 
	 * Return null if this <code>PropertyResourceDefinition</code> does not contain a
	 * <code>PropertyDefinition</code> with the specified predicate.
	 * 
	 * @param predicate
	 *            the predicate of the <code>PropertyDefinition</code> to return
	 * @return a <code>PropertyDefinition</code> with the specified predicate
	 */
	public PropertyDefinition getPropertyDefinition(URI predicate) {
		for (PropertyDefinition propertyDefinition : propertyDefinitions) {
			if (propertyDefinition.getPredicate().equals(predicate)) {
				return propertyDefinition;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((propertyDefinitions == null) ? 0 : propertyDefinitions.hashCode());
		result = prime * result + ((typeURI == null) ? 0 : typeURI.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyResourceDefinition other = (PropertyResourceDefinition) obj;
		if (propertyDefinitions == null) {
			if (other.propertyDefinitions != null)
				return false;
		} else if (!propertyDefinitions.equals(other.propertyDefinitions))
			return false;
		if (typeURI == null) {
			if (other.typeURI != null)
				return false;
		} else if (!typeURI.equals(other.typeURI))
			return false;
		return true;
	}

	protected String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent);
		sb.append("PropertyResourceDefinition ");
		sb.append(getPredicate());
		sb.append("\n");
		sb.append(indent);
		sb.append(" label=" + getLabel() + ", description=" + getDescription() + ", required="
				+ isRequired() + ", multiple=" + isMultiple() + ", ordered=" + isOrdered() + ", options="
				+ Arrays.toString(getOptions()) + ", typeURI=" + getTypeURI() + "\n");
		for (PropertyDefinition propertyDefinition : getPropertyDefinitions()) {
			sb.append(propertyDefinition.toString("  "));
		}
		return sb.toString();
	}

}
