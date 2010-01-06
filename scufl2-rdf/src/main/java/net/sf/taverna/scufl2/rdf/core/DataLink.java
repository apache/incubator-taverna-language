package net.sf.taverna.scufl2.rdf.core;

import net.sf.taverna.scufl2.rdf.common.Ontology;
import net.sf.taverna.scufl2.rdf.common.WorkflowBean;
import net.sf.taverna.scufl2.rdf.port.ReceiverPort;
import net.sf.taverna.scufl2.rdf.port.SenderPort;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "DataLink")
public interface DataLink extends WorkflowBean {

	public void setSenderPort(SenderPort fromPort);
	public void setReceiverPort(ReceiverPort toPort);
	
	@rdf(Ontology.CORE + "receiver")
	public ReceiverPort getReceiverPort();

	@rdf(Ontology.CORE + "sender")
	public SenderPort getSenderPort();
	
}
