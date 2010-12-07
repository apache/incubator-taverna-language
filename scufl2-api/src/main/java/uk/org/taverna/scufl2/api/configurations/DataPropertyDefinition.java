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

/**
 * The definition of a {@link DataProperty}.
 * 
 * @author David Withers
 */
public class DataPropertyDefinition extends PropertyDefinition {

	/**
	 * Creates a definition of a <code>DataProperty</code> with no constraints on the property
	 * value.
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
	public DataPropertyDefinition(URI predicate, URI dataType, String label, String description,
			boolean required, boolean multiple) {
		super(predicate, dataType, label, description, required, multiple, new String[0]);
	}

	/**
	 * Creates a definition of a <code>DataProperty</code>.
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
	 * @param options
	 *            the valid values for the <code>Property</code>
	 */
	public DataPropertyDefinition(URI predicate, URI dataType, String label, String description,
			boolean required, boolean multiple, String[] options) {
		super(predicate, dataType, label, description, required, multiple, options);
	}

	protected String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent);
		sb.append("DataPropertyDefinition ");
		sb.append(getPredicate());
		sb.append("\n");
		sb.append(indent);
		sb.append(" label=" + getLabel() + ", description=" + getDescription() + ", required="
				+ isRequired() + ", multiple=" + isMultiple() + ", options="
				+ Arrays.toString(getOptions()) + ", dataType=" + getDataType() + "\n");
		return sb.toString();
	}

}
