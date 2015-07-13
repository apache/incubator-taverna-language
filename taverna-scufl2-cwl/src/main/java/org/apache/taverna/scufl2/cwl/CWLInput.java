package org.apache.taverna.scufl2.cwl;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLInput {
	public String id;
	public String description;
	@JsonProperty(value="default")
	public String defaultValue;
	public String type;
	public Map<String,String> inputBinding;
}
