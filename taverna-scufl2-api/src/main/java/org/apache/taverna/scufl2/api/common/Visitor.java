package org.apache.taverna.scufl2.api.common;

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


import java.util.List;
import java.util.Stack;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;


/**
 * Visitor which can inspect a {@link WorkflowBean}.
 * <p>
 * Used with {@link WorkflowBean#accept(Visitor)} - in particular with
 * {@link WorkflowBundle#accept(Visitor)} to recursively visit every
 * WorkflowBean of a workflow bundle.
 * <p>
 * {@link #visitEnter(WorkflowBean)} will be called for each
 * {@link WorkflowBean} which has children, while {@link #visit(WorkflowBean)}
 * is called for leaf children.
 * <p>
 * For instance visiting using {@link Workflow#accept(Visitor)} will call
 * {@link #visitEnter(WorkflowBean)} with the workflow instance, followed by a
 * {@link #visit(WorkflowBean)} on each {@link InputWorkflowPort} and
 * {@link OutputWorkflowPort} (which are leaf nodes). Further
 * {@link #visitEnter(WorkflowBean)} will be called with a {@link Processor} -
 * which {@link Processor#accept(Visitor)} will recurse to
 * {@link InputProcessorPort} etc.
 * <p>
 * The visitor can avoid visiting a branch of the workflow by returning
 * <code>false</code> from {@link #visitEnter(WorkflowBean)}, or cancel the
 * iteration over children by returning <code>false</code> from
 * {@link #visit(WorkflowBean)} and {@link #visitLeave(WorkflowBean)}.
 * <p>
 * The {@link VisitorAdapter} class can be useful for avoiding to implement all
 * Visitor methods, while {@link VisitorWithPath} provides the
 * {@link VisitorAdapter#getCurrentPath()} .
 * 
 * @see http://c2.com/cgi/wiki?HierarchicalVisitorPattern
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public interface Visitor {
	public static abstract class VisitorAdapter implements Visitor {
		@Override
		public boolean visit(WorkflowBean node) {
			return true;
		}

		@Override
		public boolean visitEnter(WorkflowBean node) {
			return true;
		}

		@Override
		public boolean visitLeave(WorkflowBean node) {
			return true;
		}
	}

	public static abstract class VisitorWithPath implements Visitor {
		private final Stack<WorkflowBean> currentPath = new Stack<>();
		private WorkflowBean currentNode;

		/**
		 * Returns the {@link WorkflowBean} currently visited.
		 * 
		 * @return the <code>WorkflowBean</code> currently visited
		 */
		public WorkflowBean getCurrentNode() {
			return currentNode;
		}

		/**
		 * Return the current path of {@link WorkflowBean WorkflowBeans}.
		 * <p>
		 * The list will never contain the current node, which can instead be
		 * found in {@link #getCurrentNode()}. That means that
		 * {@link Stack#peek()} will be the intermediate parent of the current
		 * node.
		 * <p>
		 * The first object of the stack will be the initial object where
		 * {@link WorkflowBean#accept(Visitor)} was called, not necessarily the
		 * {@link WorkflowBundle}.
		 * <p>
		 * So for instance if you are in {@link #visit(WorkflowBean)} on an
		 * {@link InputProcessorPort}, {@link #getCurrentPath()} will have a
		 * list <code>[workflowBundle, workflow, processor]</code>. This should
		 * generally match the path from following {@link Child#getParent()}
		 * recursively.
		 * <p>
		 * This method returns the same {@link List} instance throughout the use
		 * of the visitor, it is dynamically modified by
		 * {@link #visitEnter(WorkflowBean)} and
		 * {@link #visitLeave(WorkflowBean)}.
		 * 
		 * @return A {@link List} of the current path of the ancestors of the
		 *         currently visiting {@link WorkflowBean}.
		 */
		public Stack<WorkflowBean> getCurrentPath() {
			return currentPath;
		}

		/**
		 * Similar to {@link Visitor#visit(WorkflowBean)} - but called for every
		 * node, even if it can have children.
		 * <p>
		 * The current node is available in {@link #getCurrentNode()}.
		 * <p>
		 * 
		 * @return <code>true</code> if this node's children should be visited.
		 *         (return value is ignored for nodes which can't have children)
		 */
		public abstract boolean visit();

		/**
		 * Final to maintain current path. Override {@link #visit()} instead.
		 * 
		 * @see #visit()
		 */
		@Override
		public final boolean visit(WorkflowBean node) {
			visitEnter(node);
			return visitLeave(node);
		}

		/**
		 * Final to maintain current path. Override {@link #visit()} instead.
		 * 
		 * @see #visit()
		 */
		@Override
		public final boolean visitEnter(WorkflowBean node) {
			currentNode = node;
			boolean recurse = visit();
			currentPath.add(node);
			return recurse;
		}

		/**
		 * Override this method to be informed when leaving a node.
		 * 
		 * Similar to {@link Visitor#visitLeave(WorkflowBean)}, but also called
		 * for nodes which can't have children.
		 * <p>
		 * The current node is present in {@link #getCurrentPath()} and can be
		 * retrieved using {@link #getCurrentNode()}.
		 * 
		 * @return <code>true</code> if the visits over the current beans'
		 *         siblings should continue, <code>false</code> to immediately
		 *         leave the parent.
		 */
		public boolean visitLeave() {
			return true;
		};

		/**
		 * Final to maintain current path. Override {@link #visitLeave()}
		 * instead.
		 * 
		 * @see #visitLeave()
		 */
		@Override
		public final boolean visitLeave(WorkflowBean node) {
			currentNode = currentPath.pop();
			if (currentNode != node)
				throw new IllegalStateException("Unexpected visit to node "
						+ node + " expected " + currentNode);
			return visitLeave();
		}
	}

	/**
	 * Called by {@link WorkflowBean#accept(Visitor)} when the visited bean is a
	 * leaf node.
	 * <p>
	 * A leaf node is a bean that can't have children, like
	 * {@link InputWorkflowPort}, {@link PortNode} or {@link PropertyLiteral}.
	 * 
	 * @see #visitEnter(WorkflowBean)
	 * @param node
	 *            the currently visited {@link WorkflowBean}
	 * @return <code>true</code> if the visits over the current beans' siblings
	 *         should continue, <code>false</code> to immediately leave the
	 *         parent.
	 */
	boolean visit(WorkflowBean node);

	/**
	 * Called by {@link WorkflowBean#accept(Visitor)} when first visiting a bean
	 * which potentially has children.
	 * <p>
	 * After visiting the children (which could be none),
	 * {@link WorkflowBean#accept(Visitor)} on the current bean will call
	 * {@link #visitLeave(WorkflowBean)}.
	 * <p>
	 * Note that a bean can have some children which are visited with
	 * {@link #visit(WorkflowBean)} and others which are visited with
	 * {@link #visitEnter(WorkflowBean)} (depending on if they themselves can
	 * have children).
	 * 
	 * @see #visit(WorkflowBean)
	 * @see #visitLeave(WorkflowBean)
	 * @param node
	 *            the currently visited {@link WorkflowBean}
	 * @return <code>true</code> if the children of this bean should be visited.
	 */
	boolean visitEnter(WorkflowBean node);

	/**
	 * Called by {@link WorkflowBean#accept(Visitor)} before leaving a bean
	 * previously visited with {@link #visitEnter(WorkflowBean)}. This is called
	 * after all of the bean's children have been recursively visited, even if
	 * any of those aborted the visit by returning <code>false</code>.
	 * 
	 * @param node
	 *            the currently visited {@link WorkflowBean}
	 * @return <code>false</code> if the visits over the current beans' siblings
	 *         should continue, <code>false</code> to immediately leave the
	 *         parent.
	 */
	boolean visitLeave(WorkflowBean node);
}
