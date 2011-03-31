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
package uk.org.taverna.scufl2.api.property;

import java.net.URI;

/**
 * Thrown when a {@link PropertyObject} is not found when a single property was expected.
 * 
 * @see PropertyResource#getProperty(java.net.URI)
 * @author David Withers
 */
public class PropertyNotFoundException extends PropertyException {

	private static final long serialVersionUID = 1916372334894633453L;

	public PropertyNotFoundException(URI predicate, PropertyResource propertyResource) {
		super("Could not find property " + predicate + " in " + propertyResource, predicate,
				propertyResource);
	}

}
