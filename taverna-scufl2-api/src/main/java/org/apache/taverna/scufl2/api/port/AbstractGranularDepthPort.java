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


import org.apache.taverna.scufl2.api.common.WorkflowBean;

/**
 * Abstract implementation of a <code>Port</code> that has a granular depth
 * property.
 * <p>
 * The granular depth of a <code>Port </code> specifies the granularity of the
 * depth at which data is emitted. The granular depth must be less than or equal
 * to the depth.
 * <p>
 * For example, if a <code>Port</code> has a depth of 1 and a granular depth of
 * 0 the <code>Port</code> will emit each element of the list separately.
 * 
 * @author Alan R Williams
 * 
 */
public abstract class AbstractGranularDepthPort extends AbstractDepthPort
		implements GranularDepthPort {
	private Integer granularDepth;

	/**
	 * Constructs an <code>AbstractGranularDepthPort</code> with a random UUID
	 * as the name.
	 */
	public AbstractGranularDepthPort() {
		super();
	}

	/**
	 * Constructs an <code>AbstractGranularDepthPort</code> with the specified
	 * name.
	 * 
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong>
	 *            be <code>null</code> or an empty String.
	 */
	public AbstractGranularDepthPort(String name) {
		super(name);
	}

	/**
	 * Returns the granular depth of the <code>Port</code>.
	 * 
	 * @return the granular depth of the <code>Port</code>
	 */
	@Override
	public Integer getGranularDepth() {
		return granularDepth;
	}

	/**
	 * Sets the granular depth of the <code>Port</code>.
	 * 
	 * @param granularDepth
	 *            the granular depth of the <code>Port</code>
	 */
	@Override
	public void setGranularDepth(Integer granularDepth) {
		this.granularDepth = granularDepth;
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		AbstractGranularDepthPort clonePort = (AbstractGranularDepthPort) clone;
		clonePort.setGranularDepth(getGranularDepth());
	}
}
