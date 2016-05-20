package org.apache.taverna.scufl2.cwl.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLWorkflowStepOutput {
	@JsonProperty(required=true)
	String id;
}
