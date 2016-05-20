package org.apache.taverna.scufl2.cwl.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLSubworkflowFeatureRequirement extends CWLRequirement {
	@JsonProperty(value="class", required=true)
	public String klass;
}
