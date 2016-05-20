package org.apache.taverna.scufl2.cwl.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLDirectory {
	private static final String DIRECTORY = "Directory";
	@JsonProperty(value="class", defaultValue=DIRECTORY, required=true)
	public String klass = DIRECTORY;
	@JsonProperty(required=true)
	public String path;

}
