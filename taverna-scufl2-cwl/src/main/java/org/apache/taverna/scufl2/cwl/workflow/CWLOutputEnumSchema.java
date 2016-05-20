package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutputEnumSchema {
	@JsonProperty(required=true, defaultValue="enum")
	public String type = "enum";
	
	public List<String> symbols;
	public List<String> secondaryFiles;
	public List<String> format;
	public Boolean streamable;
	public CWLCommandOutputBinding outputBinding;
	
}
