package net.sf.taverna.scufl2.rdf.core;

import java.util.List;
import java.util.Set;

import net.sf.taverna.scufl2.rdf.common.Named;
import net.sf.taverna.scufl2.rdf.common.Ontology;
import net.sf.taverna.scufl2.rdf.port.InputProcessorPort;
import net.sf.taverna.scufl2.rdf.port.OutputProcessorPort;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "Processor")
public interface Processor extends Named {
	
	public void setOutputPorts(Set<OutputProcessorPort> outputPorts);
	
	@rdf(Ontology.CORE + "hasOutputProcessorPort")
	public Set<OutputProcessorPort> getOutputPorts();

	public void setInputPorts(Set<InputProcessorPort> inputPorts);
	
	@rdf(Ontology.CORE + "hasInputProcessorPort")
	public Set<InputProcessorPort> getInputPorts();

	public void setIterationStrategyStack(List<IterationStrategy> iterationStrategyStack);

	@rdf(Ontology.CORE + "iterationStrategyStack")
	public List<IterationStrategy> getIterationStrategyStack() ;

	public void setProcessorType(ProcessorType processorType);
	
	@rdf(Ontology.CORE + "isProcessorType")
	public ProcessorType getProcessorType();
}
