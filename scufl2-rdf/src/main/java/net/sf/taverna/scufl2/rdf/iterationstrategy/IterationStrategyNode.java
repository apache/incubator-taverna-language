package net.sf.taverna.scufl2.rdf.iterationstrategy;

import net.sf.taverna.scufl2.rdf.common.Ontology;
import net.sf.taverna.scufl2.rdf.common.WorkflowBean;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "IterationStrategyNode")
public interface IterationStrategyNode extends WorkflowBean {

}
