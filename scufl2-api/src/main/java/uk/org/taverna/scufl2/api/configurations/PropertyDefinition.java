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

import javax.xml.XMLConstants;

/**
 * Abstract definition of a {@link Property}.
 * 
 * @author David Withers
 */
public abstract class PropertyDefinition {

	public static final URI BOOLEAN = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI + "#boolean");
	public static final URI DOUBLE = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI + "#double");
	public static final URI FLOAT = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI + "#float");
	public static final URI LONG = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI + "#long");
	public static final URI INTEGER = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI + "#integer");
	public static final URI STRING = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI + "#string");

	private URI predicate;
	private String name, label, description;
	private boolean required, multiple;
	private String[] options;

	/**
	 * Creates a PropertyDefinition that defines the attributes of a {@link Property}.
	 */
	public PropertyDefinition() {
	}
	
	/**
	 * Creates a PropertyDefinition that defines the attributes of a {@link Property}.
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
	 * @param options
	 *            the valid values for the <code>Property</code>
	 */
	public PropertyDefinition(URI predicate, String name, String label, String description,
			boolean required, boolean multiple, String[] options) {
		this.predicate = predicate;
		this.name = name;
		this.label = label;
		this.description = description;
		this.required = required;
		this.multiple = multiple;
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
	public String toString() {
		return toString("");
	}

	protected abstract String toString(String indent);

}
