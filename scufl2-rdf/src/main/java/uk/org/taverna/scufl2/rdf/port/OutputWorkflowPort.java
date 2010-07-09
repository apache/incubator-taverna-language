package uk.org.taverna.scufl2.rdf.port;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;

@rdf(Ontology.CORE + "OutputWorkflowPort")
public interface OutputWorkflowPort extends ReceiverPort, WorkflowPort {

}
