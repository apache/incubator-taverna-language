package net.sf.taverna.scufl2.rdf.bindings;

import net.sf.taverna.scufl2.rdf.common.WorkflowBean;
import net.sf.taverna.scufl2.rdf.common.Ontology;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "Bindings")
public interface Bindings extends WorkflowBean {

}
