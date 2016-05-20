package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLInputEnumSchema {

	@JsonProperty(defaultValue="enum", required=true)
	public String type = "enum";
	
	@JsonProperty(required=true)
	public List<String> symbols;
	
	public List<String> secondaryFiles;
	
	public List<String> format;
	
	public Boolean streamable;
	
	public CWLCommandLineBinding inputBinding;

}
