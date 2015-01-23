package org.apache.taverna.scufl2.translator.t2flow;
/*
 *
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
 *
*/


import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

public class ParserState {
	private org.apache.taverna.scufl2.api.activity.Activity currentActivity;
	private Profile currentProfile;
	private Processor currentProcessor;

	public org.apache.taverna.scufl2.api.activity.Activity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(
			org.apache.taverna.scufl2.api.activity.Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	public Profile getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(Profile currentProfile) {
		this.currentProfile = currentProfile;
	}

	public Processor getCurrentProcessor() {
		return currentProcessor;
	}

	public void setCurrentProcessor(Processor currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public ProcessorBinding getCurrentProcessorBinding() {
		return currentProcessorBinding;
	}

	public void setCurrentProcessorBinding(
			ProcessorBinding currentProcessorBinding) {
		this.currentProcessorBinding = currentProcessorBinding;
	}

	public WorkflowBundle getCurrentWorkflowBundle() {
		return workflowBundle;
	}

	public void setCurrentWorkflowBundle(WorkflowBundle workflowBundle) {
		this.workflowBundle = workflowBundle;
	}

	public T2Parser getCurrentT2Parser() {
		return currentT2Parser;
	}

	public void setCurrentT2Parser(T2Parser currentT2Parser) {
		this.currentT2Parser = currentT2Parser;
	}

	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	private ProcessorBinding currentProcessorBinding;
	private WorkflowBundle workflowBundle;
	private T2Parser currentT2Parser;
	private Workflow currentWorkflow;
	private T2FlowParser t2FlowParser;
	private Configuration currentConfiguration;
	private Configurable currentConfigurable;
    private String previousDispatchLayerName;

	/**
     * @return the previousDispatchLayerName
     */
    public String getPreviousDispatchLayerName() {
        return previousDispatchLayerName;
    }

    public final T2FlowParser getT2FlowParser() {
		return t2FlowParser;
	}

	public void setT2FlowParser(T2FlowParser t2FlowParser) {
		this.t2FlowParser = t2FlowParser;
		
	}

	public Configuration getCurrentConfiguration() {
		return currentConfiguration;
	}

	public void setCurrentConfiguration(Configuration currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

	public Configurable getCurrentConfigurable() {
		return currentConfigurable;
	}

	public void setCurrentConfigurable(Configurable currentConfigurable) {
		this.currentConfigurable = currentConfigurable;
	}

    public void setPreviousDispatchLayerName(String previousDispatchLayerName) {
        this.previousDispatchLayerName = previousDispatchLayerName;
    }
	
}
