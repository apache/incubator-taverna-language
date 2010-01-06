package net.sf.taverna.scufl2.rdf.port;

import net.sf.taverna.scufl2.rdf.common.Ontology;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "OutputProcessorPort")
public interface OutputProcessorPort extends SenderPort, ProcessorPort {

	public void setDepth(Integer depth);

	@rdf(Ontology.CORE + "depth")
	public Integer getDepth();
	
}
