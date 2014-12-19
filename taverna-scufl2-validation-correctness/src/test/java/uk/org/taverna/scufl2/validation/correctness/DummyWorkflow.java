/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import uk.org.taverna.scufl2.api.annotation.Revision;
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
	private Revision currentRevision = null;
	@Override
	public Revision getCurrentRevision() {
		return currentRevision;
	}

	@Override
	public void setCurrentRevision(Revision currentRevision) {
		this.currentRevision = currentRevision;
	}

	private WorkflowBundle dummyParent = new WorkflowBundle();
	
	private String name2;

	public DummyWorkflow() {
		
	}

	public DummyWorkflow(WorkflowBundle parent) {
		super.setParent(parent);
	}
	
	@Override
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
	@Override
	public NamedSet<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * @return the outputPorts
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
	public NamedSet<Processor> getProcessors() {
		return processors;
	}

	/**
	 * @param processors the processors to set
	 */
	public void setProcessors(NamedSet<Processor> processors) {
		this.processors = processors;
	}


}