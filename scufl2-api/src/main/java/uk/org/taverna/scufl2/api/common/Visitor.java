package uk.org.taverna.scufl2.api.common;

import java.util.List;
import java.util.Stack;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;

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
 * Visitor methods, and also provides the
 * {@link VisitorAdapter#getCurrentPath()} if subclasses remembers to call
 * <code>super.visitEnter()</code> and <code>super.visitLeave</code>.
 *
 * @see http://c2.com/cgi/wiki?HierarchicalVisitorPattern
 *
 * @author Stian Soiland-Reyes
 *
 */
public interface Visitor {
	public static abstract class VisitorAdapter implements Visitor {

		private final Stack<WorkflowBean> currentPath = new Stack<WorkflowBean>();

		/**
		 * Return the current path of {@link WorkflowBean}s.
		 * <p>
		 * This method only works correctly if subclasses overriding
		 * {@link #visitEnter(WorkflowBean)} call super.visitEnter() and vice
		 * versa for {@link #visitLeave(WorkflowBean)}.
		 * <p>
		 * The list will not contain the current node for calls to
		 * {@link #visit(WorkflowBean)} - it will contain the current node or
		 * not for {@link #visitEnter(WorkflowBean)}/
		 * {@link #visitLeave(WorkflowBean)} depends on when the super call is
		 * made.
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

		@Override
		public boolean visit(WorkflowBean node) {
			return true;
		}

		@Override
		public boolean visitEnter(WorkflowBean node) {
			currentPath.add(node);
			return true;
		}

		@Override
		public boolean visitLeave(WorkflowBean node) {
			currentPath.pop();
			return true;
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
	public boolean visit(WorkflowBean node);

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