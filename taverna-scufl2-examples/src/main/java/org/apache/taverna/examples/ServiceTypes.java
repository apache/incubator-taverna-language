package org.apache.taverna.examples;

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


import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.api.profiles.Profile;

public class ServiceTypes {
	public static void main(String[] args) throws Exception, ReaderException,
			WriterException {
		for (String type :  new ServiceTypes().serviceTypes(args)) {
			System.out.println(type);
		}
	}

	public Set<String> serviceTypes(String[] filepaths) throws ReaderException, IOException, WriterException {
		
		Set<String> types = new LinkedHashSet<String>();
		
		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String filepath : filepaths) {
			File file = new File(filepath);
			// mediaType = null  --> guess
			WorkflowBundle wfBundle = io.readBundle(file, null);
			
			for (Profile profile : wfBundle.getProfiles()) {
				for (Activity activity : profile.getActivities()) {
					types.add(activity.getType().toASCIIString());
				}
			}			
		}
		return types;
	}

}
