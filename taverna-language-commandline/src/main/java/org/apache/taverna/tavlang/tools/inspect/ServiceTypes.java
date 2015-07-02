package org.apache.taverna.tavlang.tools.inspect;


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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.profiles.Profile;

/*
 * List the service types used in workflow.
 * Supported file formats : .wfbundle, .t2flow
 * */

public class ServiceTypes {
	
	private List<String> filesList;
	private Set<String> types = new LinkedHashSet<String>();
	private String save;
	
	public ServiceTypes(List<String> filesList, String file) throws IOException, ReaderException{
		this.filesList = filesList;
		this.save = file;
		this.service();
		
	}
	
	public void service() throws ReaderException, IOException{
		WorkflowBundleIO io = new WorkflowBundleIO();
		StringBuilder sb = new StringBuilder();
		
		for (String filepath : filesList) {
			File file = new File(filepath);
			
			if(file.isDirectory()){
				for(File f : file.listFiles()){
				WorkflowBundle wfBundle = io.readBundle(f, null);
				System.out.println("Service types used in " + f.getCanonicalPath() + " :" +"\n");
				sb.append("Service types used in " + f.getCanonicalPath() + " :");
				for (Profile profile : wfBundle.getProfiles()) {
					for (Activity activity : profile.getActivities()) {
						this.types.add(activity.getType().toASCIIString());
					}
				}
				for(String t : types){
					System.out.println(t);
					sb.append(t + "\n");
				}
				System.out.println("\n**************************************************\n");
				sb.append("\n**************************************************\n");
				}
			}else{
			// mediaType = null  --> guess
				WorkflowBundle wfBundle = io.readBundle(file, null);
				System.out.println("Service types used in " + file.getCanonicalPath() + " :" + "\n");
				sb.append("Service types used in " + file.getCanonicalPath() + " :");
				for (Profile profile : wfBundle.getProfiles()) {
					for (Activity activity : profile.getActivities()) {
						this.types.add(activity.getType().toASCIIString());
					}
				}
				for(String t : types){
					System.out.println(t);
					sb.append(t + "\n");
				}
				
				System.out.println("\n**************************************************\n");
				sb.append("\n**************************************************\n");
			}
		}
		
		if(save!=null){
			File log = new File(save);
			FileWriter fw = new FileWriter(log);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();
			fw.close();
			System.out.println("Results were saved into " + save);
		}
		
	}
	
}
