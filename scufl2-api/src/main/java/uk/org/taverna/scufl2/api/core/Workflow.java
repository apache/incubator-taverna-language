package uk.org.taverna.scufl2.api.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * @author Alan R Williams
 *
 */
public class Workflow extends AbstractNamedChild implements
		Child<WorkflowBundle>, Ported {

	public static final URI WORKFLOW_ROOT = URI
			.create("http://ns.taverna.org.uk/2010/workflow/");

	public static URI generateIdentifier() {
		return WORKFLOW_ROOT.resolve(UUID.randomUUID().toString() + "/");
	}

	private Set<DataLink> dataLinks = new HashSet<DataLink>();

	private Set<ControlLink> controlLinks = new HashSet<ControlLink>();

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

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getInputPorts());
			children.add(getOutputPorts());
			children.add(getProcessors());
			children.add(getDataLinks());
			children.add(getControlLinks());
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
		}
		return visitor.visitLeave(this);
	}

	public Set<ControlLink> getControlLinks() {
		return controlLinks;
	}

	public Set<DataLink> getDataLinks() {
		return dataLinks;
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

	public void setControlLinks(Set<ControlLink> controlLinks) {
		this.controlLinks = controlLinks;
	}

	public void setDataLinks(Set<DataLink> datalinks) {
		dataLinks = datalinks;
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
				+ (getDataLinks() != null ? toString(getDataLinks(), maxLen)
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