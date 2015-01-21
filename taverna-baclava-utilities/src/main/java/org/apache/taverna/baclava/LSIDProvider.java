/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package org.apache.taverna.baclava;

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
