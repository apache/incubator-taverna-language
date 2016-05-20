package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutput {
	public String id;
	public String type;
	public String label;
	public String description;
	public boolean streamable;
	public String linkMerge;
	@JsonProperty(value="default")
	public String defaultValue;
	
	public String source;
	public Map<String,String> outputBinding;
}
