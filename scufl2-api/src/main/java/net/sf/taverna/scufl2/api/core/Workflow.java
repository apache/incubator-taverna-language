package net.sf.taverna.scufl2.api.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.port.InputWorkflowPort;
import net.sf.taverna.scufl2.api.port.OutputWorkflowPort;

public class Workflow extends AbstractNamed {

	public Workflow() {
		super("wf-" + UUID.randomUUID().toString());
	}

	private Set<InputWorkflowPort> inputPorts = new HashSet<InputWorkflowPort>();
	private Set<DataLink> datalinks = new HashSet<DataLink>();
	private Set<Processor> processors = new HashSet<Processor>();
	private Set<OutputWorkflowPort> outputPorts = new HashSet<OutputWorkflowPort>();

	public Set<OutputWorkflowPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public String toString() {
		final int maxLen = 6;
		return "Workflow [getName()="
				+ getName()
				+ ", getDatalinks()="
				+ (getDatalinks() != null ? toString(getDatalinks(), maxLen)
						: null)
				+ ", getInputPorts()="
				+ (getInputPorts() != null ? toString(getInputPorts(), maxLen)
						: null)
				+ ", getOutputPorts()="
				+ (getOutputPorts() != null ? toString(getOutputPorts(), maxLen)
						: null)
				+ ", getProcessors()="
				+ (getProcessors() != null ? toString(getProcessors(), maxLen)
						: null) + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	public void setOutputPorts(Set<OutputWorkflowPort> outputPorts) {
		this.outputPorts = outputPorts;
	}

	public Set<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}

	public void setInputPorts(Set<InputWorkflowPort> inputPorts) {
		this.inputPorts = inputPorts;
	}

	public void setProcessors(Set<Processor> processors) {
		this.processors = processors;
	}

	public Set<Processor> getProcessors() {
		return processors;
	}

	public void setDatalinks(Set<DataLink> datalinks) {
		this.datalinks = datalinks;
	}

	public Set<DataLink> getDatalinks() {
		return datalinks;
	}

	public InputWorkflowPort addInputPort(String portName) {
		InputWorkflowPort port = new InputWorkflowPort(this, portName);
		getInputPorts().add(port);
		return port;
	}

	public OutputWorkflowPort addOutputPort(String portName) {
		OutputWorkflowPort port = new OutputWorkflowPort(this, portName);
		getOutputPorts().add(port);
		return port;
	}

	public Processor addProcessor(String processorName) {
		Processor proc = new Processor(this, processorName);
		getProcessors().add(proc);
		return proc;
	}

}