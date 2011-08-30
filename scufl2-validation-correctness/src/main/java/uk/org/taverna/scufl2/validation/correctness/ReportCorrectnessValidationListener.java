/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

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

/**
 * @author alanrw
 *
 */
public class ReportCorrectnessValidationListener implements
		CorrectnessValidationListener {
	

	HashSet<IterationStrategyTopNode> emptyIterationStrategyTopNodes = new HashSet<IterationStrategyTopNode> ();
	HashSet<MismatchConfigurableTypeProblem> mismatchConfigurableTypeProblems = new HashSet<MismatchConfigurableTypeProblem>();
	HashSet<NegativeValueProblem> negativeValueProblems = new HashSet<NegativeValueProblem>();

	HashSet<Root> nonAbsoluteGlobalBaseURIs = new HashSet<Root>();
	HashSet<NullFieldProblem> nullFieldProblems = new HashSet<NullFieldProblem>();
	HashSet<OutOfScopeValueProblem> outOfScopeValueProblems = new HashSet<OutOfScopeValueProblem>();
	HashSet<PortMentionedTwiceProblem> portMentionedTwiceProblems = new HashSet<PortMentionedTwiceProblem>();
	private HashSet<PortMissingFromIterationStrategyStackProblem> portMissingFromIterationStrategyStackProblems = new HashSet<PortMissingFromIterationStrategyStackProblem>();
	private HashSet<Child> wrongParents = new HashSet<Child>();
	private HashSet<IncompatibleGranularDepthProblem> incompatibleGranularDepthProblems = new HashSet<IncompatibleGranularDepthProblem>();

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.validation.correctness.CorrectnessValidationListener#emptyIterationStrategyTopNode(uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode)
	 */
	@Override
	public void emptyIterationStrategyTopNode(IterationStrategyTopNode bean) {
		emptyIterationStrategyTopNodes.add(bean);
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
	public void nonAbsoluteGlobalBaseURI(Root bean) {
		nonAbsoluteGlobalBaseURIs.add(bean);
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
		wrongParents.add(iap);
	}
	
	@Override
	public void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth) {
		incompatibleGranularDepthProblems .add(new IncompatibleGranularDepthProblem(bean, depth, granularDepth));
	}
	
	public static class MismatchConfigurableTypeProblem {
		
		private final Configuration configuration;
		private final Configurable configurable;

		public MismatchConfigurableTypeProblem(Configuration configuration, Configurable configurable) {
			this.configuration = configuration;
			this.configurable = configurable;	
		}

		/**
		 * @return the configuration
		 */
		public Configuration getConfiguration() {
			return configuration;
		}

		/**
		 * @return the configurable
		 */
		public Configurable getConfigurable() {
			return configurable;
		}
		
	}
	
	public static class NegativeValueProblem {
		private final WorkflowBean bean;
		private final String fieldName;
		private final Integer fieldValue;

		public NegativeValueProblem(WorkflowBean bean, String fieldName,
				Integer fieldValue) {
					this.bean = bean;
					this.fieldName = fieldName;
					this.fieldValue = fieldValue;
			
		}

		/**
		 * @return the bean
		 */
		public WorkflowBean getBean() {
			return bean;
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
	}
	
	public HashSet<NegativeValueProblem> getNegativeValueProblems() {
		return negativeValueProblems;
	}

	
	public static class NullFieldProblem {
		private final WorkflowBean bean;
		private final String fieldName;

		public NullFieldProblem(WorkflowBean bean, String fieldName) {
			this.bean = bean;
			this.fieldName = fieldName;	
		}

		/**
		 * @return the bean
		 */
		public WorkflowBean getBean() {
			return bean;
		}

		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}
	}
	
	/**
	 * @author alanrw
	 *
	 */
	public static class OutOfScopeValueProblem {

		private final WorkflowBean bean;
		private final String fieldName;
		private final Object value;

		public OutOfScopeValueProblem(WorkflowBean bean, String fieldName,
				Object value) {
					this.bean = bean;
					this.fieldName = fieldName;
					this.value = value;
		}

		/**
		 * @return the bean
		 */
		public WorkflowBean getBean() {
			return bean;
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

	}

	/**
	 * @author alanrw
	 *
	 */
	public static class PortMentionedTwiceProblem {

		private final IterationStrategyNode originalNode;
		private final IterationStrategyNode duplicateNode;

		public PortMentionedTwiceProblem(IterationStrategyNode originalNode,
				IterationStrategyNode duplicateNode) {
					this.originalNode = originalNode;
					this.duplicateNode = duplicateNode;
		}

		/**
		 * @return the subNode
		 */
		public IterationStrategyNode getOriginalNode() {
			return originalNode;
		}

		/**
		 * @return the iterationStrategyNode
		 */
		public IterationStrategyNode getDuplicateNode() {
			return duplicateNode;
		}

	}

	/**
	 * @author alanrw
	 *
	 */
	public static class PortMissingFromIterationStrategyStackProblem {

		private final Port port;
		private final IterationStrategyStack iterationStrategyStack;

		public PortMissingFromIterationStrategyStackProblem(Port port,
				IterationStrategyStack iterationStrategyStack) {
					this.port = port;
					this.iterationStrategyStack = iterationStrategyStack;
		}

		/**
		 * @return the port
		 */
		public Port getPort() {
			return port;
		}

		/**
		 * @return the iterationStrategyStack
		 */
		public IterationStrategyStack getIterationStrategyStack() {
			return iterationStrategyStack;
		}

	}

	/**
	 * @author alanrw
	 *
	 */
	public class IncompatibleGranularDepthProblem {

		private final AbstractGranularDepthPort bean;
		private final Integer depth;
		private final Integer granularDepth;

		public IncompatibleGranularDepthProblem(AbstractGranularDepthPort bean,
				Integer depth, Integer granularDepth) {
					this.bean = bean;
					this.depth = depth;
					this.granularDepth = granularDepth;
		}

		/**
		 * @return the bean
		 */
		public AbstractGranularDepthPort getBean() {
			return bean;
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

	}


	/**
	 * @return the emptyIterationStrategyTopNodes
	 */
	public HashSet<IterationStrategyTopNode> getEmptyIterationStrategyTopNodes() {
		return emptyIterationStrategyTopNodes;
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
	public HashSet<Root> getNonAbsoluteGlobalBaseURIs() {
		return nonAbsoluteGlobalBaseURIs;
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
	public HashSet<Child> getWrongParents() {
		return wrongParents;
	}

	/**
	 * @return the incompatibleGranularDepthProblems
	 */
	public HashSet<IncompatibleGranularDepthProblem> getIncompatibleGranularDepthProblems() {
		return incompatibleGranularDepthProblems;
	}




}
