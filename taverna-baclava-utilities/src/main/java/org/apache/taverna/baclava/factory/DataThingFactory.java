/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package org.apache.taverna.baclava.factory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.taverna.baclava.DataThing;


/**
 * A collection of static methods to build DataThings from various other Java
 * object types.
 * 
 * @author Tom Oinn
 */
public class DataThingFactory {
	public static DataThing fetchFromURL(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		conn.connect();
		String contentType = conn.getContentType();
		Object content = conn.getContent();

		DataThing dt = new DataThing(content);
		dt.getMetadata().setMIMETypes(
				Arrays.asList(new String[] { contentType }));

		return dt;
	}

	public static DataThing bake(Object theObject) {
		return new DataThing(convertObject(theObject));
	}

	/**
	 * Easy for String objects, everything already recognizes them so no custom
	 * code required.
	 */
	public static DataThing bake(String theString) {
		return new DataThing(convertObject(theString));
	}

	/**
	 * For String arrays convert the array to a List and store that.
	 */
	public static DataThing bake(String[] theStringArray) {
		return new DataThing(convertObject(theStringArray));
		/**
		 * List theList = new ArrayList(); for (int i = 0; i <
		 * theStringArray.length; i++) { theList.add(theStringArray[i]); }
		 * return new DataThing(theList);
		 */
	}

	/**
	 * For byte arrays store the byte array as is
	 */
	public static DataThing bake(byte[] theByteArray) {
		return new DataThing(convertObject(theByteArray));
	}

	/**
	 * For arrays of byte arrays store each byte array in a List
	 */
	public static DataThing bake(byte[][] theByteArrayArray) {
		return new DataThing(convertObject(theByteArrayArray));
		/**
		 * List theList = new ArrayList(); for (int i = 0; i <
		 * theByteArrayArray.length; i++) { theList.add(theByteArrayArray[i]); }
		 * return new DataThing(theList);
		 */
	}

	/**
	 * Bake a List of Lists into a List of byte[]
	 */
	public static DataThing bakeForSoaplab(List theList) {
		Object[] list = ((List) theList).toArray();
		if (list.length == 0) {
			// Return an empty data thing
			return new DataThing(new ArrayList());
		}
		if (!(list[0] instanceof List)) {
			// If not a list of lists then just return the
			// original object wrapped in a DataThing
			return new DataThing(theList);
		}

		Vector v = new Vector();
		for (int i = 0; i < list.length; i++) {
			Object[] list2 = ((ArrayList) list[i]).toArray();
			if (list2.length > 0 && (list2[0] instanceof Byte)) {
				byte[] bytes = new byte[list2.length];
				for (int j = 0; j < list2.length; j++)
					bytes[j] = ((Byte) list2[j]).byteValue();
				v.addElement(bytes);
			} else {
				// If we can't cope here just return the original
				// object wrapped up in a DataThing
				return new DataThing(theList);
			}
		}
		byte[][] results = new byte[v.size()][];
		v.copyInto(results);
		return bake(results);
	}

	public static Object convertObject(Object theObject) {
		if (theObject == null) {
			return null;
		}
		// If an array type...
		Class theClass = theObject.getClass();
		if (theClass.isArray()) {
			// Special case for byte[]
			if (theObject instanceof byte[]) {
				// System.out.println("Found a byte[], returning it.");
				return theObject;
			} else {
				// For all other arrays, create a new
				// List and iterate over the array,
				// unpackaging the item and recursively
				// putting it into the new List after
				// conversion
				Object[] theArray = (Object[]) theObject;
				// System.out.println("Found an array length
				// "+theArray.length+", repacking as List...");
				List l = new ArrayList();
				for (int i = 0; i < theArray.length; i++) {
					l.add(convertObject(theArray[i]));
				}
				return l;
			}
		}
		// If a collection, iterate over it and copy
		if (theObject instanceof Collection) {
			if (theObject instanceof List) {
				// System.out.println("Re-packing a list...");
				List l = new ArrayList();
				for (Iterator i = ((List) theObject).iterator(); i.hasNext();) {
					l.add(convertObject(i.next()));
				}
				return l;
			} else if (theObject instanceof Set) {
				// System.out.println("Re-packing a set...");
				Set s = new HashSet();
				for (Iterator i = ((Set) theObject).iterator(); i.hasNext();) {
					s.add(convertObject(i.next()));
				}
				return s;
			}
		}
		// If a number then return the string representation for it
		if (theObject instanceof Number) {
			// System.out.println("Found a number, converting it to a
			// string...");
			return theObject.toString();
		}
		// Otherwise just return the object
		// System.out.println("Found a "+theObject.getClass().getName()+",
		// returning it");
		return theObject;
	}

}
