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


import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;

import org.apache.taverna.scufl2.api.annotation.Annotation;


/**
 * Abstract implementation of a {@link Named} {@link WorkflowBean}.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public abstract class AbstractNamed extends AbstractCloneable implements Named  {
	private String name;

	/**
	 * Constructs a {@link Named} {@link WorkflowBean} with a random UUID as the name.
	 */
	public AbstractNamed() {
		setName(UUID.randomUUID().toString());
	}

	/**
	 * Constructs a {@link Named} {@link WorkflowBean} with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Named</code> <code>WorkflowBean</code>. <strong>Must not</strong> be <code>null</code>
	 *            or an empty String.
	 */
	public AbstractNamed(String name) {
		setName(name);
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof AbstractNamed))
			// Other comparables go first
			return 1;
		AbstractNamed other = (AbstractNamed) o;
		if (other == this)
			return 0;
		/**
		 * Disabled as this means the order changes depending on setParents being called or not;
		 * could cause a DataLink to appear twice in workflow.getDataLinks(). 
		 * 
		 * 
		if (this instanceof Child) {
			if (!(other instanceof Child)) {
				// He's orphan, he's sorted first
				return 1;
			}
			WorkflowBean parent = ((Child<?>) this).getParent();
			WorkflowBean otherParent = ((Child<?>) other).getParent();
			if (parent instanceof Comparable && otherParent instanceof Comparable) {
				int comparedParents = ((Comparable) parent).compareTo(otherParent);
				if (comparedParents != 0) {
					return comparedParents;
				}
			}
		} else {
			if (other instanceof Child) {
				// We're orphan, we're first
				return -1;
			}
		}
		*/
		if (getClass() != other.getClass()) {
			int classCompare = getClass().getCanonicalName().compareTo(
					other.getClass().getCanonicalName());
			if (classCompare != 0)
				// Allow having say InputPorts and OutputPorts in the same sorted list
				return classCompare;
		}
		// We're the same class, let's compare the names
		return getName().compareTo(other.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractNamed other = (AbstractNamed) obj;
		if (!getName().equals(other.getName()))
			return false;
		if (this instanceof Child) {
			WorkflowBean parent = ((Child<?>) this).getParent();
			WorkflowBean otherParent = ((Child<?>) other).getParent();
			if (parent != null)
				return parent.equals(otherParent);
			if (parent == null && otherParent != null)
				return false;
		}
		if (this instanceof Typed) {
			URI myId = ((Typed) this).getType();
			URI otherId = ((Typed) obj).getType();
			if (myId != null)
				return myId.equals(otherId);
			if (myId == null && otherId != null)
				return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());

		if (this instanceof Child) {
			WorkflowBean parent = ((Child) this).getParent();
			if (parent != null)
				result = prime * result + parent.hashCode();
		}
		return result;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("Name can't be null");
		Matcher invalidMatcher = INVALID_NAME.matcher(name);
		if (invalidMatcher.find())
		    // http://dev.mygrid.org.uk/issues/browse/SCUFL2-87
		    // TODO: Any other characters that must be disallowed?
			throw new IllegalArgumentException("Name invalid in position "
					+ invalidMatcher.start() + ": '" + name + "'");

		if (this instanceof Child) {
			Child child = (Child) this;
			WorkflowBean parent = child.getParent();
			if (parent != null) {
				child.setParent(null);
				this.name = name;
				// Might overwrite other Named object with same name
				child.setParent(parent);
			}
		}
		this.name = name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " \"" + getName() + '"';
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		AbstractNamed namedClone = (AbstractNamed)clone;
		namedClone.setName(getName());
	}

	// Derived operations

	/**
	 * Get all the annotations that pertain to this workflow element.
	 * 
	 * @return The collection of annotations.
	 * @see Scufl2Tools#annotationsFor(Child)
	 */
	public Collection<Annotation> getAnnotations() {
		if (this instanceof Child)
			return getTools().annotationsFor((Child<?>) this);
		throw new UnsupportedOperationException(
				"operation needs to be overridden for root elements");
	}

	/**
	 * Get the URI of this workflow element.
	 * 
	 * @return The absolute URI.
	 * @see URITools#uriForBean(WorkflowBean)
	 */
	public URI getURI() {
		return getUriTools().uriForBean(this);
	}

	/**
	 * Get the URI of this workflow element relative to another workflow
	 * element.
	 * 
	 * @return The relative URI.
	 * @see URITools#relativeUriForBean(WorkflowBean,WorflowBean)
	 */
	public URI getRelativeURI(WorkflowBean relativeTo) {
		return getUriTools().relativeUriForBean(this, relativeTo);
	}
}
