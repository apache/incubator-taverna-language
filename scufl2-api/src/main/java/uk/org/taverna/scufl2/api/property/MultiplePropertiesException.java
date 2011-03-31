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
 * Thrown when more than one {@link PropertyObject} was found, buy only one property was expected.
 * 
 * @see PropertyResource#getProperty(java.net.URI)
 * @see PropertyResource#getPropertyAsString(java.net.URI)
 * @see PropertyResource#getPropertyAsResourceURI(java.net.URI)
 * 
 * @author Stian Soiland-Reyes
 */
public class MultiplePropertiesException extends PropertyException {

	private static final long serialVersionUID = 2801964990757296381L;

	public MultiplePropertiesException(URI predicate,
			PropertyResource propertyResource) {
		super("More than one property " + predicate + " found in "
				+ propertyResource, predicate, propertyResource);
	}

}
