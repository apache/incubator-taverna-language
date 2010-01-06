package net.sf.taverna.scufl2.rdf.common;

import java.util.Set;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "Configurable")
public interface Configurable extends WorkflowBean {
	
	@rdf(Ontology.CORE + "hasConfiguration")
	public Set<ConfigurableProperty> getConfigurableProperties();
	
	
}
