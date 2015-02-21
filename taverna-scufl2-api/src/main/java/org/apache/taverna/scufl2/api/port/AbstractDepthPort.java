package org.apache.taverna.scufl2.api.port;

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


import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;

/**
 * Abstract implementation of a <code>Port</code> that has a depth property.
 * <p>
 * The depth of a <code>Port </code> specifies whether the data is a list and
 * how deep lists are nested. A depth of 0 is a single element, depth 1 is a
 * list, depth 2 is a list of lists and so on.
 * 
 * @author Alan R Williams
 */
public abstract class AbstractDepthPort extends AbstractNamed implements
		DepthPort {
	private Integer depth;

	/**
	 * Constructs an <code>AbstractDepthPort</code> with a random UUID as the
	 * name.
	 */
	public AbstractDepthPort() {
		super();
	}

	/**
	 * Constructs an <code>AbstractDepthPort</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong>
	 *            be <code>null</code> or an empty String.
	 */
	public AbstractDepthPort(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the depth of the <code>Port</code>.
	 * 
	 * @return the depth of the <code>Port</code>
	 */
	@Override
	public Integer getDepth() {
		return depth;
	}

	/**
	 * Sets the depth of the <code>Port</code>.
	 * 
	 * @param depth
	 *            the depth of the <code>Port</code>
	 */
	@Override
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		AbstractDepthPort clonePort = (AbstractDepthPort) clone;
		clonePort.setDepth(getDepth());
	}
}
