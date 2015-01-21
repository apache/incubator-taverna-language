/**
 * 
 */
package org.apache.taverna.scufl2.validation;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class ValidationException extends Exception {
	public ValidationException(String string) {
		super(string);
	}

	public ValidationException(String string, Throwable throwable) {
		super(string, throwable);
	}
}
