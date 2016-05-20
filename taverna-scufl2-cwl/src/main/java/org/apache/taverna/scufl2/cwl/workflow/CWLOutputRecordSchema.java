package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutputRecordSchema {
	@JsonProperty(required=true, defaultValue="record")
	public String type="record";
	
	List<CWLOutputRecordField> fields;
	List<String> secondaryFiles;
	List<String> format;
	boolean streamable;
}
