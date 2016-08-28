package org.apache.taverna.databundle;
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


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ErrorDocument {
	private List<Path> causedBy = new ArrayList<>();
	private String message = "";
	private String trace = "";

	public List<Path> getCausedBy() {
		return causedBy;
	}

	public String getMessage() {
		return message;
	}

	public String getTrace() {
		return trace;
	}

	public void setCausedBy(List<Path> causedBy) {
		this.causedBy.clear();
		if (causedBy != null)
			this.causedBy.addAll(causedBy);
	}

	public void setMessage(String message) {
		if (message == null)
			message = "";
		this.message = message;
	}

	public void setTrace(String trace) {
		if (trace == null)
			trace = "";
		this.trace = trace;
	}
	
	@Override
	public String toString() {
		return "Error: " + getMessage() + "\n" + trace;
		// TODO: also include the causedBy paths?
	}
	
}
