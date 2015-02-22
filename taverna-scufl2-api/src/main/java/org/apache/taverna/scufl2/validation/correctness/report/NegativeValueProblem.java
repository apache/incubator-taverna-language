/**
 * 
 */
package org.apache.taverna.scufl2.validation.correctness.report;
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
import org.apache.taverna.scufl2.validation.ValidationProblem;


public class NegativeValueProblem extends ValidationProblem {
	private final String fieldName;
	private final Integer fieldValue;

	public NegativeValueProblem(WorkflowBean bean, String fieldName,
			Integer fieldValue) {
		super(bean);
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the fieldValue
	 */
	public Integer getFieldValue() {
		return fieldValue;
	}

	@Override
	public String toString() {
		return getBean() + " has " + fieldName + " of value " + fieldValue;
	}
}
