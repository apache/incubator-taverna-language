package org.apache.taverna.scufl2.cwl.workflow;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CWLInputParameter {
	
	@JsonProperty(required=true)
	public String id;

	public List<String> secondaryFiles;
	
	public List<String> format;
	
	public Boolean streamable;
	
	public List<String> type;
	
	public void setType(String type) {
		this.type = Arrays.asList(type);
	}
	
	public String label;
	
	public String description;
	
	public CWLCommandLineBinding inputBinding;
	
	@JsonProperty("default")
	public JsonNode defaultValue;
}
