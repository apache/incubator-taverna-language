package org.apache.taverna.scufl2.api.core;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.taverna.scufl2.api.annotation.Revisioned;
import org.apache.taverna.scufl2.api.common.AbstractRevisioned;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;


/**
 * A <code>Workflow</code> is a set of {@link Processor}s and {@link DataLink}s
 * between the <code>Processor</code>s. <code>Workflow</code>s may also have
 * input and output ports.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Workflow extends AbstractRevisioned implements
		Child<WorkflowBundle>, Ported, Revisioned {
	public static final URI WORKFLOW_ROOT = URI
			.create("http://ns.taverna.org.uk/2010/workflow/");

	private final TreeSet<DataLink> dataLinks = new TreeSet<>();
	private final TreeSet<ControlLink> controlLinks = new TreeSet<>();
	private final NamedSet<InputWorkflowPort> inputPorts = new NamedSet<>();
	private final NamedSet<OutputWorkflowPort> outputPorts = new NamedSet<>();
	private final NamedSet<Processor> processors = new NamedSet<>();
	private WorkflowBundle parent;

	/**
	 * Constructs a <code>Workflow</code> with a name based on a random UUID.
	 */
	public Workflow() {	
	}

	/**
     * Constructs a <code>Workflow</code> with the specified name.
     * 
     * @param name
     *            The name of the <code>Workflow</code>. <strong>Must
     *            not</strong> be <code>null</code> or an empty String.
     */
    public Workflow(String name) {
        super(name);
    }
	
	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<WorkflowBean> children = new ArrayList<>();
			children.addAll(getInputPorts());
			children.addAll(getOutputPorts());
			children.addAll(getProcessors());
			children.addAll(getDataLinks());
			children.addAll(getControlLinks());
			children.add(getCurrentRevision());
			for (WorkflowBean bean : children)
				if (!bean.accept(visitor))
					break;
		}
		return visitor.visitLeave(this);
	}

	/**
	 * Returns the <code>ControlLink</code>s.
	 * 
	 * If there are no <code>ControlLink</code>s an empty set is returned.
	 * 
	 * @return the <code>ControlLink</code>s
	 */
	public Set<ControlLink> getControlLinks() {
		return controlLinks;
	}
	
	/**
	 * Returns the <code>DataLink</code>s.
	 * 
	 * If there are no <code>DataLink</code>s an empty set is returned.
	 * 
	 * @return the <code>DataLink</code>s.
	 */
	public Set<DataLink> getDataLinks() {
		return dataLinks;
	}

	/**
	 * Returns the <code>InputWorkflowPort</code>s.
	 * 
	 * If there are no <code>InputWorkflowPort</code>s an empty set is returned.
	 * 
	 * @return the <code>InputWorkflowPort</code>s.
	 */
	@Override
	public NamedSet<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * Returns the <code>OutputWorkflowPort</code>s.
	 * 
	 * If there are no <code>OutputWorkflowPort</code>s an empty set is
	 * returned.
	 * 
	 * @return the <code>OutputWorkflowPort</code>s.
	 */
	@Override
	public NamedSet<OutputWorkflowPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public WorkflowBundle getParent() {
		return parent;
	}

	/**
	 * Returns the <code>Processor</code>s.
	 * 
	 * If there are no <code>Processor</code>s an empty set is returned.
	 * 
	 * @return the <code>Processor</code>s.
	 */
	public NamedSet<Processor> getProcessors() {
		return processors;
	}

	/**
	 * Set the <code>ControlLink</code>s to be the contents of the specified
	 * set.
	 * <p>
	 * <code>ControlLink</code>s can be added by using
	 * {@link #getControlLinks()}.add(controlLink).
	 * 
	 * @param controlLinks
	 *            the <code>ControlLink</code>s. <strong>Must not</strong> be
	 *            null
	 */
	public void setControlLinks(Set<ControlLink> controlLinks) {
		this.controlLinks.clear();
		this.controlLinks.addAll(controlLinks);
	}
	
	/**
	 * Set the <code>DataLink</code>s to be the contents of the specified set.
	 * <p>
	 * <code>DataLink</code>s can be added by using {@link #getDataLinks()}
	 * .add(dataLink).
	 * 
	 * @param dataLinks
	 *            the <code>DataLink</code>s. <strong>Must not</strong> be null
	 */
	public void setDataLinks(Set<DataLink> dataLinks) {
		this.dataLinks.clear();
		this.dataLinks.addAll(dataLinks);
	}

	/**
	 * Set the <code>InputWorkflowPort</code>s to be the contents of the
	 * specified set.
	 * <p>
	 * <code>InputWorkflowPort</code>s can be added by using
	 * {@link #getInputWorkflowPorts()}.add(inputPort).
	 * 
	 * @param inputPorts
	 *            the <code>InputWorkflowPort</code>s. <strong>Must not</strong>
	 *            be null
	 */
	public void setInputPorts(Set<InputWorkflowPort> inputPorts) {
		this.inputPorts.clear();
		for (InputWorkflowPort inputPort : inputPorts)
			inputPort.setParent(this);
	}

	/**
	 * Set the <code>OutputWorkflowPort</code>s to be the contents of the
	 * specified set.
	 * <p>
	 * <code>OutputWorkflowPort</code>s can be added by using
	 * {@link #getOutputWorkflowPorts()}.add(outputPort).
	 * 
	 * @param outputPorts
	 *            the <code>OutputWorkflowPort</code>s. <strong>Must
	 *            not</strong> be null
	 */
	public void setOutputPorts(Set<OutputWorkflowPort> outputPorts) {
		this.outputPorts.clear();
		for (OutputWorkflowPort outputPort : outputPorts)
			outputPort.setParent(this);
	}

	@Override
	public void setParent(WorkflowBundle parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getWorkflows().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getWorkflows().add(this);
	}

	/**
	 * Set the <code>Processor</code>s to be the contents of the specified set.
	 * <p>
	 * <code>Processor</code>s can be added by using {@link #getProcessors()}
	 * .add(processor).
	 * 
	 * @param processors
	 *            the <code>Processor</code>s. <strong>Must not</strong> be null
	 */
	public void setProcessors(Set<Processor> processors) {
		this.processors.clear();
		for (Processor processor : processors)
			processor.setParent(this);
	}

	/**
	 * Updates the workflow identifier.
	 * <p>
	 * The {@link #getCurrentRevision()} will be replaced using using
	 * {@link #newRevision()}.
	 * 
	 */
	public void updateWorkflowIdentifier() {
		newRevision();
	}

	@SuppressWarnings("unused")
	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder("[");
		String sep = "";
		int i = 0;
		for (Object o : collection) {
			builder.append(sep).append(o);
			sep = ", ";
			if (++i >= maxLen)
				break;
		}
		return builder.append("]").toString();
	}

	@Override
	protected URI getIdentifierRoot() {
		return WORKFLOW_ROOT;
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		Workflow cloneWorkflow = (Workflow)clone;
		cloneWorkflow.setCurrentRevision(cloning.cloneIfNotInCache(getCurrentRevision()));		
	}
}
