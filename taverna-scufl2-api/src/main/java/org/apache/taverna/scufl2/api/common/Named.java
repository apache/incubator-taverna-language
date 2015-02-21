package org.apache.taverna.scufl2.api.common;

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


import java.util.regex.Pattern;

/**
 * A named {@link WorkflowBean}.
 * 
 * @author Alan R Williams
 */
@SuppressWarnings("rawtypes")
public interface Named extends WorkflowBean, Comparable {
	/**
	 * Name must not match this regular expression, e.g. must not include: slash
	 * (/), colon (:), ASCII control characters
	 */
	Pattern INVALID_NAME = Pattern.compile("^$|[/:\\x7f\\x00-\\x1f]");

	/**
	 * Returns the name of the {@link WorkflowBean}.
	 * 
	 * @return the name of the <code>WorkflowBean</code>
	 */
	String getName();

	/**
	 * Sets the name of the {@link WorkflowBean}.
	 * 
	 * The name <strong>must not</strong> be <code>null</code>, not be an empty
	 * String, and must not match the {@link #INVALID_NAME} regular expression.
	 * 
	 * @param name
	 *            the name of the <code>WorkflowBean</code>
	 */
	void setName(String name);
}
