package net.sf.taverna.scufl2.rdf.common;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.Named)
public interface Named extends WorkflowBean {
	
	@rdf(Ontology.name)
	public String getName();

	public void setName(String name);
}
