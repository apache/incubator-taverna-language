package uk.org.taverna.scufl2.rdf.port;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;

@rdf(Ontology.CORE + "ReceivingPort")
public interface ReceiverPort extends Port {
	public void setDepth(Integer depth);

	@rdf(Ontology.CORE + "depth")
	public Integer getDepth();
}
