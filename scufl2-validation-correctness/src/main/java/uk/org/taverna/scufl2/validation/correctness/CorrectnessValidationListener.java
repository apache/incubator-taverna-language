/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.api.port.AbstractGranularDepthPort;
import uk.org.taverna.scufl2.api.port.Port;

/**
 * @author alanrw
 *
 */
public interface CorrectnessValidationListener {

	void emptyIterationStrategyTopNode(IterationStrategyTopNode bean);

	void mismatchConfigurableType(Configuration bean, Configurable configures);

	void nonAbsoluteGlobalBaseURI(Root bean);

	void nullField(WorkflowBean bean, String string);

	void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode);

	void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean);

	void wrongParent(Child iap);

	void negativeValue(WorkflowBean bean, String fieldName,
			Integer fieldValue);

	void outOfScopeValue(WorkflowBean bean, String fieldName,
			Object value);

	void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth);

}
