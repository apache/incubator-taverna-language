package uk.org.taverna.scufl2.api.property;

import java.net.URI;

/**
 * Thrown when an error occured when trying to retrieve a property.
 * 
 * @see PropertyNotFoundException
 * @see MultiplePropertiesException
 * @see UnexpectedPropertyException
 * 
 * @author Stian Soiland-Reyes
 */
public abstract class PropertyException extends Exception {

	private static final long serialVersionUID = -3331261486445335308L;

	private final URI predicate;
	private final PropertyResource propertyResource;

	public PropertyException(String message, URI predicate,
			PropertyResource propertyResource) {
		super(message);
		this.predicate = predicate;
		this.propertyResource = propertyResource;
	}

	public URI getPredicate() {
		return predicate;
	}

	public PropertyResource getPropertyResource() {
		return propertyResource;
	}

}