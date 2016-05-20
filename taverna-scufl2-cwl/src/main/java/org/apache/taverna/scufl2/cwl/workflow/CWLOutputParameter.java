package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

public class CWLOutputParameter {

	public String id;
	
	public List<String> secondaryFiles;
	
	public List<String> format;
	
	public Boolean streamable;
	
	public List<String> type;
	
	public String label;
	
	public CWLCommandLineBinding outputBinding;
	
	
}
