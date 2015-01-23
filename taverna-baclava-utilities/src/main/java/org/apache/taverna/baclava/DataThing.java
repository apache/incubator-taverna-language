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


import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;


import org.apache.log4j.Logger;
import org.apache.taverna.baclava.factory.DataThingFactory;
import org.apache.taverna.baclava.factory.DataThingXMLFactory;
import org.apache.taverna.baclava.iterator.BaclavaIterator;
import org.apache.taverna.baclava.lsid.UUIDLSIDProvider;

import org.jdom.Element;

/**
 * A simple wrapper around an arbitrary Collection object which allows lookup
 * and storage of any metadata within the collection or its children. In
 * addition, there is an object of metadata concerning the DataThing itself. You
 * obtain a DataThing by invoking the bake operation on the DataThingFactory in
 * the factory subpackage, this is to allow the factory to sensibly configure
 * such things as types and underlying collections.
 * 
 * @author Tom Oinn
 */
public class DataThing implements Cloneable, Serializable {

	private static Logger logger = Logger.getLogger(DataThing.class);
	
	protected Object theDataObject;

	protected HashMap<Object, WeakReference<SemanticMarkup>> metadataMap = new HashMap<Object, WeakReference<SemanticMarkup>>();

	protected SemanticMarkup myMarkup;

	protected HashMap<Object, String> lsid = new HashMap<Object, String>();

	public static LSIDProvider SYSTEM_DEFAULT_LSID_PROVIDER = new UUIDLSIDProvider();

	// This array contains mime types, when asked for its most
	// interesting type this list is checked and the first match
	// returned.
	private static String[] interestingTypes = new String[] { "text/html",
			"text/xml", "text/rtf", "text/x-graphviz", "image/png",
			"image/jpeg", "image/gif", "application/zip", "text/plain" };

	public static Properties mimeTypes = new Properties();

	static {
		// Get the mimetypes.properties data
		try {
			ClassLoader loader = DataThing.class.getClassLoader();
			if (loader == null) {
				loader = Thread.currentThread().getContextClassLoader();
			}
			Enumeration en = loader
					.getResources("org/embl/ebi/escience/baclava/mimetypes.properties");
			while (en.hasMoreElements()) {
				URL resourceURL = (URL) en.nextElement();
				mimeTypes.load(resourceURL.openStream());
			}
		} catch (Exception ex) {
			logger.warn("Unable to get mime type information", ex);
		}

	}

	public DataThing(DataThing other) {
		theDataObject = other.theDataObject;
		metadataMap.putAll(other.metadataMap);
		myMarkup = new SemanticMarkup(other.myMarkup);
		lsid.putAll(other.lsid);
	}

	/**
	 * Construct a new DataThing from the supplied XML Jdom Element. Delegates
	 * to the DataThingXMLFactory for almost all the real work here.
	 */
	public DataThing(Element e) {
		myMarkup = new SemanticMarkup(this);
		theDataObject = DataThingXMLFactory.configureDataThing(e, this);
	}

	/**
	 * Get the LSID map object->LSID
	 */
	public Map<Object, String> getLSIDMap() {
		return lsid;
	}

	/**
	 * Populate all unassigned LSID values using the system default LSID
	 * provider
	 */
	public void fillLSIDValues() {
		if (SYSTEM_DEFAULT_LSID_PROVIDER != null) {
			try {
				fillLSIDValues(SYSTEM_DEFAULT_LSID_PROVIDER);
			} catch (Throwable ex) {
				logger.warn("Could not fill LSID values", ex);
			}
		}
	}

	/**
	 * Populate all unassigned LSID values in the LSID map from the supplied
	 * LSIDProvider. This traverses the object contained within the DataThing as
	 * well as the DataThing itself and provides LSID values where there are
	 * none defined.
	 */
	public void fillLSIDValues(LSIDProvider provider) {
		// First check the DataThing itself
		String selfValue = lsid.get(this);
		if (selfValue == null || selfValue.equals("")) {
			// lsid.put(this, provider.getID("datathing"));
		}
		// Recursively populate the data object lsid map
		doInternalLSIDFill(theDataObject, provider);
	}

