/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package org.apache.taverna.baclava;

// Utility Imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class SemanticMarkup implements Serializable {

	private String description = "";

	private String semanticType = "";

	protected List mimeTypeList = new ArrayList();

	protected Object subject = null;

	/**
	 * Create a new item of semantic markup for the Object specified. This
	 * should be interpreted as 'this markup object applies to the supplied
	 * Object'.
	 */
	public SemanticMarkup(Object subject) {
		super();
		this.subject = subject;
	}

	public SemanticMarkup(SemanticMarkup other) {
		super();
		this.description = other.description;
		this.semanticType = other.semanticType;
		this.mimeTypeList.addAll(other.mimeTypeList);
		this.subject = other.subject;
	}

	// fixme:
	// We should make either both getMIMETypes() and getMIMETypeList()
	// return a copy or the original data. +1 for copy [mrp]
	// fixme:
	// These two calls are redundant - let's use one or the other. List is
	// used 2x, String[] is used 10x [mrp]

	/**
	 * Get hold of the List used to hold the MIME types, useful for UI
	 * components. The returned list is owned by this object.
	 */
	public List getMIMETypeList() {
		return this.mimeTypeList;
	}

	/**
	 * Set the free text description
	 */
	public void setDescription(String theDescription) {
		this.description = theDescription;
	}

	/**
	 * Get the free text description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the Object that is the subject of this markup, if that's not too
	 * confusing a way of expressing it... This cannot be altered once the
	 * object is created, I'm not entirely sure this is actually a requirement
	 * but I can't think of a good reason to do otherwise.
	 */
	public Object getSubject() {
		return this.subject;
	}

	/**
	 * Get an array of strings containing MIME types for the item this markup
	 * object applies to. The returned array is owned by the caller.
	 */
	public String[] getMIMETypes() {
		synchronized (this.mimeTypeList) {
			return (String[]) mimeTypeList.toArray(new String[0]);
		}
	}

	/**
	 * Get the first MIME type in the list, or return the empty string if no
	 * MIME types have been defined.
	 */
	public String getFirstMIMEType() {
		synchronized (this.mimeTypeList) {
			if (mimeTypeList.isEmpty()) {
				return "";
			} else {
				return (String) mimeTypeList.get(0);
			}
		}
	}

	/**
	 * Get the MIME types as a single string with new lines seperating the types
	 */
	public String getDisplayTypeList() {
		StringBuffer sb = new StringBuffer();
		synchronized (this.mimeTypeList) {
			for (Iterator i = mimeTypeList.iterator(); i.hasNext();) {
				sb.append((String) i.next() + "\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Clear the array of MIME types
	 */
	public void clearMIMETypes() {
		synchronized (this.mimeTypeList) {
			this.mimeTypeList.clear();
		}
	}

	/**
	 * Add a MIME type
	 */
	public void addMIMEType(String mimeType) {
		synchronized (this.mimeTypeList) {
			if (mimeType != null && mimeType.equals("") == false) {
				// fixme:
				// mimeTypeList.contains(mimeType) may be more efficient [mrp]
				String[] types = mimeType.split(",");
				for (int j = 0; j < types.length; j++) {
					boolean foundType = false;
					for (Iterator i = this.mimeTypeList.iterator(); i.hasNext();) {
						// fixme:
						// is it intended that we double-check each element is a
						// string?
						// is this not checked out earlier? [mrp]
						if (((String) i.next()).equals(types[j])) {
							// Bail if we already have one
							foundType = true;
						}
					}
					if (!foundType) {
						this.mimeTypeList.add(types[j]);
					}
				}
			}
		}
	}

	public void setMIMETypes(List mimeTypes) {
		synchronized (this.mimeTypeList) {
			this.mimeTypeList.clear();
			for (Iterator i = mimeTypes.iterator(); i.hasNext();) {
				String mt = (String) i.next();
				if (!this.mimeTypeList.contains(mt)) {
					this.mimeTypeList.add(mt);
				}
			}

		}
	}

	/**
	 * Get the string of semantic markup text FIXME - this is currently
	 * unstructured free text, which it almost certainly shouldn't be.
	 */
	public String getSemanticType() {
		return this.semanticType;
	}

	/**
	 * Set the semantic markup as a string, not the best way to do things but
	 * will have to do for now
	 */
	public void setSemanticType(String newSemanticType) {
		if (newSemanticType != null) {
			this.semanticType = newSemanticType;
		}
	}

	/**
	 * Configure this markup object from the supplied XML element. This is
	 * assuming that the element passed in is the 'metadata' element in the
	 * XScufl namespace.
	 */
	public void configureFromElement(Element theElement) {
		// Do mime types
		Element mimeTypeListElement = theElement.getChild("mimeTypes",
				XScufl.XScuflNS);
		if (mimeTypeListElement != null) {
			for (Iterator i = mimeTypeListElement.getChildren("mimeType",
					XScufl.XScuflNS).iterator(); i.hasNext();) {
				Element typeElement = (Element) i.next();
				addMIMEType(typeElement.getTextTrim());
			}
		}
		// Do description
		Element descriptionElement = theElement.getChild("description",
				XScufl.XScuflNS);
		if (descriptionElement != null) {
			this.description = descriptionElement.getTextTrim();
		}
		// Do semantic type
		Element semanticTypeElement = theElement.getChild("semanticType",
				XScufl.XScuflNS);
		if (semanticTypeElement != null) {
			this.semanticType = semanticTypeElement.getTextTrim();
		}
	}

	/**
	 * Emit an element that would be used to configure this object in the method
	 * above
	 */
	public Element getConfigurationElement() {
		return getConfigurationElement(new ArrayList());
	};

	/**
	 * Emit an element that would be used to configure this object in the method
	 * above, do not emit mime types that occur within the supplied list.
	 */
	public Element getConfigurationElement(List knownMIMETypes) {
		Element topElement = new Element("metadata", XScufl.XScuflNS);
		// Store MIME types
		Element mimeTypeList = new Element("mimeTypes", XScufl.XScuflNS);
		synchronized (this.mimeTypeList) {
			boolean addedMIME = false;
			for (Iterator i = this.mimeTypeList.iterator(); i.hasNext();) {
				Element typeElement = new Element("mimeType", XScufl.XScuflNS);
				String mimeType = (String) i.next();
				typeElement.setText(mimeType);
				boolean addMIME = true;
				for (Iterator j = knownMIMETypes.iterator(); j.hasNext();) {
					String knownType = (String) j.next();
					if (mimeType.equalsIgnoreCase(knownType)) {
						addMIME = false;
					}
				}
				if (addMIME) {
					addedMIME = true;
					mimeTypeList.addContent(typeElement);
				}
			}
			if (addedMIME) {
				topElement.addContent(mimeTypeList);
			}
		}
		// Store free text description
		if (this.description.equals("") == false) {
			Element descriptionElement = new Element("description",
					XScufl.XScuflNS);
			topElement.addContent(descriptionElement);
			descriptionElement.setText(this.description);
		}
		// Store semantic type, still as text for now
		if (this.semanticType.equals("") == false) {
			Element semanticTypeElement = new Element("semanticType",
					XScufl.XScuflNS);
			topElement.addContent(semanticTypeElement);
			semanticTypeElement.setText(this.semanticType);
		}
		return topElement;
	}

	public String toString() {
		return super.toString() + " description=" + description
				+ " semanticType=" + semanticType + " mimeTypes="
				+ mimeTypeList;
	}
}
