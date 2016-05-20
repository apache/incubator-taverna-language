package org.apache.taverna.scufl2.cwl.workflow;

public enum CWLType {
	Null("null"),
	Boolean("boolean"),
	Int("int"),
	Long("long"),
	Float("float"),
	Double("double"),
	string("string"),
	File("File");
	
	public final String name;
	
	CWLType(String name){
		this.name = name;
	}
	public String toString() {
		return name;
	};
	
}
