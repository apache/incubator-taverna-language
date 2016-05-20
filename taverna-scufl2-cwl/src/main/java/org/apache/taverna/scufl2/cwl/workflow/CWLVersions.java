package org.apache.taverna.scufl2.cwl.workflow;

public enum CWLVersions {
	draft2("draft-2"),
	draft3Dev1("draft-3.dev1"),
	draft3Dev2("draft-3.dev2"),
	draft3Dev3("draft-3.dev3"),
	draft3Dev4("draft-3.dev4"),
	draft3Dev5("draft-3.dev5"),
	draft4Dev1("draft-4.dev1"),
	draft4("draft-4"),
	;
	
	
	public final String name;
	
	CWLVersions(String name) { 
		this.name = name;
	}
}
