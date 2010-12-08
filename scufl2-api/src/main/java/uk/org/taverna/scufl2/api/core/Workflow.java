package uk.org.taverna.scufl2.api.core;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;


/**
 * @author Alan R Williams
 *
 */
public class Workflow extends AbstractNamed implements Configurable,
		Child<WorkflowBundle> {

	private static URI WORKFLOW_ROOT = URI
	.create("http://ns.taverna.org.uk/2010/workflow/");

	public static URI generateIdentifier() {
		return WORKFLOW_ROOT.resolve(UUID.randomUUID().toString());
	}

	private Set<DataLink> datalinks = new HashSet<DataLink>();
	private NamedSet<InputWorkflowPort> inputPorts = new NamedSet<InputWorkflowPort>();
	private NamedSet<OutputWorkflowPort> outputPorts = new NamedSet<OutputWorkflowPort>();
	private NamedSet<Processor> processors = new NamedSet<Processor>();
	private URI workflowIdentifier;
	private WorkflowBundle parent;

	public Workflow() {
		setWorkflowIdentifier(generateIdentifier());
		String workflowId = WORKFLOW_ROOT.relativize(getWorkflowIdentifier())
		.toASCIIString();
		setName("wf-" + workflowId);
	}

	public Set<DataLink> getDatalinks() {
		return datalinks;
	}


	public NamedSet<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}


	public NamedSet<OutputWorkflowPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public WorkflowBundle getParent() {
		return parent;
	}


	public NamedSet<Processor> getProcessors() {
		return processors;
	}

	public URI getWorkflowIdentifier() {
		return workflowIdentifier;
	}


	public void setDatalinks(Set<DataLink> datalinks) {
		this.datalinks = datalinks;
	}


	public void setInputPorts(Set<InputWorkflowPort> inputPorts) {
		this.inputPorts.clear();
		for (InputWorkflowPort inputPort : inputPorts) {
			inputPort.setParent(this);
		}
	}


	public void setOutputPorts(Set<OutputWorkflowPort> outputPorts) {
		this.outputPorts.clear();
		for (OutputWorkflowPort outputPort : outputPorts) {
			outputPort.setParent(this);
		}
	}


	@Override
	public void setParent(WorkflowBundle parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getWorkflows().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getWorkflows().add(this);
		}

	}

	public void setProcessors(Set<Processor> processors) {
		this.processors.clear();
		for (Processor processor : processors) {
			processor.setParent(this);
		}
	}

	public void setWorkflowIdentifier(URI workflowIdentifier) {
		this.workflowIdentifier = workflowIdentifier;
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
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}