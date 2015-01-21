/**
 * 
 */
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


import org.apache.taverna.scufl2.api.activity.Activity;

/**
 * An <code>OutputActivityPort</code> is a <Port> that outputs data from an
 * {@link Activity}.
 * 
 * @author Alan R Williams
 */
public class OutputActivityPort extends AbstractGranularDepthPort implements
		ActivityPort, OutputPort, GranularDepthPort {
	private Activity parent;

	/**
	 * Constructs an <code>OutputActivityPort</code> with a random UUID as the
	 * name.
	 */
	public OutputActivityPort() {
	}

	/**
	 * Constructs an <code>OutputPort</code> for the specified
	 * <code>Activity</code> with the specified name.
	 * <p>
	 * The <code>OutputPort</code> is added to the <code>Activity</code> (if the
	 * <code>Activity</code> is not <code>null</code>).
	 * 
	 * @param activity
	 *            the <code>Activity</code> to add this <code>Port</code> to.
	 *            Can be <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong>
	 *            be <code>null</code> or an empty String.
	 */
	public OutputActivityPort(Activity activity, String name) {
		super(name);
		setParent(activity);
	}

	@Override
	public Activity getParent() {
		return parent;
	}

	@Override
	public void setParent(Activity parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getOutputPorts().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getOutputPorts().add(this);
	}
}
