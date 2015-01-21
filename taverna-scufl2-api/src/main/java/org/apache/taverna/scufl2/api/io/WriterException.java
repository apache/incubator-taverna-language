package org.apache.taverna.scufl2.api.io;

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


import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * Thrown when there is a problem writing a {@link WorkflowBundle}
 * 
 * @see WorkflowBundleIO#writeBundle(WorkflowBundle, java.io.File, String)
 * @see WorkflowBundleIO#writeBundle(WorkflowBundle, java.io.OutputStream,
 *      String)
 * @see WorkflowBundleWriter#writeBundle(WorkflowBundle, java.io.File, String)
 * @see WorkflowBundleWriter#writeBundle(WorkflowBundle, java.io.OutputStream,
 *      String)
 */
@SuppressWarnings("serial")
public class WriterException extends Exception {

	/**
	 * Constructs an exception with no message or cause.
	 */
	public WriterException() {
	}

	/**
	 * Constructs an exception with the specified message and no cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 */
	public WriterException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the specified message and cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public WriterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an exception with the specified cause and and the same message
	 * as the cause (if the cause is not null).
	 * 
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public WriterException(Throwable cause) {
		super(cause);
	}
}
