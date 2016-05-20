package org.apache.taverna.scufl2.cwl.workflow;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class CWLSchemaDefRequirement extends CWLRequirement {
	public String klass;
	public List<JsonNode> types;
}
