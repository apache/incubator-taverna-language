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


/**
 * A {@link WorkflowBean} that is the child of another <code>WorkflowBean</code>
 * .
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 * @param <T>
 *            the type of <code>WorkflowBean</code> that this is a child of
 */
public interface Child<T extends WorkflowBean> extends WorkflowBean {
	/**
	 * @return the parent of this workflow bean, or <code>null</code> if it is
	 *         orphan
	 */
	T getParent();

	/**
	 * Sets the parent of this workflow bean.
	 * <p>
	 * Setting the parent would normally also add the object to the relevant
	 * collection in the parent if it does not already exist there.
	 * <p>
	 * If the child has an existing, object-identity different parent, the child
	 * will first be removed from the parent collection if it exists there.
	 * <p>
	 * <strong>Note:</strong>If the child is {@link Named} the parent collection
	 * will be a {@link NamedSet}. This implicit insertion would overwrite any
	 * conflicting sibling with the same {@link Named#getName()} - to avoid
	 * this, add the child to the parent collection by using
	 * {@link NamedSet#addWithUniqueName(Named)} before setting the parent.
	 * 
	 * @param parent
	 *            the parent of this workflow bean
	 */
	void setParent(T parent);
}
