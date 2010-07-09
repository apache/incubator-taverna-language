package uk.org.taverna.scufl2.rdf.core;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Ontology;
import uk.org.taverna.scufl2.rdf.common.WorkflowBean;
import uk.org.taverna.scufl2.rdf.port.ReceiverPort;
import uk.org.taverna.scufl2.rdf.port.SenderPort;

@rdf(Ontology.CORE + "DataLink")
public interface DataLink extends WorkflowBean {

	public void setSenderPort(SenderPort fromPort);
	public void setReceiverPort(ReceiverPort toPort);
	
	@rdf(Ontology.CORE + "receiver")
	public ReceiverPort getReceiverPort();

	@rdf(Ontology.CORE + "sender")
	public SenderPort getSenderPort();
	
}
