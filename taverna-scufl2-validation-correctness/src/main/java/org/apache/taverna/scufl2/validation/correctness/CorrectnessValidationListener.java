/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness;

import java.net.URI;

import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.port.AbstractGranularDepthPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.validation.ValidationReport;


/**
 * @author alanrw
 */
public interface CorrectnessValidationListener extends ValidationReport {
	void emptyIterationStrategyTopNode(IterationStrategyTopNode bean);

	void mismatchConfigurableType(Configuration bean, Configurable configures);

	void nonAbsoluteURI(WorkflowBean bean, String fieldName, URI fieldValue);

	void nullField(WorkflowBean bean, String string);

	void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode);

	void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean);

	void wrongParent(Child<?> iap);

	void negativeValue(WorkflowBean bean, String fieldName, Integer fieldValue);

	void outOfScopeValue(WorkflowBean bean, String fieldName, Object value);

	void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth);
}
