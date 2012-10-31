package uk.org.taverna.scufl2.api.common;

import java.util.HashMap;

public abstract class AbstractCloneable implements WorkflowBean {
	
	protected static class CopyVisitor implements Visitor {
		private Cloning cloning;

		public CopyVisitor(Cloning cloning) {
			this.cloning = cloning;
		}

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

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void cloneNode(WorkflowBean node) {
			WorkflowBean clone = cloning.cloneIfNotInCache((WorkflowBean)node);
			if (node instanceof Child && clone instanceof Child) {
				Child child = (Child) node;
				Child childClone = (Child)clone;
				WorkflowBean oldParent = child.getParent();
				// NOTE: oldParent==null is OK, as cloned is HashMap

				// NOTE: We don't clone the parent! If it's not already cloned,
				// then it might be above our current visit tree and should not be
				// cloned (this is the case for the top level node). The clone will then have the parent as null.
				WorkflowBean newParent = cloning.getCloned(oldParent);
				childClone.setParent(newParent);
			}
		}
	}
	
	@Override
	public AbstractCloneable clone() {
		return cloneWorkflowBean(this);		
	}

	public static <T extends WorkflowBean> T cloneWorkflowBean(T obj) {
		Cloning cloning = new Cloning(obj);
		CopyVisitor copyVisitor = new CopyVisitor(cloning);
		obj.accept(copyVisitor);
		return cloning.getCloned(obj);
	}
	
	public static class Cloning {
		private HashMap<WorkflowBean, WorkflowBean> cloned = new HashMap<WorkflowBean, WorkflowBean>();

		
		/**
		 * Construct a Cloning helper.
		 * <p>
		 * 
		 * @param ancestor
		 *            The highest WorkflowBean in the hierarchy to clone. Any
		 *            beans that are 'below' this ancestor will be cloned.
		 */
		public Cloning(WorkflowBean ancestor) {
			this.ancestor = ancestor;
		}
		
		final WorkflowBean ancestor;
	
		@SuppressWarnings("unchecked")
		public <T extends WorkflowBean> T cloneOrOriginal(T original) {
			if (cloned.containsKey(original)) {
				return (T) cloned.get(original);
			}
			return original;
		}

		@SuppressWarnings("unchecked")
		public <T extends WorkflowBean> T cloneIfNotInCache(T original) {
			if (original == null) {
				return null;
			}
			if (cloned.containsKey(original)) {
				return (T) cloned.get(original);
			}		
			T clone;
			try {
				clone = (T) original.getClass().newInstance();
			} catch (InstantiationException e) {
//				System.err.println("Can't do this one.. " + original);
				return null;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			cloned.put(original, clone);
			
			if (original instanceof AbstractCloneable) {
				AbstractCloneable cloneable = (AbstractCloneable) original;
				cloneable.cloneInto(clone, this);
			}
			
//			System.out.println("Cloned " + clone);
			return clone;	
		}
		

		@SuppressWarnings("unchecked")
		public <T extends WorkflowBean> T getCloned(T originalBean) {
			return (T) cloned.get(originalBean);
		}
		
		public <T extends WorkflowBean> void knownClone(T original, T clone) {
			cloned.put(original, clone);
		}
		
		
	}

	protected abstract void cloneInto(WorkflowBean clone,
			Cloning cloning);
	
	

}
