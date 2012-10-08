/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness.report;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;

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
import uk.org.taverna.scufl2.validation.ValidationException;
import uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener;

/**
 * @author alanrw
 *
 */
public class ReportCorrectnessValidationListener implements
		CorrectnessValidationListener {

	HashSet<EmptyIterationStrategyTopNodeProblem> emptyIterationStrategyTopNodeProblems = new HashSet<EmptyIterationStrategyTopNodeProblem> ();
	HashSet<MismatchConfigurableTypeProblem> mismatchConfigurableTypeProblems = new HashSet<MismatchConfigurableTypeProblem>();
	HashSet<NegativeValueProblem> negativeValueProblems = new HashSet<NegativeValueProblem>();

	HashSet<NonAbsoluteURIProblem> nonAbsoluteURIProblems = new HashSet<NonAbsoluteURIProblem>();
	HashSet<NullFieldProblem> nullFieldProblems = new HashSet<NullFieldProblem>();
	HashSet<OutOfScopeValueProblem> outOfScopeValueProblems = new HashSet<OutOfScopeValueProblem>();
	HashSet<PortMentionedTwiceProblem> portMentionedTwiceProblems = new HashSet<PortMentionedTwiceProblem>();
	private HashSet<PortMissingFromIterationStrategyStackProblem> portMissingFromIterationStrategyStackProblems = new HashSet<PortMissingFromIterationStrategyStackProblem>();
	private HashSet<WrongParentProblem> wrongParentProblems = new HashSet<WrongParentProblem>();
	private HashSet<IncompatibleGranularDepthProblem> incompatibleGranularDepthProblems = new HashSet<IncompatibleGranularDepthProblem>();

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#emptyIterationStrategyTopNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode)
	 */
	@Override
	public void emptyIterationStrategyTopNode(IterationStrategyTopNode bean) {
		emptyIterationStrategyTopNodeProblems.add(new EmptyIterationStrategyTopNodeProblem(bean));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#mismatchConfigurableType(uk.org.taverna.scufl2.api.configurations.Configuration, uk.org.taverna.scufl2.api.common.Configurable)
	 */
	@Override
	public void mismatchConfigurableType(Configuration bean,
			Configurable configures) {
		mismatchConfigurableTypeProblems.add(new MismatchConfigurableTypeProblem(bean, configures));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#negativeValue(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void negativeValue(WorkflowBean bean, String fieldName,
			Integer fieldValue) {
		negativeValueProblems.add(new NegativeValueProblem(bean, fieldName, fieldValue));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#nonAbsoluteGlobalBaseURI(uk.org.taverna.scufl2.api.common.Root)
	 */
	@Override
	public void nonAbsoluteURI(WorkflowBean bean, String fieldName, URI fieldValue) {
		nonAbsoluteURIProblems.add(new NonAbsoluteURIProblem(bean, fieldName, fieldValue));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#nullField(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.String)
	 */
	@Override
	public void nullField(WorkflowBean bean, String string) {
		nullFieldProblems.add(new NullFieldProblem(bean, string));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#outOfScopeValue(uk.org.taverna.scufl2.api.common.WorkflowBean, java.lang.String, java.lang.Object)
	 */
	@Override
	public void outOfScopeValue(WorkflowBean bean, String fieldName,
			Object value) {
		outOfScopeValueProblems.add(new OutOfScopeValueProblem(bean, fieldName, value));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#portMentionedTwice(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode, uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyNode)
	 */
	@Override
	public void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode) {
		portMentionedTwiceProblems.add(new PortMentionedTwiceProblem(subNode, iterationStrategyNode));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#portMissingFromIterationStrategyStack(uk.org.taverna.scufl2.api.port.Port, uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack)
	 */
	@Override
	public void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean) {
		portMissingFromIterationStrategyStackProblems .add(new PortMissingFromIterationStrategyStackProblem(p, bean));
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#wrongParent(uk.org.taverna.scufl2.api.common.Child)
	 */
	@Override
	public void wrongParent(Child iap) {
		wrongParentProblems.add(new WrongParentProblem(iap));
	}
	
	@Override
	public void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth) {
		incompatibleGranularDepthProblems .add(new IncompatibleGranularDepthProblem(bean, depth, granularDepth));
	}
	
	public HashSet<NegativeValueProblem> getNegativeValueProblems() {
		return negativeValueProblems;
	}

	
	/**
	 * @return the emptyIterationStrategyTopNodes
	 */
	public HashSet<EmptyIterationStrategyTopNodeProblem> getEmptyIterationStrategyTopNodeProblems() {
		return emptyIterationStrategyTopNodeProblems;
	}

	/**
	 * @return the mismatchConfigurableTypeProblems
	 */
	public HashSet<MismatchConfigurableTypeProblem> getMismatchConfigurableTypeProblems() {
		return mismatchConfigurableTypeProblems;
	}

	/**
	 * @return the nonAbsoluteGlobalBaseURIs
	 */
	public HashSet<NonAbsoluteURIProblem> getNonAbsoluteURIProblems() {
		return nonAbsoluteURIProblems;
	}

	/**
	 * @return the nullFieldProblems
	 */
	public HashSet<NullFieldProblem> getNullFieldProblems() {
		return nullFieldProblems;
	}

	/**
	 * @return the outOfScopeValueProblems
	 */
	public HashSet<OutOfScopeValueProblem> getOutOfScopeValueProblems() {
		return outOfScopeValueProblems;
	}

	/**
	 * @return the portMentionedTwiceProblems
	 */
	public HashSet<PortMentionedTwiceProblem> getPortMentionedTwiceProblems() {
		return portMentionedTwiceProblems;
	}

	/**
	 * @return the portMissingFromIterationStrategyStackProblems
	 */
	public HashSet<PortMissingFromIterationStrategyStackProblem> getPortMissingFromIterationStrategyStackProblems() {
		return portMissingFromIterationStrategyStackProblems;
	}

	/**
	 * @return the wrongParents
	 */
	public HashSet<WrongParentProblem> getWrongParentProblems() {
		return wrongParentProblems;
	}

	/**
	 * @return the incompatibleGranularDepthProblems
	 */
	public HashSet<IncompatibleGranularDepthProblem> getIncompatibleGranularDepthProblems() {
		return incompatibleGranularDepthProblems;
	}

	@Override
	public boolean detectedProblems() {
		return (!(Collections.EMPTY_SET.equals(getEmptyIterationStrategyTopNodeProblems()) &&
				Collections.EMPTY_SET.equals(getIncompatibleGranularDepthProblems()) &&
				Collections.EMPTY_SET.equals(getMismatchConfigurableTypeProblems()) &&
				Collections.EMPTY_SET.equals(getNegativeValueProblems()) &&
				Collections.EMPTY_SET.equals(getNonAbsoluteURIProblems()) &&
				Collections.EMPTY_SET.equals(getNullFieldProblems()) &&
				Collections.EMPTY_SET.equals(getOutOfScopeValueProblems()) &&
				Collections.EMPTY_SET.equals(getPortMentionedTwiceProblems()) &&
				Collections.EMPTY_SET.equals(getPortMissingFromIterationStrategyStackProblems()) &&
				Collections.EMPTY_SET.equals(getWrongParentProblems())));
	}

	@Override
	public ValidationException getException() {
		// TODO Needs to be improved;
		if (detectedProblems()) {
			return new ValidationException(this.toString());
		} else {
			return null;
		}
	}




}
