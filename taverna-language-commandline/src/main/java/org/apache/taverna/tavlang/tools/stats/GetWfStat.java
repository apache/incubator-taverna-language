package org.apache.taverna.tavlang.tools.stats;


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
	private StringBuilder str;
	
	
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
	}
	
	
	public void read(File file){
		WorkflowBundleIO io = new WorkflowBundleIO();
		try {
			WorkflowBundle wf = io.readBundle(file, null);
			this.set = wf.getWorkflows();
			System.out.println("Statistics of the workflow bundle: " + wf.getName());
			for(Workflow wrf : set){
				//TODO :- Take each type of resource and make a system to view them to the user.
				System.out.println("Name of the workflow = " + wrf.getName());
				System.out.println("  |--> Number of Processors = " + wrf.getProcessors().size());
				
				if(this.verbose && wrf.getProcessors().size()!=0){
					System.out.println("  |     |--> Processors: ");
					for(Processor p : wrf.getProcessors()){
						System.out.println("  |          |--> " + p.getName());
					}
					System.out.println("  |");
				}
				
				System.out.println("  |--> Number of Data Links = " + wrf.getDataLinks().size());
				
				if(this.verbose && wrf.getDataLinks().size()!=0){
					System.out.println("  |     |--> Data Links");
					for(DataLink link : wrf.getDataLinks()){
						System.out.println("  |          |--> " + link);
					}
					System.out.println("  |");
				}
				
				System.out.println("  |--> Number of Control Links = " + wrf.getControlLinks().size());
				
				if(this.verbose && wrf.getControlLinks().size()!=0){
					System.out.println("  |     |--> Control Links");
					for(ControlLink link : wrf.getControlLinks()){
						System.out.println("  |          |--> " + link);
					}
					System.out.println("  |");
				}
				
				System.out.println("  |--> Number of Input ports = " + wrf.getInputPorts().size());
				
				if(this.verbose && wrf.getInputPorts().size()!=0){
					System.out.println("  |     |--> Input Ports");
					for(InputPort iport : wrf.getInputPorts()){
						System.out.println("  |          |--> " + iport.toString());
					}
					System.out.println("  |");
				}
				
				System.out.println("  |--> Number of Output Ports = " + wrf.getOutputPorts().size());
				
				if(this.verbose && wrf.getOutputPorts().size()!=0){
					System.out.println("  |     |--> Input Ports");
					for(OutputPort o_port : wrf.getOutputPorts()){
						System.out.println("  |          |--> " + o_port.toString());
					}
					System.out.println("");
				}
				
				System.out.println("");
			}
			
		} catch (ReaderException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
