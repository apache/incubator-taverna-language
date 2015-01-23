/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.port.AbstractGranularDepthPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.validation.ValidationException;
import org.apache.taverna.scufl2.validation.correctness.report.EmptyIterationStrategyTopNodeProblem;
import org.apache.taverna.scufl2.validation.correctness.report.IncompatibleGranularDepthProblem;
import org.apache.taverna.scufl2.validation.correctness.report.MismatchConfigurableTypeProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NegativeValueProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NonAbsoluteURIProblem;
import org.apache.taverna.scufl2.validation.correctness.report.NullFieldProblem;
import org.apache.taverna.scufl2.validation.correctness.report.OutOfScopeValueProblem;
import org.apache.taverna.scufl2.validation.correctness.report.PortMentionedTwiceProblem;
import org.apache.taverna.scufl2.validation.correctness.report.PortMissingFromIterationStrategyStackProblem;
import org.apache.taverna.scufl2.validation.correctness.report.WrongParentProblem;


/**
 * @author alanrw
 */
public class ReportCorrectnessValidationListener implements
		CorrectnessValidationListener {
	private HashSet<EmptyIterationStrategyTopNodeProblem> emptyIterationStrategyTopNodeProblems = new HashSet<>();
	private HashSet<MismatchConfigurableTypeProblem> mismatchConfigurableTypeProblems = new HashSet<>();
	private HashSet<NegativeValueProblem> negativeValueProblems = new HashSet<>();
	private HashSet<NonAbsoluteURIProblem> nonAbsoluteURIProblems = new HashSet<>();
	private HashSet<NullFieldProblem> nullFieldProblems = new HashSet<>();
	private HashSet<OutOfScopeValueProblem> outOfScopeValueProblems = new HashSet<>();
	private HashSet<PortMentionedTwiceProblem> portMentionedTwiceProblems = new HashSet<>();
	private HashSet<PortMissingFromIterationStrategyStackProblem> portMissingFromIterationStrategyStackProblems = new HashSet<>();
	private HashSet<WrongParentProblem> wrongParentProblems = new HashSet<>();
	private HashSet<IncompatibleGranularDepthProblem> incompatibleGranularDepthProblems = new HashSet<>();

	@Override
	public void emptyIterationStrategyTopNode(IterationStrategyTopNode bean) {
		emptyIterationStrategyTopNodeProblems
				.add(new EmptyIterationStrategyTopNodeProblem(bean));
	}

	@Override
	public void mismatchConfigurableType(Configuration bean,
			Configurable configures) {
		mismatchConfigurableTypeProblems
				.add(new MismatchConfigurableTypeProblem(bean, configures));
	}

	@Override
	public void negativeValue(WorkflowBean bean, String fieldName,
			Integer fieldValue) {
		negativeValueProblems.add(new NegativeValueProblem(bean, fieldName,
				fieldValue));
	}

	@Override
	public void nonAbsoluteURI(WorkflowBean bean, String fieldName,
			URI fieldValue) {
		nonAbsoluteURIProblems.add(new NonAbsoluteURIProblem(bean, fieldName,
				fieldValue));
	}

	@Override
	public void nullField(WorkflowBean bean, String string) {
		nullFieldProblems.add(new NullFieldProblem(bean, string));
	}

	@Override
	public void outOfScopeValue(WorkflowBean bean, String fieldName,
			Object value) {
		outOfScopeValueProblems.add(new OutOfScopeValueProblem(bean, fieldName,
				value));
	}

	@Override
	public void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode) {
		portMentionedTwiceProblems.add(new PortMentionedTwiceProblem(subNode,
				iterationStrategyNode));
	}

	@Override
	public void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean) {
		portMissingFromIterationStrategyStackProblems
				.add(new PortMissingFromIterationStrategyStackProblem(p, bean));
	}

	@Override
	public void wrongParent(Child<?> iap) {
		wrongParentProblems.add(new WrongParentProblem(iap));
	}

	@Override
	public void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth) {
		incompatibleGranularDepthProblems
				.add(new IncompatibleGranularDepthProblem(bean, depth,
						granularDepth));
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
		return !emptyIterationStrategyTopNodeProblems.isEmpty()
				|| !incompatibleGranularDepthProblems.isEmpty()
				|| !mismatchConfigurableTypeProblems.isEmpty()
				|| !negativeValueProblems.isEmpty()
				|| !nonAbsoluteURIProblems.isEmpty()
				|| !nullFieldProblems.isEmpty()
				|| !outOfScopeValueProblems.isEmpty()
				|| !portMentionedTwiceProblems.isEmpty()
				|| !portMissingFromIterationStrategyStackProblems.isEmpty()
				|| !wrongParentProblems.isEmpty();
	}

	@Override
	public ValidationException getException() {
		// TODO Needs to be improved;
		if (!detectedProblems())
			return null;
		return new ValidationException(this.toString());
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("ReportCorrectnessValidationListener [getNegativeValueProblems()=");
		builder.append(getNegativeValueProblems() != null ? toString(
				getNegativeValueProblems(), maxLen) : null);
		builder.append(", getEmptyIterationStrategyTopNodeProblems()=");
		builder.append(getEmptyIterationStrategyTopNodeProblems() != null ? toString(
				getEmptyIterationStrategyTopNodeProblems(), maxLen) : null);
		builder.append(", getMismatchConfigurableTypeProblems()=");
		builder.append(getMismatchConfigurableTypeProblems() != null ? toString(
				getMismatchConfigurableTypeProblems(), maxLen) : null);
		builder.append(", getNonAbsoluteURIProblems()=");
		builder.append(getNonAbsoluteURIProblems() != null ? toString(
				getNonAbsoluteURIProblems(), maxLen) : null);
		builder.append(", getNullFieldProblems()=");
		builder.append(getNullFieldProblems() != null ? toString(
				getNullFieldProblems(), maxLen) : null);
		builder.append(", getOutOfScopeValueProblems()=");
		builder.append(getOutOfScopeValueProblems() != null ? toString(
				getOutOfScopeValueProblems(), maxLen) : null);
		builder.append(", getPortMentionedTwiceProblems()=");
		builder.append(getPortMentionedTwiceProblems() != null ? toString(
				getPortMentionedTwiceProblems(), maxLen) : null);
		builder.append(", getPortMissingFromIterationStrategyStackProblems()=");
		builder.append(getPortMissingFromIterationStrategyStackProblems() != null ? toString(
				getPortMissingFromIterationStrategyStackProblems(), maxLen)
				: null);
		builder.append(", getWrongParentProblems()=");
		builder.append(getWrongParentProblems() != null ? toString(
				getWrongParentProblems(), maxLen) : null);
		builder.append(", getIncompatibleGranularDepthProblems()=");
		builder.append(getIncompatibleGranularDepthProblems() != null ? toString(
				getIncompatibleGranularDepthProblems(), maxLen) : null);
		builder.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}
