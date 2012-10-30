package uk.org.taverna.scufl2.api.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class AbstractCloneable implements WorkflowBean {
	
	public static class CopyVisitor implements Visitor {

		private HashMap<WorkflowBean, WorkflowBean> cloned = new HashMap<WorkflowBean, WorkflowBean>();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public boolean visit(WorkflowBean node) {
			cloneNode(node);
			return true;
		}

		@Override
		public boolean visitEnter(WorkflowBean node) {
			cloneNode(node);
			return true;
		}

		@Override
		public boolean visitLeave(WorkflowBean node) {
			return true;
		}

		public void cloneNode(WorkflowBean node) {
			WorkflowBean clone = cloneIfNotInCache((WorkflowBean)node, cloned);
			if (node instanceof Child && clone instanceof Child) {
				Child child = (Child) node;
				Child childClone = (Child)clone;
				WorkflowBean oldParent = child.getParent();
				// NOTE: oldParent==null is OK, as cloned is HashMap

				// NOTE: We don't clone the parent! If it's not already cloned,
				// then it might be above our current visit tree and should not be
				// cloned (this is the case for the top level node). The clone will then have the parent as null.
				WorkflowBean newParent = cloned.get(oldParent);
				childClone.setParent(newParent);
			}
		}

		@SuppressWarnings("unchecked")
		public <T extends WorkflowBean> T getCloned(T originalBean) {
			return (T) cloned.get(originalBean);
		}
		
		public <T extends WorkflowBean> void knownClone(T original, T clone) {
			cloned.put(original, clone);
		}
		
	}
	
	@Override
	public AbstractCloneable cloned() {
		CopyVisitor copyVisitor = new CopyVisitor();
		accept(copyVisitor);
		return (AbstractCloneable) copyVisitor.getCloned(this);
	}
	

	protected static WorkflowBean cloneIfNotInCache(WorkflowBean original, HashMap<WorkflowBean, WorkflowBean> cloned) {
		if (cloned.containsKey(original)) {
			return cloned.get(original);
		}		
		WorkflowBean clone;
		try {
			clone = original.getClass().newInstance();
		} catch (InstantiationException e) {
			System.err.println("Can't do this one.. " + original);
			return null;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		cloned.put(original, clone);
		
		if (original instanceof AbstractCloneable) {
			AbstractCloneable cloneable = (AbstractCloneable) original;
			cloneable.cloneInto(clone, cloned);
		}
		
		System.out.println("Cloned " + clone);
		return clone;	
	}


	protected abstract void cloneInto(WorkflowBean clone,
			HashMap<WorkflowBean, WorkflowBean> cloned);


}
