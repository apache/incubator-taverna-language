/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl;
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


import java.util.HashMap;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;


/**
 * @author alanrw
 */
public class ParserState {
	private ScuflParser currentParser;
	private WorkflowBundle currentWorkflowBundle;
	private Profile currentProfile;
	private Workflow currentWorkflow;
	private Processor currentProcessor;
	private ScuflExtensionParser currentExtensionParser;
	private Activity currentActivity;

	private HashMap<Object, WorkflowBean> forwardMapping = new HashMap<>();

	public void setCurrentWorkflowBundle(WorkflowBundle wfBundle) {
		this.currentWorkflowBundle = wfBundle;
	}

	public WorkflowBundle getCurrentWorkflowBundle() {
		return currentWorkflowBundle;
	}

	public void setCurrentProfile(Profile profile) {
		this.currentProfile = profile;
	}

	/**
	 * @return the currentProfile
	 */
	public Profile getCurrentProfile() {
		return currentProfile;
	}

	/**
	 * @return the currentParser
	 */
	public ScuflParser getCurrentParser() {
		return currentParser;
	}

	/**
	 * @param currentParser
	 *            the currentParser to set
	 */
	public void setCurrentParser(ScuflParser currentParser) {
		this.currentParser = currentParser;
	}

	/**
	 * @param currentWorkflow
	 *            the currentWorkflow to set
	 */
	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}

	/**
	 * @return the currentWorkflow
	 */
	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void addMapping(Object scuflObject, WorkflowBean scufl2Object) {
		forwardMapping.put(scuflObject, scufl2Object);
	}

	/**
	 * @return the currentProcessor
	 */
	public Processor getCurrentProcessor() {
		return currentProcessor;
	}

	/**
	 * @param currentProcessor
	 *            the currentProcessor to set
	 */
	public void setCurrentProcessor(Processor currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	/**
	 * @return the currentExtensionParser
	 */
	public ScuflExtensionParser getCurrentExtensionParser() {
		return currentExtensionParser;
	}

	/**
	 * @param currentExtensionParser
	 *            the currentExtensionParser to set
	 */
	public void setCurrentExtensionParser(
			ScuflExtensionParser currentExtensionParser) {
		this.currentExtensionParser = currentExtensionParser;
	}

	/**
	 * @return the currentActivity
	 */
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * @param currentActivity
	 *            the currentActivity to set
	 */
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}
}
