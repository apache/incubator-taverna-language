package net.sf.taverna.scufl2.rdf.port;

import net.sf.taverna.scufl2.rdf.common.Ontology;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "ReceivingPort")
public interface ReceiverPort extends Port {
	public void setDepth(Integer depth);

	@rdf(Ontology.CORE + "depth")
	public Integer getDepth();
}
