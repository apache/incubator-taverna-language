package uk.org.taverna.scufl2.api.property;

import java.net.URI;

/**
 * Thrown when a {@link PropertyObject} other than the expected one is encountered.
 */
public class UnexpectedPropertyException extends PropertyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4036096310976859256L;

	public UnexpectedPropertyException(String message, URI predicate,
			PropertyResource propertyResource) {
		super(message, predicate, propertyResource);
	}

}
