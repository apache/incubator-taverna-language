/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package net.sf.taverna.t2.baclava.iterator;

// Utility Imports
import java.util.Iterator;

/**
 * Specifies an Iterator that can be reset to some initial starting condition
 * 
 * @author Tom Oinn
 */
public interface ResumableIterator extends Iterator {

	public void reset();

	public int size();

	public int[] getCurrentLocation();

}
