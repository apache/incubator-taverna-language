package uk.org.taverna.scufl2.api.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.annotation.Revisioned;
import uk.org.taverna.scufl2.api.common.AbstractRevisioned;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Ported;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * A <code>Workflow</code> is a set of {@link Processor}s and {@link DataLink}s
 * between the <code>Processor</code>s. <code>Workflow</code>s may also have
 * input and output ports.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 */
public class Workflow extends AbstractRevisioned implements
		Child<WorkflowBundle>, Ported, Revisioned {

	public static final URI WORKFLOW_ROOT = URI
			.create("http://ns.taverna.org.uk/2010/workflow/");

	private final TreeSet<DataLink> dataLinks = new TreeSet<DataLink>();

	private final TreeSet<ControlLink> controlLinks = new TreeSet<ControlLink>();

	private final NamedSet<InputWorkflowPort> inputPorts = new NamedSet<InputWorkflowPort>();
	private final NamedSet<OutputWorkflowPort> outputPorts = new NamedSet<OutputWorkflowPort>();
	private final NamedSet<Processor> processors = new NamedSet<Processor>();
	private WorkflowBundle parent;

	/**
	 * Constructs a <code>Workflow</code> with a name based on a random UUID.
	 */
	public Workflow() {	
		String workflowId = getIdentifierRoot().relativize(getIdentifier())
				.toASCIIString();
		setName("wf-" + workflowId);
	}

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<WorkflowBean> children = new ArrayList<WorkflowBean>();
			children.addAll(getInputPorts());
			children.addAll(getOutputPorts());
			children.addAll(getProcessors());
			children.addAll(getDataLinks());
			children.addAll(getControlLinks());
			children.addAll(Collections.singleton(getCurrentRevision()));			
			for (WorkflowBean bean : children) {
				if (!bean.accept(visitor)) {
					break;
				}
			}
		}
		return visitor.visitLeave(this);
	}

	/**
	 * Returns the <code>ControlLink</code>s.
	 * 
	 * If there are no <code>ControlLink</code>s an empty set is returned.
	 * 
	 * @return the <code>ControlLink</code>s
	 */
	public Set<ControlLink> getControlLinks() {
		return controlLinks;
	}
	
	/**
	 * Returns the <code>DataLink</code>s.
	 * 
	 * If there are no <code>DataLink</code>s an empty set is returned.
	 * 
	 * @return the <code>DataLink</code>s.
	 */
	public Set<DataLink> getDataLinks() {
		return dataLinks;
	}

	/**
	 * Returns the <code>InputWorkflowPort</code>s.
	 * 
	 * If there are no <code>InputWorkflowPort</code>s an empty set is returned.
	 * 
	 * @return the <code>InputWorkflowPort</code>s.
	 */
	@Override
	public NamedSet<InputWorkflowPort> getInputPorts() {
		return inputPorts;
	}

	/**
	 * Returns the <code>OutputWorkflowPort</code>s.
	 * 
	 * If there are no <code>OutputWorkflowPort</code>s an empty set is
	 * returned.
	 * 
	 * @return the <code>OutputWorkflowPort</code>s.
	 */
	@Override
	public NamedSet<OutputWorkflowPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public WorkflowBundle getParent() {
		return parent;
	}

	/**
	 * Returns the <code>Processor</code>s.
	 * 
	 * If there are no <code>Processor</code>s an empty set is returned.
	 * 
	 * @return the <code>Processor</code>s.
	 */
	public NamedSet<Processor> getProcessors() {
		return processors;
	}

	/**
	 * Returns the workflow identifier.
	 *
	 * @deprecated for {@link #getIdentifier()} 
	 * 
	 * @return the workflow identifier
	 */
	@Deprecated
	public URI getWorkflowIdentifier() {
		return getIdentifier();
	}

	/**
	 * Set the <code>ControlLink</code>s to be the contents of the specified
	 * set.
	 * <p>
	 * <code>ControlLink</code>s can be added by using
	 * {@link #getControlLinks()}.add(controlLink).
	 * 
	 * @param controlLinks
	 *            the <code>ControlLink</code>s. <strong>Must not</strong> be
	 *            null
	 */
	public void setControlLinks(Set<ControlLink> controlLinks) {
		this.controlLinks.clear();
		this.controlLinks.addAll(controlLinks);
	}
	
	/**
	 * Set the <code>DataLink</code>s to be the contents of the specified set.
	 * <p>
	 * <code>DataLink</code>s can be added by using {@link #getDataLinks()}
	 * .add(dataLink).
	 * 
	 * @param dataLinks
	 *            the <code>DataLink</code>s. <strong>Must not</strong> be null
	 */
	public void setDataLinks(Set<DataLink> dataLinks) {
		this.dataLinks.clear();
		this.dataLinks.addAll(dataLinks);
	}

	/**
	 * Set the <code>InputWorkflowPort</code>s to be the contents of the
	 * specified set.
	 * <p>
	 * <code>InputWorkflowPort</code>s can be added by using
	 * {@link #getInputWorkflowPorts()}.add(inputPort).
	 * 
	 * @param inputPorts
	 *            the <code>InputWorkflowPort</code>s. <strong>Must not</strong>
	 *            be null
	 */
	public void setInputPorts(Set<InputWorkflowPort> inputPorts) {
		this.inputPorts.clear();
		for (InputWorkflowPort inputPort : inputPorts) {
			inputPort.setParent(this);
		}
	}

	/**
	 * Set the <code>OutputWorkflowPort</code>s to be the contents of the
	 * specified set.
	 * <p>
	 * <code>OutputWorkflowPort</code>s can be added by using
	 * {@link #getOutputWorkflowPorts()}.add(outputPort).
	 * 
	 * @param outputPorts
	 *            the <code>OutputWorkflowPort</code>s. <strong>Must
	 *            not</strong> be null
	 */
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

	/**
	 * Set the <code>Processor</code>s to be the contents of the specified set.
	 * <p>
	 * <code>Processor</code>s can be added by using {@link #getProcessors()}
	 * .add(processor).
	 * 
	 * @param processors
	 *            the <code>Processor</code>s. <strong>Must not</strong> be null
	 */
	public void setProcessors(Set<Processor> processors) {
		this.processors.clear();
		for (Processor processor : processors) {
			processor.setParent(this);
		}
	}

	/**
	 * Set the workflow identifier.
     *
	 * @deprecated for {@link #setIdentifier(URI)}
	 * 
	 * @param workflowIdentifier
	 *            the workflow identifier
	 */
	@Deprecated
	public void setWorkflowIdentifier(URI workflowIdentifier) {
		setIdentifier(workflowIdentifier);
	}

	/**
	 * Updates the workflow identifier.
	 * <p>
	 * The {@link #getCurrentRevision()} will be replaced using using
	 * {@link #newRevision()}.
	 * 
	 */
	public void updateWorkflowIdentifier() {
		newRevision();
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

	@Override
	protected URI getIdentifierRoot() {
		return WORKFLOW_ROOT;
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		Workflow cloneWorkflow = (Workflow)clone;
		cloneWorkflow.setCurrentRevision(cloning.cloneIfNotInCache(getCurrentRevision()));		
	}
	
}
