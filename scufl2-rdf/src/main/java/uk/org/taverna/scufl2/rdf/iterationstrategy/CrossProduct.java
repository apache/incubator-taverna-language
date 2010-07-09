package uk.org.taverna.scufl2.rdf.iterationstrategy;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;

@rdf(Ontology.CORE + "CrossProduct")
public interface CrossProduct extends IterationStrategyNode {

}
