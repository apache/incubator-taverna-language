package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ObjectProperty extends Property {

	private URI objectUri;
	private URI objectClass;
	private List<Property> objectProperties = new ArrayList<Property>();

	public ObjectProperty() {
	}

	public ObjectProperty(URI predicate, URI objectUri) {
		setPredicate(predicate);
		setObjectUri(objectUri);
	}

	public ObjectProperty(URI predicate, URI objectUri, URI objectClass) {
		setPredicate(predicate);
		setObjectUri(objectUri);
		setObjectClass(objectClass);
	}

	public URI getObjectClass() {
		return objectClass;
	}

	public List<Property> getObjectProperties() {
		return objectProperties;
	}

	public URI getObjectUri() {
		return objectUri;
	}

	public void setObjectClass(URI objectClass) {
		this.objectClass = objectClass;
	}

	public void setObjectProperties(List<Property> objectProperties) {
		if (objectProperties == null) {
			throw new NullPointerException("Properties can't be null");
		}
		this.objectProperties = objectProperties;
	}

	public void setObjectUri(URI objectUri) {
		this.objectUri = objectUri;
	}

	@Override
	protected void toStringObject(StringBuilder sb, String indent) {

		String newIndent = indent + "    ";

		if (!getObjectProperties().isEmpty() || getObjectClass() != null) {
			sb.append("[\n");
			if (getObjectClass() != null) {
				sb.append(newIndent);
				sb.append("a ");
				appendUri(sb, getObjectClass());
				sb.append(";\n");
			}
			if (getObjectUri() != null) {
				sb.append(newIndent);
				sb.append("= ");
				appendUri(sb, getObjectUri());
				sb.append(";\n");
			}

			for (Property p : getObjectProperties()) {
				sb.append(newIndent);
				appendUri(sb, p.getPredicate());
				sb.append(" ");
				p.toStringObject(sb, newIndent);
				sb.append(";\n");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append("]");
		} else {
			appendUri(sb, getObjectUri());
		}

	}

}
