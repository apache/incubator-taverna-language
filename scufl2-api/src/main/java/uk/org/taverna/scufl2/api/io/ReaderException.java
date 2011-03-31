package uk.org.taverna.scufl2.api.io;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * Thrown when there is a problem reading a {@link WorkflowBundle}
 * 
 * @see WorkflowBundleIO#readBundle(java.io.File, String)
 * @see WorkflowBundleIO#readBundle(java.io.InputStream, String)
 * @see WorkflowBundleIO#readBundle(java.net.URL, String)
 * @see WorkflowBundleReader#readBundle(java.io.File, String)
 * @see WorkflowBundleReader#readBundle(java.io.InputStream, String)
 */
public class ReaderException extends Exception {

	/**
	 * Constructs an exception with no message or cause.
	 */
	public ReaderException() {
	}

	/**
	 * Constructs an exception with the specified message and no cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 */
	public ReaderException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the specified message and cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public ReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an exception with the specified cause and and the same message as the cause (if
	 * the cause is not null).
	 * 
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public ReaderException(Throwable cause) {
		super(cause);
	}

}
