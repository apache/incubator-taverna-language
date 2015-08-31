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

import java.util.ArrayList;


/*
 * Class for the validation report
 * There are basically 3 types of errors. 
 * 	1. Errors :- If listed aggregate/s are not found in the bundle
 * 	2. Warnings :- If there are any external url is listed under aggregates.
 * 	3. Info level warning :- If there are resources which are not included in the manifest
 * 
 * Output format
 * 	To get the output, the get methods must be called.
 * 	If there are no specific errors/ warnings then, "The bundle has no warnings/ errors" will be returned
 * 	If there are errors or warnings, 
 * 		Aggregates not found errors: List of aggregates which are not found
 * 		Info level warning : list of files/ resources
 * 		Warnings: list of urls
 * */
public class ValidationReport {
	
	//For aggregate not found errors
	private ArrayList<String> errorList;
	
	//For info level warnings
	private ArrayList<String> infoWarnings;
	
	//For warnings
	private ArrayList<String> warnings;
	
	//Constructor
	public ValidationReport(){
		
	}
	
	//To set the Warnings list
	public void setWarnings(ArrayList<String> warnings) {
		this.warnings = warnings;
	}
	
	//To set the infoLevelWarnings list
	public void setInfoWarnings(ArrayList<String> infoWarnings) {
		this.infoWarnings = infoWarnings;
	}
	
	//To set the errors list
	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	

	/*
	 * To get the results in general manner use these methods or straight from the RoVallidator code.
	 * */

	//Returns the errors list
	public ArrayList<String> getErrorList_l() {
		return errorList;
	}

	//Returns the info warnings list
	public ArrayList<String> getInfoWarnings_l() {
		return infoWarnings;
	}
	
	//Returns the Warnings list
	public ArrayList<String> getWarnings_l() {
		return warnings;
	}
	
	
//	In order to get the output with some verbosity
	public String getErrorList() {
		String errors = "Aggregate not found error: \n";
		if(this.errorList.size()!=0){
			for(String e : this.errorList){
				errors += e + "\n";
			}
		}else{
			return "The bundle has no errors";
		}
		
		return "Aggregate not found errors: " +"\n"+ errors;
	}

	

	public String getInfoWarnings() {
		String warning = "Warning: \n";
		if(this.warnings.size()!=0){
			for(String w : this.warnings){
				warning += w + " is an external URL \n";
			}
		}else{
			return "The bundle has no warnings";
		}
		return warning;
	}

	

	public String getWarnings() {
		String infoWarning = "Warning (info) : \n";
		
		if(this.infoWarnings.size()!=0){
			for(String iw : this.infoWarnings){
				infoWarning += iw + "\n";
			}
		}else{
			return "The Bundle has no Info level warnings";
		}
		return infoWarning;
		
	}

	

}
