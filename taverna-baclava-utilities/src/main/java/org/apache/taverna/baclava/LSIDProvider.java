/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package org.apache.taverna.baclava;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


/**
 * A trivial interface to a service capable of providing unique identifiers on
 * demand. Used by the DataThing object to 'fill in' any holes in its LSID map.
 * 
 * @author Tom Oinn
 */
public interface LSIDProvider {

	public static final NamespaceEnumeration WFDEFINITION = new NamespaceEnumeration(
			"wfDefinition");

	public static final NamespaceEnumeration WFINSTANCE = new NamespaceEnumeration(
			"wfInstance");

	public static final NamespaceEnumeration DATATHINGLEAF = new NamespaceEnumeration(
			"dataItem");

	public static final NamespaceEnumeration DATATHINGCOLLECTION = new NamespaceEnumeration(
			"dataCollection");

	/**
	 * Return a unique identifier to be used as an LSID or similar
	 */
	public String getID(LSIDProvider.NamespaceEnumeration namespace);

	class NamespaceEnumeration {
		private String desc = "";

		public NamespaceEnumeration(String description) {
			this.desc = description;
		}

		public String toString() {
			return this.desc;
		}
	}

}
