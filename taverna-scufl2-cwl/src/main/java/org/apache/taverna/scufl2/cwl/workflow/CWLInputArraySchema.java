package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CWLInputArraySchema {
	@JsonProperty(required=true, defaultValue="array")
	public String type = "array";
	
	List<JsonNode> types;
	
	List<String> secondaryFiles;
	List<String> format;
	Boolean streamable;
	CWLCommandLineBinding inputBinding;
	
}
