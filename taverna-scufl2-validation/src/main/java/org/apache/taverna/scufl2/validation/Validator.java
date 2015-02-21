package org.apache.taverna.scufl2.validation;
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


import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * How to check a workflow bundle for validity in some sense.
 * 
 * @param <T>
 *            The type of the validation reports produced by this validator.
 * @author Donal Fellows
 */
public interface Validator<T extends ValidationReport> {
	/**
	 * Validate the given workflow bundle.
	 * 
	 * @param workflowBundle
	 *            The bundle to validate.
	 * @return A description of whether the bundle is valid, and if not, how it
	 *         is invalid. (Determining the nature of the invalidity may require
	 *         knowing more about the nature of the validator than this
	 *         interface describes.)
	 */
	T validate(WorkflowBundle workflowBundle);
}
