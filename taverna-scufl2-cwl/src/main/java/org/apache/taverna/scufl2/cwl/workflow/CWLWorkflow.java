package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CWLWorkflow {

	@JsonProperty(required=true)
	public List<CWLInputParameter> inputs;
	@JsonProperty(required=true)
	public List<CWLWorkflowOutputParameter> outputs;
	@JsonProperty(value="class", required=true)
	public String klass;
	@JsonProperty(required=true)
	public List<CWLWorkflowStep> steps;
	public String id;
	public List<JsonNode> requirements;
	public List<JsonNode> hints;
	
	public String label;
	public String description;
	public String cwlVersion;
	
	
}
