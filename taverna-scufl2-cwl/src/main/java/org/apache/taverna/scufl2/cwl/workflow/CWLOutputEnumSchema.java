package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutputEnumSchema {
	@JsonProperty(required=true, defaultValue="enum")
	public String type = "enum";
	
	List<String> symbols;
	List<String> secondaryFiles;
	List<String> format;
	Boolean streamable;
	CWLCommandOutputBinding outputBinding;
	
}
