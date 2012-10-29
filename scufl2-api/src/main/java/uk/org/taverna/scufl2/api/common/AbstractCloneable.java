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
			WorkflowBean clone = cloneIfNotInCache((WorkflowBean)node);
			if (node instanceof Child) {
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

		protected WorkflowBean cloneIfNotInCache(WorkflowBean original) {
			if (cloned.containsKey(original)) {
				return cloned.get(original);
			}		
			WorkflowBean clone;
			try {
				clone = original.getClass().newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			cloned.put(original, clone);
			clonePropertiesInto(original, clone, cloned);
			return clone;	
		}
		
		protected void clonePropertiesInto(WorkflowBean original,
				WorkflowBean clone, HashMap<WorkflowBean, WorkflowBean> cloned) {
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(original.getClass(), AbstractCloneable.class);
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
			for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
				copyProperty(p, original, clone);
			}
		}

		private void copyProperty(PropertyDescriptor p,
				WorkflowBean original, WorkflowBean clone) {
			Object oldValue;
			try {
				oldValue = p.getReadMethod().invoke(original);
			} catch (Exception e) {				
				// TODO: Should we really silently ignore this?			
				//return;
				throw new RuntimeException("Could not invoke read method for " + p.getName() 
						+ " on " + original, e);
			}
			Object newValue = oldValue;
			if (oldValue instanceof WorkflowBean) {
				newValue = cloneIfNotInCache((WorkflowBean)oldValue);
			} else if (oldValue instanceof Cloneable) {				
				try {
					Method cloneMethod = oldValue.getClass().getMethod("clone");
					newValue = cloneMethod.invoke(oldValue);
				} catch (Exception e) {
					// ignore
				}
			}
			
			try {
				p.getWriteMethod().invoke(clone, newValue);
			} catch (Exception e) {
				throw new RuntimeException("Could not invoke write method for " + p.getName() 
						+ " on " + original, e);
			}
		}

		@SuppressWarnings("unchecked")
		public <T extends WorkflowBean> T getCloned(T originalBean) {
			return (T) cloned.get(originalBean);
		}		
	}
	
	@Override
	public AbstractCloneable cloned() {
		CopyVisitor copyVisitor = new CopyVisitor();
		accept(copyVisitor);
		return (AbstractCloneable) copyVisitor.getCloned(this);
	}


}
