package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CWLInputArraySchema {
	@JsonProperty(required=true, defaultValue="array")
	public String type = "array";
	
	public List<JsonNode> types;
	
	public List<String> secondaryFiles;
	public List<String> format;
	public Boolean streamable;
	public CWLCommandLineBinding inputBinding;
	
}
