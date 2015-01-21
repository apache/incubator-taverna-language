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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.core.Processor;


/**
 * A <code>ProcessorBinding</code> specifies that when enacting a
 * {@link org.apache.taverna.scufl2.api.core.Workflow Workflow}, if this particular
 * <code>ProcessorBinding</code> is used, then the boundActivity will be used to
 * implement the boundProcessor.
 * <p>
 * The <code>ProcessorBinding</code> specifies the sets of input and output port
 * bindings for the ports of the {@link Processor}. Note that there may not need
 * to be a binding for every <code>Processor</code> port, nor for every
 * {@link Activity} port. However, the ports must be of the bound
 * <code>Processor</code> and <code>Activity</code>.
 * <p>
 * It has not been decided if the binding must be unique for a given
 * <code>Processor</code> or <code>Activity</code> port within a
 * <code>ProcessorBinding</code>.
 * 
 * @author Alan R Williams
 */
public class ProcessorBinding extends AbstractNamed implements Child<Profile> {
	private Processor boundProcessor;
	private Activity boundActivity;

	private Set<ProcessorInputPortBinding> inputPortBindings = new HashSet<>();
	private Set<ProcessorOutputPortBinding> outputPortBindings = new HashSet<>();

	private Integer activityPosition;
	private Profile parent;

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<>();
			if (getInputPortBindings() != null)
				children.add(getInputPortBindings());
			if (getOutputPortBindings() != null)
				children.add(getOutputPortBindings());
			outer: for (Iterable<? extends WorkflowBean> it : children)
				for (WorkflowBean bean : it)
					if (!bean.accept(visitor))
						break outer;
		}
		return visitor.visitLeave(this);
	}

	/**
	 * Returns the relative position of the bound <code>Activity</code> within
	 * the <code>Processor</code> (for the purpose of Failover).
	 * <p>
	 * <code>Activity</code>s will be ordered by this position. Gaps will be
	 * ignored, overlapping <code>Activity</code> positions will have an
	 * undetermined order.
	 * 
	 * @return the relative position of the bound <code>Activity</code> within
	 *         the <code>Processor</code>
	 */
	public final Integer getActivityPosition() {
		return activityPosition;
	}

	/**
	 * Returns the <code>Activity</code> that will be used to enact the
	 * <code>Processor</code> if this ProcessorBinding is used.
	 * 
	 * @return the <code>Activity</code> that will be used to enact the
	 *         <code>Processor</code>
	 */
	public Activity getBoundActivity() {
		return boundActivity;
	}

	/**
	 * Returns the <code>Processor</code> for which a possible means of
	 * enactment is specified.
	 * 
	 * @return the <code>Processor</code> for which a possible means of
	 *         enactment is specified
	 */
	public Processor getBoundProcessor() {
		return boundProcessor;
	}

	/**
	 * Returns the bindings for individual input ports of the bound
	 * <code>Processor</code>.
	 * 
	 * @return the bindings for individual input ports of the bound
	 *         <code>Processor</code>
	 */
	public Set<ProcessorInputPortBinding> getInputPortBindings() {
		return inputPortBindings;
	}

	/**
	 * Returns the bindings for individual output ports of the bound
	 * <code>Processor</code>.
	 * 
	 * @return the bindings for individual output ports of the bound
	 *         <code>Processor</code>
	 */
	public Set<ProcessorOutputPortBinding> getOutputPortBindings() {
		return outputPortBindings;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	/**
	 * Sets the relative position of the bound <code>Activity</code> within the
	 * processor (for the purpose of Failover).
	 * <p>
	 * <code>Activity</code>s will be ordered by this position. Gaps will be
	 * ignored, overlapping <code>Activity</code> positions will have an
	 * undetermined order.
	 * 
	 * @param activityPosition
	 *            the relative position of the bound <code>Activity</code>
	 *            within the <code>Processor</code>
	 */
	public void setActivityPosition(Integer activityPosition) {
		this.activityPosition = activityPosition;
	}

	/**
	 * Sets the Activity that will be used to enact the <code>Processor</code>
	 * if this ProcessorBinding is used.
	 * 
	 * @param boundActivity
	 *            the Activity that will be used to enact the
	 *            <code>Processor</code>
	 */
	public void setBoundActivity(Activity boundActivity) {
		this.boundActivity = boundActivity;
	}

	/**
	 * Sets the <code>Processor</code> for which a possible means of enactment
	 * is specified.
	 * 
	 * @param boundProcessor
	 *            the <code>Processor</code> for which a possible means of
	 *            enactment is specified
	 */
	public void setBoundProcessor(Processor boundProcessor) {
		this.boundProcessor = boundProcessor;
	}

	/**
	 * Sets the bindings for individual input ports of the bound
	 * <code>Processor</code>.
	 * 
	 * @param inputPortBindings
	 *            the bindings for individual input ports of the bound
	 *            <code>Processor</code>
	 */
	public void setInputPortBindings(
			Set<ProcessorInputPortBinding> inputPortBindings) {
		this.inputPortBindings = inputPortBindings;
	}

	/**
	 * Sets the bindings for individual output ports of the bound
	 * <code>Processor</code>.
	 * 
	 * @param outputPortBindings
	 *            the bindings for individual output ports of the bound
	 *            <code>Processor</code>
	 */
	public void setOutputPortBindings(
			Set<ProcessorOutputPortBinding> outputPortBindings) {
		this.outputPortBindings = outputPortBindings;
	}

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getProcessorBindings().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getProcessorBindings().add(this);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getBoundProcessor() + " "
				+ getBoundActivity();
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		ProcessorBinding cloneBinding = (ProcessorBinding) clone;
		cloneBinding.setActivityPosition(getActivityPosition());
		cloneBinding.setBoundProcessor(cloning
				.cloneOrOriginal(getBoundProcessor()));
		cloneBinding.setBoundActivity(cloning
				.cloneOrOriginal(getBoundActivity()));
	}
}
