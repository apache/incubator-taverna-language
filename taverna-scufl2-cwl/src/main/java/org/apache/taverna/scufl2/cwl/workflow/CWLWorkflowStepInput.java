package org.apache.taverna.scufl2.cwl.workflow;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CWLWorkflowStepInput {

	@JsonProperty(required=true)
	public String id;
	
	public List<String> source;
	
	public void setSource(String source) {
		this.source = Arrays.asList(source);
	}
	
	@JsonProperty(defaultValue="merge_nested")
	public CWLLinkMergeMethod linkMerge;
	
	@JsonProperty("value")
	public JsonNode defaultValue;
	public String valueFrom;
	
	
}
