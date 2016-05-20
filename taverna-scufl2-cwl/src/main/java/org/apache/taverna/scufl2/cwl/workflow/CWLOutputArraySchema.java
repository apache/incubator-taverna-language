package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutputArraySchema {
	@JsonProperty(required = true, defaultValue = "array")
	public String type = "array";
	
	@JsonProperty(required=true)
	public List<String> items;
	public List<String> secondaryFiles;
	public List<String> format;
	public Boolean streamable;
	public CWLCommandOutputBinding outputBinding;
	
}
