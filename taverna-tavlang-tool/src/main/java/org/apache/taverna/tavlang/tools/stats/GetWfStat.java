package org.apache.taverna.tavlang.tools.stats;

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

import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.port.InputPort;
import org.apache.taverna.scufl2.api.port.OutputPort;

public class GetWfStat {
	
	private NamedSet<Workflow> set;
	private String logFile;
	private boolean verbose;
	private StringBuilder str_verb = new StringBuilder();
	private StringBuilder str_nverb = new StringBuilder();
	
	
	//If given file is a workflow file
	public GetWfStat(List<String> wf_files, String log, boolean verbose){
		this.logFile = log;
		this.verbose = verbose;
		
		for(String files : wf_files){
			File f = new File(files);
			if(f.isFile()){
				this.read(f);
				
			}else{
				System.err.println("Error reading the file " + f.getName());
			}
		}
		
		this.isVerbose();
		
		if(log!=null){
			this.writefile(this.str_verb.toString(), this.logFile);
		}
	}
	
	//Read the workflow file and extract the resources.
	//And append them to a String builder
	public void read(File file){
		WorkflowBundleIO io = new WorkflowBundleIO();
		try {
			WorkflowBundle wf = io.readBundle(file, null);
			this.set = wf.getWorkflows();

			//String to be written to a file.
			this.str_nverb.append(">>> Statistics of the workflow bundle: " + file.getName() + " <<<\n");
			this.str_verb.append(">>> Statistics of the workflow bundle: " + file.getName() + " <<<\n");
			
			for(Workflow wrf : set){
				//TODO :- Take each type of resource and make a system to view them to the user.
				String name = "Name of the workflow = " + wrf.getName();
				this.str_verb.append(name + "\n");
				this.str_nverb.append(name + "\n");

				
				String noP = "  |--> Number of Processors = " + wrf.getProcessors().size();
				this.str_verb.append(noP + "\n");
				this.str_nverb.append(noP + "\n");
				
				if(this.verbose && wrf.getProcessors().size()!=0){
					this.str_verb.append("  |     |--> Processors: " + "\n");
					
					for(Processor p : wrf.getProcessors()){
						this.str_verb.append("  |          |--> " + p.getName() + "\n");
					}
					this.str_verb.append("  |" + "\n");
				}
				
				this.str_verb.append("  |--> Number of Data Links = " + wrf.getDataLinks().size() + "\n");
				this.str_nverb.append("  |--> Number of Data Links = " + wrf.getDataLinks().size() + "\n");
				
				if(this.verbose && wrf.getDataLinks().size()!=0){
					this.str_verb.append("  |     |--> Data Links" + "\n");
					
					for(DataLink link : wrf.getDataLinks()){
						this.str_verb.append("  |          |--> " + link + "\n");
					}
					this.str_verb.append("  |" + "\n");
				}
				
				this.str_nverb.append("  |--> Number of Control Links = " + wrf.getControlLinks().size() + "\n");
				this.str_verb.append("  |--> Number of Control Links = " + wrf.getControlLinks().size() + "\n");
				
				
				if(this.verbose && wrf.getControlLinks().size()!=0){
					this.str_verb.append("  |     |--> Control Links\n");
					for(ControlLink link : wrf.getControlLinks()){
						this.str_verb.append("  |          |--> " + link + "\n");
					}
					this.str_verb.append("  |\n");
				}
				
				this.str_nverb.append("  |--> Number of Input ports = " + wrf.getInputPorts().size()+"\n");
				this.str_verb.append("  |--> Number of Input ports = " + wrf.getInputPorts().size()+"\n");
				
				
				if(this.verbose && wrf.getInputPorts().size()!=0){
					this.str_verb.append("  |     |--> Input Ports\n");
					for(InputPort iport : wrf.getInputPorts()){
						this.str_verb.append("  |          |--> " + iport.toString()+"\n");
					}
					this.str_verb.append("  |\n");
				}
			
				this.str_nverb.append("  |--> Number of Output Ports = " + wrf.getOutputPorts().size()+"\n");
				this.str_verb.append("  |--> Number of Output Ports = " + wrf.getOutputPorts().size()+"\n");
				
				if(this.verbose && wrf.getOutputPorts().size()!=0){
					this.str_verb.append("  |     |--> Output Ports\n");
					for(OutputPort o_port : wrf.getOutputPorts()){
						this.str_verb.append("  |          |--> " + o_port.toString()+"\n");
					}
					
					this.str_verb.append("\n");
				}
				
				this.str_nverb.append("\n");
				this.str_verb.append("\n");
			}
			
		} catch (ReaderException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//Saving the output into a log
	public void writefile(String report, String url){
		
			File file = new File(url);
			
			if(!file.isFile()){
				System.err.println("Error in writing the log file...");
			}
			
			FileWriter writer;
			BufferedWriter bfw;
			try {
				writer = new FileWriter(file);
				bfw = new BufferedWriter(writer);
				bfw.write(report);
				bfw.close();
				writer.close();
				System.out.println("Results were saved into " + file.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
		
	public void isVerbose(){
		if(verbose)
			System.out.println(this.str_verb.toString());
		else 
			System.out.println(this.str_nverb.toString());
	}
	

}
