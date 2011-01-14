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

import uk.org.taverna.scufl2.api.property.PropertyObject;

/**
 * Abstract definition of a {@link PropertyObject}.
 * 
 * @author David Withers
 */
public abstract class PropertyDefinition {

	private URI predicate;
	private String name, label, description;
	private boolean required, multiple, ordered;
	private String[] options;

	/**
	 * Creates a PropertyDefinition that defines the attributes of a {@link PropertyObject}.
	 */
	public PropertyDefinition() {
	}
	
	/**
	 * Creates a PropertyDefinition that defines the attributes of a {@link PropertyObject}.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>Property</code> that this class defines
	 * @param name
	 *            the name of the <code>Property</code>
	 * @param label
	 *            a human readable label for the <code>Property</code>
	 * @param description
	 *            a description of the <code>Property</code>
	 * @param required
	 *            whether the <code>Property</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the <code>Property</code>
	 * @param ordered
	 *            whether the order of multiple instances of the <code>Property</code> is significant
	 * @param options
	 *            the valid values for the <code>Property</code>
	 */
	public PropertyDefinition(URI predicate, String name, String label, String description,
			boolean required, boolean multiple, boolean ordered, String[] options) {
		this.predicate = predicate;
		this.name = name;
		this.label = label;
		this.description = description;
		this.required = required;
		this.multiple = multiple;
		this.ordered = ordered;
		this.options = options;
	}

	/**
	 * Returns the URI identifying the <code>Property</code> that this class defines.
	 * 
	 * @return the URI identifying the <code>Property</code> that this class defines
	 */
	public URI getPredicate() {
		return predicate;
	}

	/**
	 * Sets the URI identifying the <code>Property</code> that this class defines.
	 * 
	 * @param predicate the URI identifying the <code>Property</code> that this class defines
	 */
	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}

	/**
	 * Returns the name of the <code>Property</code>.
	 * 
	 * @return the name of the <code>Property</code>
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the <code>Property</code>.
	 * 
	 * @param name the name of the <code>Property</code>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a human readable label for the <code>Property</code>.
	 * 
	 * @return a human readable label for the <code>Property</code>
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets a human readable label for the <code>Property</code>.
	 * 
	 * @param label a human readable label for the <code>Property</code>
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns a description of the <code>Property</code>.
	 * 
	 * @return a description of the <code>Property</code>
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description of the <code>Property</code>.
	 * 
	 * @param description a description of the <code>Property</code>
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns true if the <code>Property</code> is mandatory.
	 * 
	 * @return true if the <code>Property</code> is mandatory
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets whether the <code>Property</code> is mandatory.
	 * 
	 * @param required whether the <code>Property</code> is mandatory
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Returns true if there can be multiple instances of the <code>Property</code>.
	 * 
	 * @return true if there can be multiple instances of the <code>Property</code>
	 */
	public boolean isMultiple() {
		return multiple;
	}

	/**
	 * Sets whether there can be multiple instances of the <code>Property</code>.
	 * 
	 * @param multiple whether there can be multiple instances of the <code>Property</code>
	 */
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	/**
	 * Returns true if the order of multiple instances of the <code>Property</code> is significant.
	 * 
	 * @return true if the order of multiple instances of the <code>Property</code> is significant
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * Sets whether the order of multiple instances of the <code>Property</code> is significant.
	 * 
	 * @param ordered the order of multiple instances of the <code>Property</code> is significant
	 */
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	/**
	 * Returns the valid values for the <code>Property</code>.
	 * 
	 * If the value of the <code>Property</code> is not constrained this method will return an zero
	 * length array.
	 * 
	 * @return the valid values for the <code>Property</code>
	 */
	public String[] getOptions() {
		return options;
	}

	/**
	 * Sets the valid values for the <code>Property</code>.
	 * 
	 * @param options
	 */
	public void setOptions(String[] options) {
		this.options = options;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + (multiple ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(options);
		result = prime * result + (ordered ? 1231 : 1237);
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyDefinition other = (PropertyDefinition) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (multiple != other.multiple)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(options, other.options))
			return false;
		if (ordered != other.ordered)
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (required != other.required)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toString("");
	}

	protected abstract String toString(String indent);

}
