package org.apache.taverna.robundle.validator;

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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.event.ListSelectionEvent;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.robundle.manifest.PathAnnotation;
import org.apache.taverna.robundle.manifest.PathMetadata;

/*
 * Validation Process:
 * The class validates RO bundles for manifest and aggregates.
 * Aggregate validation:
 * 	There are 03 cases,
 * 		1. If the aggregates are listed in manifest but not found in the container: AggregatenotFound Error
 * 		2. If aggregates contain an external uri, there will be a warning
 * 		3. If aggregate is not listed but included in the container, info-level warning.
 * 
 * Also in annotations....
 * If the about contain a /-based resource which is not listed or not in the container, there will be an error.
 */

public class RoValidator {
	
	private Path path;
	//Store all aggregates to be checked
	private List<PathMetadata> aggr;
	
	//Store all the annotations
	private List<PathAnnotation> anno;
	
	//List of resources in the bundle
	private ArrayList<String> items = new ArrayList<String>();
	
	
	//ArrayList for errors :- If aggregate is listed in manifest but not in bundle
	private ArrayList<String> errorList = new ArrayList<>();
	
	//ArrayList for warnings :- If files not listed in the manifest are included in bundle
	private ArrayList<String> infoWarningList = new ArrayList<String>();
	
	//If there are external urls
	private ArrayList<String> warning = new ArrayList<String>();
	
	
	public RoValidator(Path path){
		this.path = path;
		this.validate();
	}
	
	public void validate(){
		// Autoclose the zip file
		try(ZipFile zip = new ZipFile(new File(path.toString()))) {
			Enumeration<? extends ZipEntry> ent = zip.entries();
			while(ent.hasMoreElements()){
				ZipEntry entry = ent.nextElement();
				if(!entry.isDirectory()){
					items.add("/"+entry.getName());
				}
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
		try (Bundle bundle = Bundles.openBundle(path)) {	
			Manifest manifest = bundle.getManifest();
			this.aggr = manifest.getAggregates();
			this.anno = manifest.getAnnotations();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to open the bundle");
			e.printStackTrace();
		}
				
	}
	
	public ValidationReport check(){
		
		ValidationReport report = new ValidationReport();
		
		for(PathMetadata pm : this.aggr){
//			System.out.println("Path metedata " + pm);
			//If aggregates listed in manifest are not found in bundle...
			if(!this.items.contains(pm.toString())){
				
				/*
				 * Here it can be a external url or the file is missing
				 * */
				//If the aggregate is a external URL...
				if(pm.toString().contains("http://") || pm.toString().contains(".com")){
					this.warning.add(pm.toString());
				}else{
					this.errorList.add(pm.toString());	
				}
			}
		}
			
		/*
		 * There could be files in the bundle, which are not included as aggregates.
		 * There are default files: mimetype and LICENSE
		 * */
		
		
		//FIX ME : performance ???
		Set<String> set = new HashSet<>();
		for(PathMetadata p : this.aggr){
			set.add(p.toString());
		}
		
		for(String s : this.items){
			
			if(s.contains("mimetype")||s.toLowerCase().contains("license")||s.contains(".ro")){
				//This is ok and skip
			}else{
				if(!set.contains(s)){
					this.infoWarningList.add(path.toString());
				}
			}
		}
		
		report.setErrorList(this.errorList);
		report.setInfoWarnings(this.infoWarningList);
		report.setWarnings(this.warning);
		
		return report;
	}
	
	
	
	
}
