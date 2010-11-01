package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;

public abstract class Property {

	protected static void appendString(StringBuilder sb, String string) {
		sb.append('"');
		String escaped = string.replace("\\", "\\\\").replace("\"", "\\\"");
		sb.append(escaped);
		sb.append('"');
	}

	protected static void appendUri(StringBuilder sb, URI uri) {
		if (uri == null) {
			sb.append("[]");
			return;
		}
		sb.append('<');
		sb.append(uri.toASCIIString());
		sb.append('>');
	}

	private URI predicate;

	public URI getPredicate() {
		return predicate;
	}

	public void setPredicate(URI predicate) {
		if (predicate == null) {
			throw new NullPointerException("Predicate can't be null");
		}
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		// Give some rough Turtle view of the property

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(' ');
		appendUri(sb, predicate);
		sb.append(' ');

		toStringObject(sb, "");

		sb.append(" ]");
		sb.append(" .");
		return sb.toString();
	}

	protected abstract void toStringObject(StringBuilder sb, String indent);

}
