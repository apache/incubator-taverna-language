package uk.org.taverna.scufl2.api.property;

import java.net.URI;

public class UnexpectedPropertyException extends PropertyException {

	public UnexpectedPropertyException(String message, URI predicate,
			PropertyResource propertyResource) {
		super(message, predicate, propertyResource);
	}

}
