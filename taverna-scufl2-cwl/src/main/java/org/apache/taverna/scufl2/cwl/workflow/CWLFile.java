package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CWLFile {
	private static final String FILE = "File";
	@JsonProperty(value="class", defaultValue=FILE, required=true)
	public String klass = FILE;
	@JsonProperty(required=true)
	public String path;
	public String checksum;
	public Long size;
	public List<CWLFile> secondaryFiles;
	public String format;
	
}
