/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package net.sf.taverna.t2.baclava;

// JDOM Imports
import org.jdom.Namespace;

/**
 * Provides a place to store constants relating to the XML Scufl representation
 * such as namespaces etc.
 * 
 * @author Tom Oinn
 */
public class XScufl {

	public final static Namespace XScuflNS = Namespace.getNamespace("s",
			"http://org.embl.ebi.escience/xscufl/0.1alpha");

}
