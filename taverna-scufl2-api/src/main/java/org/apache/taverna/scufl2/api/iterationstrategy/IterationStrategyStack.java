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
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.core.Processor;


@SuppressWarnings("serial")
public class IterationStrategyStack extends ArrayList<IterationStrategyTopNode>
		implements List<IterationStrategyTopNode>, Child<Processor>,
		IterationStrategyParent {
	private Processor parent;

	public IterationStrategyStack() {
	}

	public IterationStrategyStack(Processor parent) {
		setParent(parent);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IterationStrategyStack && super.equals(o);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this))
			for (IterationStrategyTopNode strategy : this)
				if (!strategy.accept(visitor))
					break;
		return visitor.visitLeave(this);
	}

	@Override
	public Processor getParent() {
		return parent;
	}

	@Override
	public void setParent(Processor parent) {
		if (this.parent == parent)
			return;
		if (this.parent != null
				&& this.parent.getIterationStrategyStack() == this)
			this.parent.setIterationStrategyStack(null);
		this.parent = parent;
		if (parent != null && parent.getIterationStrategyStack() != this)
			parent.setIterationStrategyStack(this);
	}

	@Override
	public WorkflowBean clone() {
		return AbstractCloneable.cloneWorkflowBean(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + getParent();
	}
}
