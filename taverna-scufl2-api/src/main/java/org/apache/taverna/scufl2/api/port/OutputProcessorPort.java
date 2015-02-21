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


import java.util.List;

import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;


/**
 * An <code>OutputProcessorPort</code> is a <Port> that outputs data from a
 * {@link Processor}.
 */
public class OutputProcessorPort extends AbstractGranularDepthPort implements
		SenderPort, ProcessorPort, OutputPort, GranularDepthPort {
	private Processor parent;

	/**
	 * Constructs an <code>OutputProcessorPort</code> with a random UUID as the
	 * name.
	 */
	public OutputProcessorPort() {
		super();
	}

	/**
	 * Constructs an <code>OutputProcessorPort</code> for the specified
	 * <code>Processor</code> with the specified name.
	 * <p>
	 * The <code>OutputPort</code> is added to the <code>Processor</code> (if
	 * the <code>Processor</code> is not <code>null</code>).
	 * 
	 * @param parent
	 *            the <code>Processor</code> to add this <code>Port</code> to.
	 *            Can be <code>null</code>
	 * @param name
	 *            the name of the <code>Port</code>. <strong>Must not</strong>
	 *            be <code>null</code> or an empty String.
	 */
	public OutputProcessorPort(Processor parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public Processor getParent() {
		return parent;
	}

	@Override
	public void setParent(Processor parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getOutputPorts().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getOutputPorts().add(this);
	}

	// Derived operations, implemented via Scufl2Tools

	/**
	 * Get the datalinks leading from this port.
	 * 
	 * @return the collection of links.
	 * @see Scufl2Tools#datalinksFrom(SenderPort)
	 */
	public List<DataLink> getDatalinksFrom() {
		return getTools().datalinksFrom(this);
	}
}
