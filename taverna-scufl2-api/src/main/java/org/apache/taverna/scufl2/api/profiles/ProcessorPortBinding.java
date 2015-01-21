package org.apache.taverna.scufl2.api.profiles;

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

import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.AbstractCloneable;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.port.ActivityPort;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.ProcessorPort;


/**
 * The binding between an <code>ActivityPort</code> and a
 * <code>ProcessorPort</code>.
 * <p>
 * This abstract class is realized as either an
 * {@link ProcessorInputPortBinding} or {@link ProcessorOutputPortBinding}. For
 * an input port binding, the binding goes from an {@link InputProcessorPort} to
 * an {@link InputActivityPort}, while for an output port binding the binding
 * goes from an {@link OutputActivityPort} to an {@link OutputProcessorPort}.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * @param <A>
 *            the <code>ActivityPort</code>
 * @param <P>
 *            the <code>ProcessorPort</code>
 *            
 */
public abstract class ProcessorPortBinding<A extends ActivityPort, P extends ProcessorPort>
		extends AbstractCloneable implements Child<ProcessorBinding> {
	private P boundProcessorPort;
	private A boundActivityPort;

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Return the {@link ActivityPort} which is passing data from/to the
	 * {@link #getBoundProcessorPort()}.
	 * 
	 * @return the <code>ActivityPort</code> to which data is passing from/to
	 *         the bound <code>ProcessorPort</code>
	 */
	public A getBoundActivityPort() {
		return boundActivityPort;
	}

	/**
	 * Return the {@link ProcessorPort} which is passing data to/from the
	 * {@link #getBoundActivityPort()}.
	 * 
	 * @return the <code>ProcessorPort</code> to which data is passing to/from
	 *         the bound <code>ActivityPort</code>
	 */
	public P getBoundProcessorPort() {
		return boundProcessorPort;
	}

	/**
	 * Sets the {@link ActivityPort} which is passing data from/to the
	 * {@link #getBoundProcessorPort()}.
	 * 
	 * @param boundActivityPort
	 *            the <code>ActivityPort</code> to which data is passing from/to
	 *            the bound <code>ProcessorPort</code>
	 */
	public void setBoundActivityPort(A boundActivityPort) {
		this.boundActivityPort = boundActivityPort;
	}

	/**
	 * Sets the {@link ProcessorPort} which is passing data to/from the
	 * {@link #getBoundActivityPort()}.
	 * 
	 * @param boundProcessorPort
	 *            the <code>ProcessorPort</code> to which data is passing
	 *            to/from the bound <code>ActivityPort</code>
	 */
	public void setBoundProcessorPort(P boundProcessorPort) {
		this.boundProcessorPort = boundProcessorPort;
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		@SuppressWarnings("unchecked")
		ProcessorPortBinding<A, P> cloneBinding = (ProcessorPortBinding<A, P>) clone;
		cloneBinding.setBoundActivityPort(cloning
				.cloneOrOriginal(getBoundActivityPort()));
		cloneBinding.setBoundProcessorPort(cloning
				.cloneOrOriginal(getBoundProcessorPort()));
	}

	// Derived operations

	/**
	 * Get all the annotations that pertain to this port binding.
	 * 
	 * @return The collection of annotations.
	 * @see Scufl2Tools#annotationsFor(Child)
	 */
	public Collection<Annotation> getAnnotations() {
		return getTools().annotationsFor(this);
	}

	/**
	 * Get the URI of this port binding.
	 * 
	 * @return The absolute URI.
	 * @see URITools#uriForBean(WorkflowBean)
	 */
	public URI getURI() {
		return getUriTools().uriForBean(this);
	}

	/**
	 * Get the URI of this port binding relative to another workflow element.
	 * 
	 * @return The relative URI.
	 * @see URITools#relativeUriForBean(WorkflowBean,WorflowBean)
	 */
	public URI getRelativeURI(WorkflowBean relativeTo) {
		return getUriTools().relativeUriForBean(this, relativeTo);
	}
}
