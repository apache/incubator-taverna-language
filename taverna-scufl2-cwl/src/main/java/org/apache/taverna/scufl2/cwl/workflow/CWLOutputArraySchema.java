package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutputArraySchema {
	@JsonProperty(required = true, defaultValue = "array")
	public String type = "array";
	
	@JsonProperty(required=true)
	List<String> items;
	List<String> secondaryFiles;
	List<String> format;
	Boolean streamable;
	CWLCommandOutputBinding outputBinding;
	
}
