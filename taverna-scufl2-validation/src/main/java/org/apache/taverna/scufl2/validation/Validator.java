package org.apache.taverna.scufl2.validation;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * How to check a workflow bundle for validity in some sense.
 * 
 * @param <T>
 *            The type of the validation reports produced by this validator.
 * @author Donal Fellows
 */
public interface Validator<T extends ValidationReport> {
	/**
	 * Validate the given workflow bundle.
	 * 
	 * @param workflowBundle
	 *            The bundle to validate.
	 * @return A description of whether the bundle is valid, and if not, how it
	 *         is invalid. (Determining the nature of the invalidity may require
	 *         knowing more about the nature of the validator than this
	 *         interface describes.)
	 */
	T validate(WorkflowBundle workflowBundle);
}
