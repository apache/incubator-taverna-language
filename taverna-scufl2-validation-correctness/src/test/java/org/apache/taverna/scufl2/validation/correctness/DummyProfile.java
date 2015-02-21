/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness;
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


import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;


class DummyProfile extends Profile {

	private NamedSet<ProcessorBinding> processorBindings = null;

	private NamedSet<Configuration> configurations = null;

	private NamedSet<Activity> activities = null;

	/**
	 * @return the processorBindings
	 */
	@Override
	public NamedSet<ProcessorBinding> getProcessorBindings() {
		return processorBindings;
	}

	/**
	 * @param processorBindings the processorBindings to set
	 */
	public void setProcessorBindings(NamedSet<ProcessorBinding> processorBindings) {
		this.processorBindings = processorBindings;
	}

	/**
	 * @return the configurations
	 */
	@Override
	public NamedSet<Configuration> getConfigurations() {
		return configurations;
	}

	/**
	 * @param configurations the configurations to set
	 */
	public void setConfigurations(NamedSet<Configuration> configurations) {
		this.configurations = configurations;
	}

	/**
	 * @return the activities
	 */
	@Override
	public NamedSet<Activity> getActivities() {
		return activities;
	}

	/**
	 * @param activities the activities to set
	 */
	public void setActivities(NamedSet<Activity> activities) {
		this.activities = activities;
	}
	
	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			if (getActivities() != null) {
				children.add(getActivities());
			}
			if (getProcessorBindings() != null) {
				children.add(getProcessorBindings());
			}
			if (getConfigurations() != null) {
				children.add(getConfigurations());
			}
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
		}
		return visitor.visitLeave(this);
	}


}
