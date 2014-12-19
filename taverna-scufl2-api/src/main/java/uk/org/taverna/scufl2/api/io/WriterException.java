package uk.org.taverna.scufl2.api.io;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * Thrown when there is a problem writing a {@link WorkflowBundle}
 * 
 * @see WorkflowBundleIO#writeBundle(WorkflowBundle, java.io.File, String)
 * @see WorkflowBundleIO#writeBundle(WorkflowBundle, java.io.OutputStream,
 *      String)
 * @see WorkflowBundleWriter#writeBundle(WorkflowBundle, java.io.File, String)
 * @see WorkflowBundleWriter#writeBundle(WorkflowBundle, java.io.OutputStream,
 *      String)
 */
@SuppressWarnings("serial")
public class WriterException extends Exception {

	/**
	 * Constructs an exception with no message or cause.
	 */
	public WriterException() {
	}

	/**
	 * Constructs an exception with the specified message and no cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 */
	public WriterException(String message) {
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
	public WriterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an exception with the specified cause and and the same message
	 * as the cause (if the cause is not null).
	 * 
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public WriterException(Throwable cause) {
		super(cause);
	}
}
