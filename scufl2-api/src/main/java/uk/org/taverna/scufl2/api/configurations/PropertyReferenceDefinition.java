package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.LinkedHashSet;

import uk.org.taverna.scufl2.api.property.PropertyReference;

/**
 * The definition of a {@link PropertyReference}.
 * <p>
 *
 *
 * @author Stian Soiland-Reyes
 *
 */
public class PropertyReferenceDefinition extends PropertyDefinition {

	private LinkedHashSet<URI> options;

	/**
	 * Create a definition of a {@link PropertyReference}.
	 */
	public PropertyReferenceDefinition() {
		options = new LinkedHashSet<URI>();
	}

	/**
	 * Create a definition of an {@link PropertyReference}.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>PropertyReference</code> that
	 *            this class defines
	 * @param name
	 *            the name of the <code>PropertyReference</code>
	 * @param label
	 *            a human readable label for the <code>PropertyReference</code>
	 * @param description
	 *            a description of the <code>PropertyReference</code>
	 * @param required
	 *            whether the <code>PropertyReference</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the
	 *            <code>PropertyReference</code>
	 * @param ordered
	 *            whether the order of multiple instances of the
	 *            <code>Property</code> is significant
	 */
	public PropertyReferenceDefinition(URI predicate, String name,
			String label, String description, boolean required,
			boolean multiple, boolean ordered) {
		super(predicate, name, label, description, required, multiple, ordered);
		options = new LinkedHashSet<URI>();
	}

	/**
	 * Create a definition of an {@link PropertyReference}.
	 * 
	 * @param predicate
	 *            the URI identifying the <code>PropertyReference</code> that
	 *            this class defines
	 * @param name
	 *            the name of the <code>PropertyReference</code>
	 * @param label
	 *            a human readable label for the <code>PropertyReference</code>
	 * @param description
	 *            a description of the <code>PropertyReference</code>
	 * @param required
	 *            whether the <code>PropertyReference</code> is mandatory
	 * @param multiple
	 *            whether there can be multiple instances of the
	 *            <code>PropertyReference</code>
	 * @param ordered
	 *            whether the order of multiple instances of the
	 *            <code>Property</code> is significant
	 * @param options
	 *            Set of options
	 */
	public PropertyReferenceDefinition(URI predicate, String name,
			String label, String description, boolean required,
			boolean multiple, boolean ordered, LinkedHashSet<URI> options) {
		super(predicate, name, label, description, required, multiple, ordered);
		this.options = options;
	}

	public LinkedHashSet<URI> getOptions() {
		return options;
	}

	public void setOptions(LinkedHashSet<URI> options) {
		this.options = options;
	}

	@Override
	protected String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent);
		sb.append("PropertyReferenceDefinition ");
		sb.append(getPredicate());
		sb.append("\n");
		sb.append(indent);
		sb.append(" label=" + getLabel() + ", description=" + getDescription()
				+ ", required=" + isRequired() + ", multiple=" + isMultiple()
				+ ", ordered=" + isOrdered());
		return sb.toString();
	}

}
