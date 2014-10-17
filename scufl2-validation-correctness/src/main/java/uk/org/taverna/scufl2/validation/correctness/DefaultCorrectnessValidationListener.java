/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.net.URI;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.api.port.AbstractGranularDepthPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.validation.ValidationException;

/**
 * @author alanrw
 */
public class DefaultCorrectnessValidationListener implements
		CorrectnessValidationListener {
	@Override
	public void emptyIterationStrategyTopNode(IterationStrategyTopNode bean) {
	}

	@Override
	public void mismatchConfigurableType(Configuration bean,
			Configurable configures) {
	}

	@Override
	public void negativeValue(WorkflowBean bean, String fieldName,
			Integer fieldValue) {
	}

	@Override
	public void nonAbsoluteURI(WorkflowBean bean, String fieldName,
			URI fieldValue) {
	}

	@Override
	public void nullField(WorkflowBean bean, String fieldName) {
	}

	@Override
	public void outOfScopeValue(WorkflowBean bean, String fieldName,
			Object value) {
	}

	@Override
	public void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode) {
	}

	@Override
	public void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean) {
	}

	@Override
	public void wrongParent(Child<?> iap) {
	}

	@Override
	public void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth) {
	}

	@Override
	public boolean detectedProblems() {
		return false;
	}

	@Override
	public ValidationException getException() {
		return null;
	}
}
