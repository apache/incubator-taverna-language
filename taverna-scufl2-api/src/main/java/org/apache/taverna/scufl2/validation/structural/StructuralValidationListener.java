package org.apache.taverna.scufl2.validation.structural;
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


import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.validation.ValidationReport;


public interface StructuralValidationListener extends ValidationReport {
	void passedProcessor(Processor p);

	void failedProcessorAdded(Processor p);

	void unresolvedProcessorAdded(Processor p);

	void missingMainIncomingLink(ReceiverPort owp);

	void unresolvedOutput(OutputWorkflowPort owp);

	void depthResolution(WorkflowBean owp, Integer portResolvedDepth);

	void missingIterationStrategyStack(Processor p);

	void emptyDotProduct(DotProduct dotProduct);

	void dotProductIterationMismatch(DotProduct dotProduct);

	void emptyCrossProduct(CrossProduct crossProduct);

	void dataLinkSender(DataLink dl);

	void dataLinkReceiver(DataLink dl);

	void unrecognizedIterationStrategyNode(
			IterationStrategyNode iterationStrategyNode);

	void incompleteWorkflow(Workflow w);
}
