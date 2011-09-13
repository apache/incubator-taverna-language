/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.net.URI;
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
import uk.org.taverna.scufl2.validation.ValidationProblem;

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
	
	public static class MismatchConfigurableTypeProblem extends ValidationProblem {
		
		private final Configurable configurable;

		public MismatchConfigurableTypeProblem(Configuration configuration, Configurable configurable) {
			super(configuration);
			this.configurable = configurable;	
		}

		/**
		 * @return the configurable
		 */
		public Configurable getConfigurable() {
			return configurable;
		}
		
		public String toString() {
			return ("The types of " + getBean() + " and " + configurable + " are mismatched");
		}
		
	}
	
	public static class NegativeValueProblem extends ValidationProblem {
		private final String fieldName;
		private final Integer fieldValue;

		public NegativeValueProblem(WorkflowBean bean, String fieldName,
				Integer fieldValue) {
			super(bean);
					this.fieldName = fieldName;
					this.fieldValue = fieldValue;
			
		}

		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * @return the fieldValue
		 */
		public Integer getFieldValue() {
			return fieldValue;
		}
		
		public String toString() {
			return (getBean() + " has " + fieldName + " of value " + fieldValue);
		}
	}
	
	public HashSet<NegativeValueProblem> getNegativeValueProblems() {
		return negativeValueProblems;
	}

	
	public static class NullFieldProblem extends ValidationProblem {
		private final String fieldName;

		public NullFieldProblem(WorkflowBean bean, String fieldName) {
			super(bean);
			this.fieldName = fieldName;	
		}

		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}
		
		public String toString() {
			return (getBean() + " has a null " + fieldName);
		}
	}
	
	/**
	 * @author alanrw
	 *
	 */
	public static class OutOfScopeValueProblem extends ValidationProblem {

		private final String fieldName;
		private final Object value;

		public OutOfScopeValueProblem(WorkflowBean bean, String fieldName,
				Object value) {
			super(bean);
					this.fieldName = fieldName;
					this.value = value;
		}

		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
		
		public String toString() {
			return (getBean() + " has " + fieldName + " with out of scope value " + value);
		}

	}

	/**
	 * @author alanrw
	 *
	 */
	public static class PortMentionedTwiceProblem extends ValidationProblem {

		private final IterationStrategyNode duplicateNode;

		public PortMentionedTwiceProblem(IterationStrategyNode originalNode,
				IterationStrategyNode duplicateNode) {
			super(originalNode);
					this.duplicateNode = duplicateNode;
		}

		/**
		 * @return the iterationStrategyNode
		 */
		public IterationStrategyNode getDuplicateNode() {
			return duplicateNode;
		}
		
		public String toString() {
			return (getBean() + " and " + duplicateNode + " reference the same port");
		}

	}

	/**
	 * @author alanrw
	 *
	 */
	public static class PortMissingFromIterationStrategyStackProblem extends ValidationProblem {

		private final Port port;

		public PortMissingFromIterationStrategyStackProblem(Port port,
				IterationStrategyStack iterationStrategyStack) {
			super(iterationStrategyStack);
					this.port = port;
		}

		/**
		 * @return the port
		 */
		public Port getPort() {
			return port;
		}

		
		public String toString() {
			return (getBean() + " does not include " + port);
		}

	}

	/**
	 * @author alanrw
	 *
	 */
	public class IncompatibleGranularDepthProblem extends ValidationProblem {

		private final Integer depth;
		private final Integer granularDepth;

		public IncompatibleGranularDepthProblem(AbstractGranularDepthPort bean,
				Integer depth, Integer granularDepth) {
			super(bean);
					this.depth = depth;
					this.granularDepth = granularDepth;
		}

		/**
		 * @return the depth
		 */
		public Integer getDepth() {
			return depth;
		}

		/**
		 * @return the granularDepth
		 */
		public Integer getGranularDepth() {
			return granularDepth;
		}
		
		public String toString() {
			return (getBean() + " has depth " + depth + " and granular depth " + granularDepth);
		}

	}

	/**
	 * @author alanrw
	 *
	 */
	public class EmptyIterationStrategyTopNodeProblem extends ValidationProblem {
		
		public EmptyIterationStrategyTopNodeProblem(IterationStrategyTopNode bean) {
			super(bean);
		}

		public String toString() {
			return (getBean() + " is empty");
		}

	}
	
	public class NonAbsoluteURIProblem extends ValidationProblem {
		
		private String fieldName;
		private URI fieldValue;
		
		public NonAbsoluteURIProblem(WorkflowBean bean, String fieldName, URI fieldValue) {
			super(bean);
			this.fieldName = fieldName;
			this.fieldValue = fieldValue;
			
		}

		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * @return the fieldValue
		 */
		public URI getFieldValue() {
			return fieldValue;
		}
		
		public String toString() {
			return(getBean() + "has a non-absolute URI in field " + fieldName + " of value " + fieldValue.toString());
		}
	}
	
	public class WrongParentProblem extends ValidationProblem {
		
		public WrongParentProblem(WorkflowBean bean) {
			super(bean);
		}
		
		public String toString() {
			return(getBean() + " does not have the correct parent");
		}

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




}
