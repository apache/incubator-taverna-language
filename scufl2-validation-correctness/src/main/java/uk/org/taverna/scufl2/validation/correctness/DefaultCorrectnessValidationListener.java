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
public class DefaultCorrectnessValidationListener implements
		CorrectnessValidationListener {

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#emptyIterationStrategyTopNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode)
	 */
	@Override
	public void emptyIterationStrategyTopNode(IterationStrategyTopNode bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#mismatchConfigurableType(uk.org.taverna.scufl2.api.configurations.Configuration, uk.org.taverna.scufl2.api.common.Configurable)
	 */
	@Override
	public void mismatchConfigurableType(Configuration bean,
			Configurable configures) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#negativeValue(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void negativeValue(WorkflowBean bean, String fieldName,
			Integer fieldValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#nonAbsoluteGlobalBaseURI(uk.org.taverna.scufl2.api.common.Root)
	 */
	@Override
	public void nonAbsoluteURI(WorkflowBean bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#nullField(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.String)
	 */
	@Override
	public void nullField(WorkflowBean bean, String fieldName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#outOfScopeValue(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.String, java.lang.Object)
	 */
	@Override
	public void outOfScopeValue(WorkflowBean bean, String fieldName,
			Object value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#portMentionedTwice(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode, uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode)
	 */
	@Override
	public void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#portMissingFromIterationStrategyStack(uk.org.taverna.scufl2.api.port.Port, uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack)
	 */
	@Override
	public void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#wrongParent(uk.org.taverna.scufl2.api.common.Child)
	 */
	@Override
	public void wrongParent(Child iap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth) {
		// TODO Auto-generated method stub
		
	}

}
