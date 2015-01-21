package org.apache.taverna.scufl2.api.iterationstrategy;

/*
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
 */


import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.scufl2.api.common.AbstractCloneable;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;


/**
 * @author Stian Soiland-Reyes
 */
@SuppressWarnings("serial")
public class DotProduct extends ArrayList<IterationStrategyNode> implements
		IterationStrategyTopNode {
	private IterationStrategyParent parent;

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this))
			for (IterationStrategyNode strategy : this)
				if (!strategy.accept(visitor))
					break;
		return visitor.visitLeave(this);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof DotProduct && super.equals(o);
	}

	@Override
	public IterationStrategyParent getParent() {
		return parent;
	}

	@Override
	public void setParent(IterationStrategyParent newParent) {
		if (parent == newParent)
			return;

		if (parent != null) {
			// Remove from old parent
			if (parent instanceof IterationStrategyStack) {
				IterationStrategyStack stack = (IterationStrategyStack) parent;
				stack.remove(this);
			} else if (parent instanceof DotProduct
					|| parent instanceof CrossProduct) {
				@SuppressWarnings("unchecked")
				List<IterationStrategyNode> parentList = (List<IterationStrategyNode>) parent;
				parentList.remove(this);
			} else
				throw new IllegalArgumentException(
						"Old parent must be a IterationStrategy, DotProduct or CrossProduct: "
								+ parent);
		}

		parent = newParent;
		if (parent instanceof IterationStrategyStack) {
			IterationStrategyStack stack = (IterationStrategyStack) parent;
			if (!stack.contains(this))
				stack.add(this);
		} else if (parent instanceof DotProduct
				|| parent instanceof CrossProduct) {
			@SuppressWarnings("unchecked")
			List<IterationStrategyNode> parentList = (List<IterationStrategyNode>) parent;
			if (!parentList.contains(this))
				parentList.add(this);
		} else
			throw new IllegalArgumentException(
					"Parent must be a IterationStrategy, DotProduct or CrossProduct: "
							+ parent);
	}

	@Override
	public WorkflowBean clone() {
		return AbstractCloneable.cloneWorkflowBean(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + super.toString();
	}
}
