package uk.org.taverna.scufl2.rdf.iterationstrategy;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;
import uk.org.taverna.scufl2.rdf.common.WorkflowBean;

@rdf(Ontology.CORE + "IterationStrategyNode")
public interface IterationStrategyNode extends WorkflowBean {

}
