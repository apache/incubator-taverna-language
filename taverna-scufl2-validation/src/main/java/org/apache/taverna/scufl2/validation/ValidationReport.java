package org.apache.taverna.scufl2.validation;

public interface ValidationReport {
	/**
	 * @return Whether any problems were detected during the validation.
	 */
	boolean detectedProblems();

	/**
	 * @return An exception to throw to report the problems, or <tt>null</tt> if
	 *         there are no problems to report.
	 */
	ValidationException getException();
}
