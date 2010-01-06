package net.sf.taverna.scufl2.api.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.port.InputProcessorPort;
import net.sf.taverna.scufl2.api.port.OutputProcessorPort;

public class Processor extends AbstractNamed {

	private Set<OutputProcessorPort> outputPorts = new HashSet<OutputProcessorPort>();
	private Set<InputProcessorPort> inputPorts = new HashSet<InputProcessorPort>();
	private List<IterationStrategy> iterationStrategyStack = new ArrayList<IterationStrategy>();
	private ProcessorType processorType;

	public Processor(Workflow parent, String name) {
		super(name);
		setParent(parent);
	}

	public void setOutputPorts(Set<OutputProcessorPort> outputPorts) {
		this.outputPorts.clear();
		this.outputPorts.addAll(outputPorts);
	}

	public Set<OutputProcessorPort> getOutputPorts() {
		return outputPorts;
	}

	public void setInputPorts(Set<InputProcessorPort> inputPorts) {
		this.inputPorts = inputPorts;
	}

	public Set<InputProcessorPort> getInputPorts() {
		return inputPorts;
	}

	public void setIterationStrategyStack(
			List<IterationStrategy> iterationStrategyStack) {
		this.iterationStrategyStack = iterationStrategyStack;
	}

	public List<IterationStrategy> getIterationStrategyStack() {
		return iterationStrategyStack;
	}

	public void setProcessorType(ProcessorType processorType) {
		this.processorType = processorType;
	}

	public ProcessorType getProcessorType() {
		return processorType;
	}

	private Workflow parent;

	public void setParent(Workflow parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getProcessors().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getProcessors().add(this);
		}
	}
	
	public Workflow getParent() {
		return parent;
	}

	public InputProcessorPort addInputPort(String portName) {
		InputProcessorPort port = new InputProcessorPort(this, portName);
		getInputPorts().add(port);
		return port;
	}
	
	
	public OutputProcessorPort addOutputPort(String portName) {
		OutputProcessorPort port = new OutputProcessorPort(this, portName);
		getOutputPorts().add(port);
		return port;
	}
	
	
	
	
}

