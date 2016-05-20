package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLOutputRecordField {
	@JsonProperty(required = true)
	public String name;
	@JsonProperty(required = true)
	public List<String> type;
	public String doc;
	public CWLCommandOutputBinding outputBinding;

}
