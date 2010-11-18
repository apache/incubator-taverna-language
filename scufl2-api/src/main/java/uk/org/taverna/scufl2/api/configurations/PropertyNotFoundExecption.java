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

/**
 * Thrown when a {@link Property} is not found.
 * 
 * @author David Withers
 */
public class PropertyNotFoundExecption extends Exception {

	private static final long serialVersionUID = 2590871147239697985L;

	public PropertyNotFoundExecption() {
		super();
	}

	public PropertyNotFoundExecption(String message) {
		super(message);
	}

	public PropertyNotFoundExecption(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyNotFoundExecption(Throwable cause) {
		super(cause);
	}

}
