package uk.org.taverna.scufl2.rdf.bindings;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;
import uk.org.taverna.scufl2.rdf.common.WorkflowBean;

@rdf(Ontology.CORE + "Bindings")
public interface Bindings extends WorkflowBean {

}
