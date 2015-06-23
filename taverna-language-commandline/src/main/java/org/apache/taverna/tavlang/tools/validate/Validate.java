package org.apache.taverna.tavlang.tools.validate;

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
import java.util.List;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;

/*
 * Takes .wfbundle/ .t2flow as the input
 * */
public class Validate {

	private String logfile;
	private List<String> files;
	WorkflowBundleIO io = new WorkflowBundleIO();

	public Validate(List<String> files, String file) {
		this.logfile = file;
		this.files = files;
	}

	public void read() {
		String rep = "";
		for (String f : files) {
				File wfile = new File(f);
				try {
				
				rep += validate(io.readBundle(wfile, null), wfile.getName());
				
				
			} catch (ReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	
			}
		}
		if (this.logfile != null) {
			saveToLog(rep);
		}else{
			System.out.println(rep);
		}
		
	}

	public String validate(WorkflowBundle wfb, String file) {
		System.out.println("Validation started...");
		StringBuilder report2 = new StringBuilder();
		
		CorrectnessValidationListener r = new ReportCorrectnessValidationListener();
		CorrectnessValidator v = new CorrectnessValidator();
		r = v.validate(wfb);
		
		String report = r.toString().replace(
				"ReportCorrectnessValidationListener [", "");

		String[] sections = report.split(", ");

		System.out.println("Validation completed.......");

		if (!r.detectedProblems()){
			report2.append("The workflow " + file + " has no errors. \n\n");
		}
			
		
//		System.out.println("The validation report for " + file);
		report2.append("The validation report for " + file + "......\n");
		report2.append("-------------------------------------------------------------------------------- \n");
		for (int i = 0; i < sections.length; i++) {
			String line = "-->"+sections[i].split("=")[0].replace("()", "").replace(
					"get", "")
					+ ":- ";
			report2.append(line);
			String line2 =  sections[i].split("=")[1].replace("[", "").replace("]","");
			if(line2.equals("")) report2.append("null \n\n");
			else report2.append("\t").append(line2).append("\n");
			
		
		}
		report2.append("--------------------------------------------------------------------------------- \n\n");
		
		if(r.detectedProblems())
			System.out.println(report2.toString());
		
		return report2.toString();

	}

	public void saveToLog(String s) {
		File logFile = new File(this.logfile);
		FileWriter logwriter;
		try {
			logwriter = new FileWriter(logFile);
			BufferedWriter blw = new BufferedWriter(logwriter);
			blw.write(s);
			blw.close();
			logwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
