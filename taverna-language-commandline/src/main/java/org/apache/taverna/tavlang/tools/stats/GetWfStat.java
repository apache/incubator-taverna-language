package org.apache.taverna.tavlang.tools.stats;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

public class GetWfStat {
	
	public Workflow wflow;
	public NamedSet<Workflow> set;
	public String logFile;
	
	//If given file is a workflow file
	public GetWfStat(List<String> wf_files, String log){
		this.logFile = log;
		for(String files : wf_files){
			File f = new File(files);
			if(f.isFile()){
				this.read(f);
			}else{
				System.out.println("Error reading the file " + f.getName());
			}
		}
	}
	
	public void read(File file){
		WorkflowBundleIO io = new WorkflowBundleIO();
		try {
			WorkflowBundle wf = io.readBundle(file, null);
			this.set = wf.getWorkflows();
			
			for(Workflow wrf : set){
				System.out.println(wrf.getInputPorts());
			}
			
		} catch (ReaderException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
