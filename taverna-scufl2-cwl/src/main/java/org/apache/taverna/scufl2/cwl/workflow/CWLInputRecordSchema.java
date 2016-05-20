package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLInputRecordSchema {
	@JsonProperty(required=true, defaultValue="record")
	public String type="record";
	
	public List<CWLInputRecordField> fields;
	
	public List<String> secondaryFiles;
	
	public List<String> format;
	
	public Boolean streamable; 
}
