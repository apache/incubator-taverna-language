package org.apache.taverna.scufl2.cwl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLWorkflow {

	public String id;
	@JsonProperty(value="class")
	public String klass;
	public String description;
	public List<CWLInput> inputs;
	public List<CWLOutput> outputs;
	public List<Map<String,String>> requirements;
	public List<CWLStep> steps;
	public String baseCommand;
	public String stdout;
	
	
}
