package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;

public class Property {
	public static enum PropertyType {
		DataProperty, ObjectProperty
	}

	protected static void appendString(StringBuilder sb, String string) {
		sb.append('"');
		String escaped = string.replace("\\", "\\\\").replace("\"", "\\\"");
		sb.append(escaped);
		sb.append('"');
	}

	protected static void appendUri(StringBuilder sb, URI uri) {
		if (uri == null) {
			sb.append("rdf:nil");
			return;
		}
		sb.append('<');
		sb.append(uri.toASCIIString());
		sb.append('>');
	}

	private PropertyType propertyType = PropertyType.ObjectProperty;
	private URI subject;
	private URI predicate;

	private URI objectUri;

	private String objectValue;

	/**
	 * Two Property instances are only equal if the subjects are both non-null
	 * and equal, the predicates are both null or equal, they are of the same
	 * type, and their objects are both null or equal.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Property other = (Property) obj;
		if (getPropertyType() != other.getPropertyType()) {
			return false;
		}

		if (getSubject() == null || !getSubject().equals(other.getSubject())) {
			// Both null: might be separate statements about different subjects
			return false;
		}

		if (getPredicate() == null) {
			if (other.getPredicate() != null) {
				return false;
			}
		} else if (!getPredicate().equals(other.getPredicate())) {
			return false;
		}

		if (getPropertyType().equals(PropertyType.DataProperty)) {
			if (getObjectValue() == null) {
				if (other.getObjectValue() != null) {
					return false;
				}
			} else if (!getObjectValue().equals(other.getObjectValue())) {
				return false;
			}
		} else {
			if (getObjectUri() == null) {
				if (other.getObjectUri() != null) {
					return false;
				}
			} else if (!getObjectUri().equals(other.getObjectUri())) {
				return false;
			}
		}
		return true;

	}

	public URI getObjectUri() {
		return objectUri;
	}

	public String getObjectValue() {
		return objectValue;
	}

	public URI getPredicate() {
		return predicate;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public URI getSubject() {
		return subject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ (objectUri == null ? 0 : objectUri.hashCode());
		result = prime * result
		+ (objectValue == null ? 0 : objectValue.hashCode());
		result = prime * result
		+ (predicate == null ? 0 : predicate.hashCode());
		result = prime * result
		+ (propertyType == null ? 0 : propertyType.hashCode());
		result = prime * result + (subject == null ? 0 : subject.hashCode());
		return result;
	}

	public void setObjectUri(URI objectUri) {
		this.objectUri = objectUri;
		setPropertyType(PropertyType.ObjectProperty);
	}

	public void setObjectValue(String objectValue) {
		this.objectValue = objectValue;
		if (objectValue == null) {
			setPropertyType(PropertyType.ObjectProperty);
		} else {
			setPropertyType(PropertyType.DataProperty);
		}
	}

	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}

	protected void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
		// Clear the other property
		if (propertyType == PropertyType.ObjectProperty) {
			objectValue = null;
		} else { // DataProperty
			objectUri = null;
		}
	}

	public void setSubject(URI subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		// Give some rough Turtle view of the property

		StringBuilder sb = new StringBuilder();
		if (subject != null) {
			appendUri(sb, subject);
		} else {
			sb.append('[');
		}
		sb.append(' ');
		appendUri(sb, predicate);
		sb.append(' ');

		if (propertyType == PropertyType.ObjectProperty) {
			appendUri(sb, objectUri);
		} else { // DataProperty
			appendString(sb, objectValue);
		}

		if (subject == null) {
			sb.append(" ]");
		}
		sb.append(" .");
		return sb.toString();
	}

}
