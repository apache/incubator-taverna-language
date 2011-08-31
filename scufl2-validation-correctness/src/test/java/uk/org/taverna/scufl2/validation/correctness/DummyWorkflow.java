/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

class DummyWorkflow extends Workflow {

	private TreeSet<DataLink> dataLinks = null;

	private TreeSet<ControlLink> controlLinks = null;
	private NamedSet<InputWorkflowPort> inputPorts = null;
	private NamedSet<OutputWorkflowPort> outputPorts = null;
	private NamedSet<Processor> processors = null;
	private URI workflowIdentifier = null;

	private WorkflowBundle dummyParent = new WorkflowBundle();
	
	private String name2;

	public DummyWorkflow() {
		
	}

	public DummyWorkflow(WorkflowBundle parent) {
		super.setParent(parent);
	}
	
	public WorkflowBundle getParent() {
		return dummyParent;
	}

	@Override
	public String getName() {
		return name2;
	}

	@Override
	public void setName(String name) {
		name2 = name;
	}
		/**
	 * @return the inputPorts
	 */
	public NamedSet<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * @return the outputPorts
	 */
	public NamedSet<OutputWorkflowPort> getOutputPorts() {
		return outputPorts;
	}

	/**
	 * @param inputPorts the inputPorts to set
	 */
	public void setInputPorts(NamedSet<InputWorkflowPort> inputPorts) {
		this.inputPorts = inputPorts;
	}

	/**
	 * @param outputPorts the outputPorts to set
	 */
	public void setOutputPorts(NamedSet<OutputWorkflowPort> outputPorts) {
		this.outputPorts = outputPorts;
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			if (getInputPorts() != null) {
				children.add(getInputPorts());
			}
			if (getOutputPorts() != null) {
				children.add(getOutputPorts());
			}
			if (getProcessors() != null) {
				children.add(getProcessors());
			}
			if (getDataLinks() != null) {
				children.add(getDataLinks());
			}
			if (getControlLinks() != null) {
				children.add(getControlLinks());
			}
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

	/**
	 * @return the dataLinks
	 */
	public TreeSet<DataLink> getDataLinks() {
		return dataLinks;
	}

	/**
	 * @param dataLinks the dataLinks to set
	 */
	public void setDataLinks(TreeSet<DataLink> dataLinks) {
		this.dataLinks = dataLinks;
	}

	/**
	 * @return the controlLinks
	 */
	public TreeSet<ControlLink> getControlLinks() {
		return controlLinks;
	}

	/**
	 * @param controlLinks the controlLinks to set
	 */
	public void setControlLinks(TreeSet<ControlLink> controlLinks) {
		this.controlLinks = controlLinks;
	}

	/**
	 * @return the processors
	 */
	public NamedSet<Processor> getProcessors() {
		return processors;
	}

	/**
	 * @param processors the processors to set
	 */
	public void setProcessors(NamedSet<Processor> processors) {
		this.processors = processors;
	}

	/**
	 * @return the workflowIdentifier
	 */
	public URI getWorkflowIdentifier() {
		return workflowIdentifier;
	}

	/**
	 * @param workflowIdentifier the workflowIdentifier to set
	 */
	public void setWorkflowIdentifier(URI workflowIdentifier) {
		this.workflowIdentifier = workflowIdentifier;
	}

}