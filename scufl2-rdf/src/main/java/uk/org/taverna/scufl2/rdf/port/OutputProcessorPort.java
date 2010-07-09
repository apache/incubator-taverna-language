package uk.org.taverna.scufl2.rdf.port;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;

@rdf(Ontology.CORE + "OutputProcessorPort")
public interface OutputProcessorPort extends SenderPort, ProcessorPort {

	public void setDepth(Integer depth);

	@rdf(Ontology.CORE + "depth")
	public Integer getDepth();
	
}
