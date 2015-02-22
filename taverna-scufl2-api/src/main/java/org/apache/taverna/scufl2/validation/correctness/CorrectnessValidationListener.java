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


import java.net.URI;

import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.port.AbstractGranularDepthPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.validation.ValidationReport;


/**
 * @author alanrw
 */
public interface CorrectnessValidationListener extends ValidationReport {
	void emptyIterationStrategyTopNode(IterationStrategyTopNode bean);

	void mismatchConfigurableType(Configuration bean, Configurable configures);

	void nonAbsoluteURI(WorkflowBean bean, String fieldName, URI fieldValue);

	void nullField(WorkflowBean bean, String string);

	void portMentionedTwice(IterationStrategyNode subNode,
			IterationStrategyNode iterationStrategyNode);

	void portMissingFromIterationStrategyStack(Port p,
			IterationStrategyStack bean);

	void wrongParent(Child<?> iap);

	void negativeValue(WorkflowBean bean, String fieldName, Integer fieldValue);

	void outOfScopeValue(WorkflowBean bean, String fieldName, Object value);

	void incompatibleGranularDepth(AbstractGranularDepthPort bean,
			Integer depth, Integer granularDepth);
}
