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
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Ported;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;


/**
 * A <code>Processor</code> is a {@link Workflow} component that controls the
 * invocation of activities.
 * <p>
 * When a <code>Workflow</code> is run, a particular {@link Activity} will be
 * specified as bound to the <code>Processor</code> by the {@link Profile}.
 * <p>
 * A <code>Processor</code> contains an {@link IterationStrategyStack} to
 * describe iterations, and may have {@link InputProcessorPort input} and
 * {@link OutputProcessorPort output} ports. The <code>Processor</code> can be
 * configured with a {@link Configuration} within a {@link Profile} to specify
 * execution details such as retries or parallel jobs.
 * <p>
 * The {@link #getType()} of a Processor is normally fixed to the value given by
 * PROCESSOR_TYPE. The configuration of a processor should
 * correspondingly be of the type given by the constant CONFIG_TYPE.
 * <p>
 * The default (implied) configuration of a Processor is as of Taverna 3.0 alpha
 * 2:
 * 
 * <pre>
 * { 
 *  "retry": {
 *     "maxRetries": 0,
 *     "initialDelay": 1000,
 *     "maxDelay": 5000,
 *     "backoffFactor": 1.0
 *  }, 
 *  "parallelize": {
 *      "maxJobs": 1
 *  }
 * </pre>
 * 
 * These defaults mean that the processor would not retry the operation, and
 * would only perform one concurrent invocation within a given workflow run.
 * <p>
 * You can provide a partial configuration, e.g. to activate 5 retries using the
 * default initialDelay, add a {@link Configuration} of this {@link Processor}
 * to the {@link Profile} with the keys:
 * 
 * <pre>
 * { 
 *   "retry": {
 *     "maxRetries": 5
 *   }
 * }
 * </pre>
 * <p>
 * Additionally, do..while-style looping can be configured using the key "loop":
 * 
 * <pre>
 * {
 *   "loop": {
 *     "comparePort": "outputB",
 *     "comparison": "EqualTo",
 *     "compareValue": "15",
 *     "delay": 0.5,
 *     "runFirst": true,
 *     "isFeedBack": false,
 *     "conditionActivity": "looping-loop"
 *   }
 * }
 * </pre>
 * <p>
 * Note that this is different from looping over incoming values, which happens
 * according to the {@link #getIterationStrategyStack()}..
 * </p>
 * The above loop configuration would repeat calling of the activity bound to
 * this processor until its output "outputB" is equal to the string value "15" -
 * with a delay of 0.5 seconds between each try.
 * <p>
 * if <code>"isFeedBack": true</code>, then outputs from the processor will on
 * repeated invocations replace the original input values where the port names
 * match. Note that the processor ports should be connected in the containing
 * workflow.
 * <p>
 * <code>"runFirst": true</code> means that the activity is called at least
 * once, which is generally needed to get a port value to compare.
 * <p>
 * The <code>"conditionActivity"</code> indicates the name of an
 * {@link Activity} within the {@link Profile}, here called "looping-loop". This
 * activity is invoked with the current processor output values as input ports,
 * and if its output port "loop" contains the string value "true", then the
 * processor is invoked again. If the condition output port matches a processor
 * input port, then the returned values are used instead of the original
 * processor inputs. The conditionActivity is then repeated on the new output
 * values, repeated until "loop" no longer is "true".
 * <p>
 * As of Taverna 3 alpha 2, the "conditionActivity" is called directly without
 * consideration of the other parameters. This typically contains a beanshell
 * script generated from the above parameters and performs the actual
 * comparisons.
 * <p>
 * FIXME: This conditionActivity currently has to be regenerated when the other
 * parameters have changed. This now happens within the Taverna 3 workbench user
 * interface when configuring looping. The ideal SCUFL2 behaviour would be to
 * EITHER provide a custom conditionActivity OR the parameters.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Processor extends AbstractNamed implements Child<Workflow>,
		Ported, Configurable {
	public static final URI PROCESSOR_TYPE = URI
			.create("http://ns.taverna.org.uk/2010/scufl2#Processor");
	public static final URI CONFIG_TYPE = URI
			.create("http://ns.taverna.org.uk/2010/scufl2#ProcessorConfig");

	private final NamedSet<OutputProcessorPort> outputPorts = new NamedSet<>();
	private final NamedSet<InputProcessorPort> inputPorts = new NamedSet<>();
	private IterationStrategyStack iterationStrategyStack = new IterationStrategyStack(
			this);
	private Workflow parent;
	private URI type = PROCESSOR_TYPE;

	/**
	 * Constructs a <code>Processor</code> with a random UUID as the name and no
	 * parent {@link Workflow}.
	 */
	public Processor() {
		super();
	}

	/**
	 * Constructs a <code>Processor</code> with the specified parent
	 * {@link Workflow} and name.
	 * 
	 * @param parent
	 *            the <code>Workflow</code> to set as the <code>Processor</code>
	 *            's parent. Can be <code>null</code>.
	 * @param name
	 *            the name of the <code>Processor</code>. <strong>Must
	 *            not</strong> be <code>null</code> or an empty String.
	 */
	public Processor(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<>();
			children.add(getInputPorts());
			children.add(getOutputPorts());
			outer: for (Iterable<? extends WorkflowBean> it : children)
				for (WorkflowBean bean : it)
					if (!bean.accept(visitor))
						break outer;
			if (getIterationStrategyStack() != null)
				getIterationStrategyStack().accept(visitor);
		}
		return visitor.visitLeave(this);
	}

	/**
	 * Returns the <code>NamedSet</code> of input ports.
	 * 
	 * Returns an empty <code>NamedSet</code> if there are no input ports.
	 * 
	 * @return the input ports
	 */
	@Override
	public NamedSet<InputProcessorPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * Returns the <code>IterationStrategyStack</code> or <code>null</code> if
	 * there is no <code>IterationStrategyStack</code>.
	 * 
	 * @return the <code>IterationStrategyStack</code> or <code>null</code> if
	 *         there is no <code>IterationStrategyStack</code>
	 */
	public IterationStrategyStack getIterationStrategyStack() {
		return iterationStrategyStack;
	}

	/**
	 * Returns the <code>NamedSet</code> of output ports.
	 * 
	 * Returns an empty <code>NamedSet</code> if there are no output ports.
	 * 
	 * @return the output ports
	 */
	@Override
	public NamedSet<OutputProcessorPort> getOutputPorts() {
		return outputPorts;
	}

	/**
	 * Returns the parent <code>Workflow</code> of null if this
	 * <code>Processor</code> is an orphan.
	 * 
	 * @return the parent <code>Workflow</code> of null if this
	 *         <code>Processor</code> is an orphan
	 */
	@Override
	public Workflow getParent() {
		return parent;
	}

	/**
	 * Sets the input ports.
	 * 
	 * @return the input ports. <strong>Must not</strong> be <code>null</code>
	 */
	public void setInputPorts(Set<InputProcessorPort> inputPorts) {
		this.inputPorts.clear();
		this.inputPorts.addAll(inputPorts);
	}

	/**
	 * Sets the <code>IterationStrategyStack</code>.
	 * 
	 * @param iterationStrategyStack
	 *            the <code>IterationStrategyStack</code>. Can be
	 *            <code>null</code>
	 */
	public void setIterationStrategyStack(
			IterationStrategyStack iterationStrategyStack) {
		this.iterationStrategyStack = iterationStrategyStack;
		if (iterationStrategyStack != null)
			iterationStrategyStack.setParent(this);
	}

	/**
	 * Sets the output ports.
	 * 
	 * @return the output ports. <strong>Must not</strong> be <code>null</code>
	 */
	public void setOutputPorts(Set<OutputProcessorPort> outputPorts) {
		this.outputPorts.clear();
		this.outputPorts.addAll(outputPorts);
	}

	@Override
	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getProcessors().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getProcessors().add(this);
	}

	@Override
	public URI getType() {
		return type;
	}

	@Override
	public void setType(URI type) {
		this.type = type;
	}

	// Derived operations, implemented via Scufl2Tools

	/**
	 * Find all configurations of this processor in a profile.
	 * 
	 * @param profile
	 *            The profile to search within.
	 * @return The configurations that were found.
	 * @see Scufl2Tools#configurationsFor(Configurable,Profile)
	 */
	public List<Configuration> getConfigurations(Profile profile) {
		return getTools().configurationsFor(this, profile);
	}

	/**
	 * Find the configuration of this processor in a profile.
	 * 
	 * @param profile
	 *            The profile to search within.
	 * @return The configuration.
	 * @throws IllegalStateException
	 *             If there are more than one configuration for the processor.
	 * @throws IndexOutOfBoundsException
	 *             If there aren't any configurations for the processor.
	 * @see Scufl2Tools#configurationFor(Configurable,Profile)
	 */
	public Configuration getConfiguration(Profile profile) {
		return getTools().configurationFor(this, profile);
	}

	/**
	 * Create a configuration for a processor.
	 * 
	 * @param profile
	 *            The profile to create the configuration within.
	 * @return The created configuration.
	 * @see Scufl2Tools#createConfigurationFor(Configurable,Profile)
	 */
	public Configuration createConfiguration(Profile profile) {
		return getTools().createConfigurationFor(this, profile);
	}

	/**
	 * Get the configuration of the activity bound to this processor in the
	 * given profile.
	 * 
	 * @param profile
	 *            The profile that provides the binding and the configuration.
	 * @return The <i>activity</i> configuration.
	 * @see Scufl2Tools#configurationForActivityBoundToProcessor(Processor,Profile)
	 */
	public Configuration getActivityConfiguration(Profile profile) {
		return getTools().configurationForActivityBoundToProcessor(this,
				profile);
	}

	/**
	 * Create an untyped activity for the processor.
	 * 
	 * @param profile
	 *            The profile to create the activity within.
	 * @return The created activity.
	 * @see Scufl2Tools#createActivityFromProcessor(Processor,Profile)
	 */
	public Activity createActivity(Profile profile) {
		return getTools().createActivityFromProcessor(this, profile);
	}

	/**
	 * Get the workflow nested within this processor.
	 * 
	 * @param profile
	 *            The profile that bound the nested workflow to this processor.
	 * @return The nested workflow, or <tt>null</tt> if it does not exist (e.g.,
	 *         if this processor is holding a different type of activity).
	 * @see Scufl2Tools#nestedWorkflowForProcessor(Processor,Profile)
	 */
	public Workflow getNestedWorkflow(Profile profile) {
		return getTools().nestedWorkflowForProcessor(this, profile);
	}

	/**
	 * Get the control links that prevent this processor from running. Does not
	 * judge whether they are <i>currently</i> blocking the processor.
	 * 
	 * @return The list of control links blocking this processor.
	 * @see Scufl2Tools#controlLinksBlocking(Processor)
	 */
	public List<BlockingControlLink> controlLinksBlocking() {
		return getTools().controlLinksBlocking(this);
	}

	/**
	 * Get the control links that this processor will notify once it completes
	 * running.
	 * 
	 * @return The list of control links waiting for this processor.
	 * @see Scufl2Tools#controlLinksWaitingFor(Processor)
	 */
	public List<BlockingControlLink> controlLinksWaitingFor() {
		return getTools().controlLinksWaitingFor(this);
	}

	/**
	 * Get the binding for this processor in the given profile.
	 * 
	 * @param profile
	 *            The profile to search within.
	 * @return The processor binding.
	 * @throws IllegalStateException
	 *             If there are more than one binding for the processor.
	 * @throws IndexOutOfBoundsException
	 *             If there aren't any bindings for the processor.
	 * @see Scufl2Tools#processorBindingForProcessor(Processor,Profile)
	 */
	public ProcessorBinding getBinding(Profile profile) {
		return getTools().processorBindingForProcessor(this, profile);
	}

	/**
	 * Get the activity bound to this processor in the given profile.
	 * 
	 * @param profile
	 *            The profile to search within.
	 * @return The activity.
	 * @throws IllegalStateException
	 *             If there are more than one activity for the processor.
	 * @throws IndexOutOfBoundsException
	 *             If there aren't any activities for the processor.
	 * @see #getBinding(Profile)
	 * @see Scufl2Tools#processorBindingForProcessor(Processor,Profile)
	 */
	public Activity getActivity(Profile profile) {
		return getBinding(profile).getBoundActivity();
	}

	/**
	 * Get the collection of processors that can be downstream of this
	 * processor.
	 * 
	 * @return A set of processors that it is legal to have a datalink or
	 *         control link from this processor to.
	 * @see Scufl2Tools#possibleDownStreamProcessors(Workflow,Processor)
	 */
	public Set<Processor> getPossibleDownStreamProcessors() {
		return getTools().possibleDownStreamProcessors(getParent(), this);
	}

	/**
	 * Get the collection of processors that can be upstream of this
	 * processor.
	 * 
	 * @return A set of processors that it is legal to have a datalink or
	 *         control link to this processor from.
	 * @see Scufl2Tools#possibleUpStreamProcessors(Workflow,Processor)
	 */
	public Set<Processor> getPossibleUpStreamProcessors() {
		return getTools().possibleUpStreamProcessors(getParent(), this);
	}
}
