package uk.org.taverna.scufl2.rdf.core;

import java.util.Set;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Named;
import uk.org.taverna.scufl2.rdf.common.Ontology;
import uk.org.taverna.scufl2.rdf.port.InputWorkflowPort;
import uk.org.taverna.scufl2.rdf.port.OutputWorkflowPort;

public interface Workflow extends Named {

	@rdf(Ontology.CORE + "hasOutputWorkflowPort")
	public abstract Set<OutputWorkflowPort> getOutputPorts();

	public abstract void setOutputPorts(Set<OutputWorkflowPort> outputPorts);

	@rdf(Ontology.CORE + "hasInputWorkflowPort")
	public abstract Set<InputWorkflowPort> getInputPorts();

	public abstract void setInputPorts(Set<InputWorkflowPort> inputPorts);

	public abstract void setProcessors(Set<Processor> processors);

	@rdf(Ontology.CORE + "hasProcessor")
	public abstract Set<Processor> getProcessors();

	public abstract void setDatalinks(Set<DataLink> datalinks);

	@rdf(Ontology.CORE + "hasDataLink")
	public abstract Set<DataLink> getDatalinks();

}