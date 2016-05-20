package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CWLWorkflowStep {

	@JsonProperty(required=true)
	public String id;
	
	@JsonProperty(required=true)
	public List<CWLWorkflowStepInput> in;
	
	@JsonProperty(required=true)
	public List<JsonNode> out;
	
	@JsonProperty(required=true)
	public JsonNode run;
	
	public List<CWLRequirement> requirements;
	
	public List<JsonNode> hints;
	
	public String label;
	public String description;
	public List<String> scatter;
	public CWLScatterMethod scatterMethod;
	
	
}