	private void doInternalLSIDFill(Object o, LSIDProvider provider) {
		String lsidValue = lsid.get(o);
		if (lsidValue == null || lsidValue.equals("")) {
			if (o instanceof Collection) {
				lsid.put(o, provider.getID(LSIDProvider.DATATHINGCOLLECTION));
			} else {
				lsid.put(o, provider.getID(LSIDProvider.DATATHINGLEAF));
			}
		}
		if (o instanceof Collection) {
			Iterator i = ((Collection) o).iterator();
			for (; i.hasNext();) {
				doInternalLSIDFill(i.next(), provider);
			}
		} else {
			// got to the leaf
			return;
		}
	}

	/**
	 * Set the LSID of the named object to the specified value.
	 */
	public void setLSID(Object target, String id) {
		if (id != null) {
			lsid.put(target, id);
		}
	}

	/**
	 * Get the LSID of the named object, returns the empty string if there is no
	 * such mapping
	 */
	public String getLSID(Object target) {
		String lsidString = lsid.get(target);
		return (lsidString != null) ? lsidString : "";
	}

	/**
	 * Get the object with the supplied LSID or return null if there isn't one
	 */
	public Object getDataObjectWithLSID(String LSID) {
		for (Iterator<Object> i = lsid.keySet().iterator(); i.hasNext();) {
			Object key = i.next();
			String value = lsid.get(key);
			// logger.debug("LSID value found : "+value);
			if (value.equals(LSID)) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Return an array of all the LSIDs that this DataThing's LSID map contains
	 * as values
	 */
	public String[] getAllLSIDs() {
		return lsid.values().toArray(new String[0]);
	}

	/**
	 * Create and bind a new SemanticMarkup object to the DataThing itself, it's
	 * not totally clear there's a need for this but it does no harm so why not?
	 */
	public DataThing(Object o) {
		if (o == null) {
			throw new RuntimeException(
					"Attempt to create a null data object, definitely not allowed!");
		}
		if (o instanceof ArrayList == false) {
			theDataObject = DataThingFactory.convertObject(o);
		} else {
			theDataObject = o;
		}
		myMarkup = new SemanticMarkup(this);
	}

	/**
	 * Get a display icon for this DataThing, currently this is based on the
	 * MIME type from the syntactic type string.
	 */
	public ImageIcon getIcon() {
		String baseMIMEType = (getSyntacticType().split("'")[1].toLowerCase())
				.split("/")[0];
		return new ImageIcon(ClassLoader
				.getSystemResource("org/embl/ebi/escience/baclava/icons/"
						+ baseMIMEType + ".png"));
	}

	/**
	 * Return the SemanticMarkup object associated with the DataThing itself
	 */
	public SemanticMarkup getMetadata() {
		return myMarkup;
	}

	/**
	 * Get the underlying data object, this is the first level of the data
	 * document.
	 */
	public Object getDataObject() {
		return theDataObject;
	}

	/**
	 * Set the underlying data object, this is the first level of the data
	 * document.
	 */
	private void setDataObject(Object data) {
		theDataObject = data;
		setLSID(theDataObject, "");
		doInternalLSIDFill(theDataObject, SYSTEM_DEFAULT_LSID_PROVIDER);
	}

	/**
	 * Get the syntax type of this DataThing. The type string is based around
	 * application of the collection type constructors to a base MIME type. For
	 * example, t(s('text/plain')) is a tree of sets of TEXT/PLAIN items. The
	 * MIME type may be a comma separated list of types. Possible type
	 * constructors are t(..) for trees, s(..) for sets, l(..) for lists and
	 * p(..) for partial orders.
	 * <p>
	 * I would imagine that we'll mostly be dealing with types of 'text/plain',
	 * lists of same and maybe the occasional 'image/png' or similar, but I
	 * think this has enough flexibility to cover most things.
	 * <p>
	 * The type string "null" represents and empty DataThing and is the default
	 * value returned if the collection is empty.
	 */
	public String getSyntacticType() {
		return getSyntacticTypeForObject(theDataObject);
	}

	public String getMostInterestingMIMETypeForObject(Object o) {
		String typeString = getSyntacticTypeForObject(o);
		// logger.debug("Got types : "+typeString);
		String mimeTypes = typeString.split("'")[1].toLowerCase();
		for (int i = 0; i < interestingTypes.length; i++) {
			if (mimeTypes.matches(".*" + interestingTypes[i] + ".*")) {
				return interestingTypes[i];
			}
		}
		try {
			return mimeTypes.split(",")[0];
		} catch (Exception ex) {
			return "null";
		}
	}

	public String getSyntacticTypeForObject(Object o) {
		if (o instanceof Collection) {
			if (((Collection) o).isEmpty()) {
				if (o instanceof Set) {
					return "s('null')";
				}
				return "l('null')";
			}
			// Change to take the _last_ item of the List if it's a List rather
			// than using the iterator to take the first item
			Object innerObject;
			if (o instanceof List) {
			    List innerList = (List)o;
			    innerObject = innerList.get(innerList.size()-1);
			}
			else {
			    // Pull the first object out of the collection and recurse
			    innerObject = ((Collection) o).iterator().next();
			}
			if (o instanceof Set) {
				return ("s(" + getSyntacticTypeForObject(innerObject) + ")");
			} else if (o instanceof List) {
				return ("l(" + getSyntacticTypeForObject(innerObject) + ")");
			}
			// No idea what the collection is, return the most general
			// type constructor for a partial order
			return ("p(" + getSyntacticTypeForObject(innerObject) + ")");
		}
		// Not a collection, first see if there is any metadata
		// associated with the object that we can use to determine
		// the mime types
		try {
			SemanticMarkup markup = getMetadataForObject(o, false);
			List mimeTypeList = markup.getMIMETypeList();
			if (mimeTypeList.isEmpty()) {
				// If there is no MIME information in the markup object
				// then we have to revert to guesswork.
				throw new NoMetadataFoundException();
			}
			// Return a comma seperated list of MIME types within
			// single quotes.
			StringBuffer sb = new StringBuffer();
			sb.append("'");
			for (Iterator i = mimeTypeList.iterator(); i.hasNext();) {
				String mimeType = (String) i.next();
				sb.append(mimeType);
				if (i.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("'");
			return sb.toString();
		} catch (NoMetadataFoundException nmfe) {
			StringBuffer sb = new StringBuffer();
			// Try to annotate with mime types based on data object type
			if (o instanceof String) {
				getMetadata().addMIMEType("text/plain");
			} else if (o instanceof byte[]) {
				getMetadata().addMIMEType("application/octet-stream");
			} else {
				// Special magic MIME type for Java class
				// Destroy all existing mime types first if this is the
				// case!
				getMetadata().clearMIMETypes();
				getMetadata().addMIMEType("java/" + o.getClass().getName());
			}
			for (Iterator i = getMetadata().getMIMETypeList().iterator(); i
					.hasNext();) {
				sb.append((String) i.next());
				if (i.hasNext()) {
					sb.append(",");
				}
			}

			// Try to annotate with mime types based on data object type
			String specifiedMIMETypes = sb.toString();
			return ("'" + specifiedMIMETypes + "'");
		}
	}

	/**
	 * Copy the markup from the supplied DataThing object to this one. This is
	 * mainly used when the data has been repackaged, to preserve eg. LSID
	 * values.
	 */
	public void copyMetadataFrom(DataThing source) {
		lsid.putAll(source.lsid);
		metadataMap.putAll(source.metadataMap);
	}

	/**
	 * Link the metadata of this object to the specified datathing, similar in
	 * effect to the copy operation above in most cases but doesn't deep copy
	 * the metadata.
	 */
	public void linkMetadataFrom(DataThing source) {
		lsid = source.lsid;
		metadataMap = source.metadataMap;
	}

	/**
	 * Get the SemanticMarkup associated with an object in this DataThing. If
	 * there is no such metadata available the behavious depends upon the value
	 * of the supplyDefaults parameter. If false, then a
	 * NoMetadataFoundException is thrown, if true a new SemanticMarkup object
	 * is created, stored in the dictionary and returned to the caller.
	 */
	public SemanticMarkup getMetadataForObject(Object theObject,
			boolean supplyDefault) throws NoMetadataFoundException {
		WeakReference<SemanticMarkup> ref = metadataMap.get(theObject);
		if (ref != null && ref.get() != null) {
			return ref.get();
		}
		if (supplyDefault == false) {
			throw new NoMetadataFoundException("No metadata available");
		}
		// Create a new markup object and store
		// it bound to the object specified
		SemanticMarkup theMarkup = new SemanticMarkup(theObject);
		metadataMap.put(theObject, new WeakReference<SemanticMarkup>(theMarkup));
		return theMarkup;
	}

	/**
	 * Return the JDom Element corresponding to this DataThing represented as
	 * XML
	 */
	public Element getElement() {
		return DataThingXMLFactory.getElement(this);
	}

	/**
	 * Extract a child object as a DataThing. This assumes that the object
	 * referenced is actually within this DataThing object, behaviour is
	 * undefined otherwise. Although we could check this and impose it as a
	 * constraint the performance hit of traversing the entire original
	 * DataThing collection structure and doing potentially expensive
	 * equivalence computations is probably not worth it. Use with care.
	 * 
	 * @return a view on an object contained within this DataThing as a new
	 *         DataThing
	 */
	public DataThing extractChild(Object child) {
		DataThing result = new DataThing(this);
		result.theDataObject = child;
		return result;
	}

	/**
	 * Iterate over all imediate children. If there are no children, return an
	 * iterator over nothing. All children will be viewed as DataThing
	 * instances.
	 * 
	 * @return an Iterator over all children
	 */
	@SuppressWarnings("unchecked")
	public Iterator<DataThing> childIterator() {
		if (theDataObject instanceof Collection) {
			List<DataThing> dataThingList = new ArrayList<DataThing>();
			for (Iterator i = ((Collection) theDataObject).iterator(); i
					.hasNext();) {
				DataThing newThing = new DataThing(i.next());
				newThing.metadataMap = metadataMap;
				newThing.myMarkup = new SemanticMarkup(myMarkup);
				newThing.lsid = lsid;
				dataThingList.add(newThing);
				// dataThingList.add(extractChild(i.next()));
			}
			return dataThingList.iterator();
		}
		return Collections.EMPTY_LIST.iterator();
	}

	/**
	 * Given a desired type, return the BaclavaIterator that provides DataThing
	 * objects of this type. If the desired collection structure is not
	 * contained by this DataThing then an exception is thrown.
	 * 
	 * @exception IntrospectionException
	 *                thrown if the supplied type is not contained within the
	 *                current DataThing type.
	 */
	public BaclavaIterator iterator(String desiredType)
			throws IntrospectionException {
		String type = null;
		String currentType = null;
		try {
			type = desiredType.split("\\'")[0];
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			type = "";
		}
		try {
			currentType = getSyntacticType().split("\\'")[0];
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			currentType = "";
		}
		// At this point, we should have split the mime types away from the
		// collection types, so the current type looks like, for example, l() or
		// s(l())

		// If the strings are the same then we return an iterator with a single
		// item
		// in it, namely the current DataThing; this is needed where the enactor
		// has
		// detected that iteration is required somewhere else using the join
		// iterator
		if (type.equals(currentType)) {
			List<DataThing> dataThingList = new ArrayList<DataThing>();
			dataThingList.add(this);
			return new BaclavaIterator(dataThingList);
		}

		// Now need to check that the conversion is valid, so either the
		// input type is the empty string (iterate over everything to produce
		// leaf nodes)
		// or it is a substring of the collection type
		if (type.equals("") || currentType.endsWith(type)) {
			// See how deep the iterator needs to go.
			int iterationDepth = (currentType.length() - type.length()) / 2;
			// Now drill down into the data structure that number of levels,
			// build a list of all the items into a new collection, iterate over
			// this list building the DataThing objects and return the iterator
			// over that list (and breathe...)
			List<Object> targetList = new ArrayList<Object>();
			List<int[]> indexList = new ArrayList<int[]>();
			drill(iterationDepth, targetList, indexList, new int[0],
					(Collection) theDataObject);
			// Now iterate over the target list creating new DataThing objects
			// from it
			/**
			 * for (Iterator i = targetList.iterator(); i.hasNext(); ) {
			 * DataThing newThing = new DataThing(i.next()); // Copy any
			 * metadata into the new datathing
			 * newThing.metadataMap.putAll(metadataMap);
			 * newThing.lsid.putAll(lsid); dataThingList.add(newThing); }
			 */
			// return new BaclavaIterator(dataThingList, indexList);
			return new BaclavaIterator(this, targetList, indexList);
		}
		throw new IntrospectionException(
				"Incompatible types for iterator, cannot extract " + type
						+ " from " + getSyntacticType());

	}

	/**
	 * Drill into a collection searching for a data-object that has the same
	 * data with oldDT and replace its data with newData.
	 * 
	 * @return true if a datathing's dataObject has been replaced with the
	 *         newData.
	 */
	@SuppressWarnings("unchecked")
	public DataThing drillAndSet(DataThing oldDT, String newData) {
		if (theDataObject instanceof Collection) {
			ArrayList<Object> dtList = new ArrayList<Object>((Collection) theDataObject);
			for (int i = 0; i < dtList.size(); i++) {
				DataThing tmp = new DataThing(dtList.get(i));
				tmp.metadataMap = metadataMap;
				tmp.myMarkup = new SemanticMarkup(myMarkup);
				tmp.lsid = lsid;
				DataThing newDT = tmp.drillAndSet(oldDT, newData);
				if (newDT != null) {
					dtList.set(i, newDT.getDataObject());
					setDataObject(dtList);
					return this;
				}
			}
			return null;
		} else if (getDataObject().equals(oldDT.getDataObject())) {
			setDataObject(new String(newData));
			return this;
		} else
			return null;
	}

	/*
	 * public void drillAndPrint(){ System.out.print("-");
	 * System.out.println(toString()); for (Iterator
	 * iDT=childIterator();iDT.hasNext();){ System.out.print("-");
	 * ((DataThing)iDT.next()).drillAndPrint(); } }
	 */

	/**
	 * Drill into a collection, adding items to the list if we're at the desired
	 * depth, this makes the underlying assumption that the collection contains
	 * either collections or objects, but never a mix of both.
	 */
	private void drill(int iterationDepth, List<Object> targetList, List<int[]> indexList,
			int[] currentIndex, Collection theDataObject) {
		if (iterationDepth == 1) {
			// Collecting items
			int localIndex = 0;
			for (Iterator i = theDataObject.iterator(); i.hasNext();) {
				targetList.add(i.next());
				indexList.add(append(currentIndex, localIndex++));
			}
		} else {
			// Iterating further down
			int localIndex = 0;
			for (Iterator i = theDataObject.iterator(); i.hasNext();) {
				Collection theCollection = (Collection) i.next();
				drill(iterationDepth - 1, targetList, indexList, append(
						currentIndex, localIndex++), theCollection);
			}
		}
	}

	/**
	 * Utility class, append an int onto an int array and return the new array
	 */
	private int[] append(int[] list, int head) {
		int[] newlist = new int[list.length + 1];
		System.arraycopy(list, 0, newlist, 0, list.length);
		newlist[list.length] = head;
		return newlist;
	}

	public String toString() {
		String datStr = theDataObject.toString();
		boolean trimmed = false;
		int nl = datStr.indexOf('\n');
		if (nl > -1) {
			datStr = datStr.substring(0, nl);
			trimmed = true;
		}
		if (datStr.length() > 30) {
			datStr = datStr.substring(0, 27);
			trimmed = true;
		}
		if (trimmed) {
			datStr += "...";
		}
		return super.toString() + "\n\tValue=" + datStr + "\n\tLSID="
				+ getLSID(theDataObject) + "\n";
	}

	/**
	 * Writes the contents of the DataThing into the specified directory using
	 * the given name. If there is only one item a single file is created
	 * otherwise a directory structure mirroring the collection structure is
	 * built. A File object representing the file or directory that has been
	 * written is returned.
	 */
	public File writeToFileSystem(File destination, String name)
			throws IOException {
		// Check for the most interesting type, if defined
		String interestingType = getMostInterestingMIMETypeForObject(theDataObject);
		String fileExtension = ".text";
		if (interestingType != null
				&& interestingType.equals("text/plain") == false) {
			// MIME types look like 'foo/bar'
			String lastPart = interestingType.split("/")[1];
			if (lastPart.startsWith("x-") == false) {
				fileExtension = "." + lastPart;
			}
		}
		File writtenFile = DataThing.writeObjectToFileSystem(destination, name,
				theDataObject, fileExtension);
		return writtenFile;
	}

	/**
	 * Write a specific object to the filesystem this has no access to metadata
	 * about the object and so is not particularly clever. A File object
	 * representing the file or directory that has been written is returned.
	 */
	public static File writeObjectToFileSystem(File destination, String name,
			Object o, String defaultExtension) throws IOException {
		// If the destination is not a directory then set the destination
		// directory to the parent and the name to the filename
		// i.e. if the destination is /tmp/foo.text and this exists
		// then set destination to /tmp/ and name to 'foo.text'
		if (destination.exists() && destination.isFile()) {
			name = destination.getName();
			destination = destination.getParentFile();
		}
		if (destination.exists() == false) {
			// Create the directory structure if not already present
			destination.mkdirs();
		}
		File writtenFile = writeDataObject(destination, name, o,
				defaultExtension);
		return writtenFile;
	}

	static char sep = File.separatorChar;

	private static File writeDataObject(File destination, String name,
			Object o, String defaultExtension) throws IOException {
		if (o instanceof Collection) {
			// Create a new directory, iterate over the collection recursively
			// calling this method
			File targetDir = new File(destination.toString() + sep + name);
			targetDir.mkdir();
			int count = 0;
			Collection c = (Collection) o;
			for (Iterator i = c.iterator(); i.hasNext();) {
				writeDataObject(targetDir, "" + count++, i.next(),
						defaultExtension);
			}
			return targetDir;
		}

		else {
			// Write a single item
			if (o instanceof String && defaultExtension == null) {
				name = name + ".text";
			} else {
				if (defaultExtension != null) {
					name = name + defaultExtension;
				}
			}
			File targetFile = new File(destination.toString() + sep + name);
			FileOutputStream fos = new FileOutputStream(targetFile);
			if (o instanceof byte[]) {
				fos.write((byte[]) o);
				fos.flush();
				fos.close();
			} else if (o instanceof String) {
				PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));
				out.print((String) o);
				out.flush();
				out.close();
			}
			return targetFile;
		}
	}

	public Object clone() {
		ByteArrayOutputStream bin = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(bin);
			out.writeObject(this);
			byte[] buf = bin.toByteArray();
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(buf));
			return in.readObject();
		} catch (IOException e) {
			logger.error("Could not clone", e);
			return null;
		} catch (ClassNotFoundException e) {
			logger.error("Could not clone", e);
			return null;
		}
	}
}
