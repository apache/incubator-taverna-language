package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLInlineJavascriptRequirement extends CWLRequirement {
	@JsonProperty("class")
	public String klass;
	List<String> expressionLib;
}
