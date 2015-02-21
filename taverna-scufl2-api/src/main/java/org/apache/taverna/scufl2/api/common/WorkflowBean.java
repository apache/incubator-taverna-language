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
 * The top level interface for all objects in a workflow.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public interface WorkflowBean extends Cloneable {
	/**
	 * Accepts a {@link Visitor} to this <code>WorkflowBean</code>.
	 * 
	 * @param visitor
	 *            the <code>Visitor</code> to accept
	 * @return <code>true</code> if this <code>WorkflowBeans</code> children
	 *         should be visited.
	 */
	boolean accept(Visitor visitor);

	/**
	 * Create a deep clone of this bean.
	 * <p>
	 * The cloned bean will have equivalent properties as the original bean. Any
	 * {@link Child} beans which parent match this bean will be cloned as well
	 * (recursively), non-child beans will remain the same. If this bean is a
	 * {@link Child}, the returned clone will not have a parent set.
	 * <p>
	 * Note that children whose {@link Child#getParent()} is <code>null</code>
	 * might not be cloned, to avoid this, use
	 * {@link Scufl2Tools#setParents(org.apache.taverna.scufl2.api.container.WorkflowBundle)}
	 * before cloning.
	 * 
	 * @return A cloned workflow bean
	 */
	// @Override
	WorkflowBean clone();
}
