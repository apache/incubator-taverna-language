package org.apache.taverna.scufl2.cwl.workflow;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLWorkflowOutputParameter {
	@JsonProperty(required=true)
	public String id;
	public List<String> secondaryFiles;
	public List<String> format;
	public boolean streamable;
	
	public List<String> type;
	
	public void setType(String type) {
		this.type = Arrays.asList(type);
	}
	
	public String label;
	public String description;
	public CWLCommandOutputBinding outputBinding;
	public List<String> source;
	
	public void setSource(String source) {
		this.source = Arrays.asList(source);
	}
	
	public CWLLinkMergeMethod linkMerge;
}
