package net.sf.taverna.scufl2.api.bindings;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.common.WorkflowBean;

/**
 * A Bindings specifies a set of compatible ProcessorBindings. For example, one
 * Bindings could contain ways of enacting a set of Processors on a grid whilst
 * another contained ways of enacting the Processors on a laptop.
 * 
 * A given Bindings does not need to specify bindings for all the Processors
 * within a workflow, nor is it limited to giving bindings for Processors within
 * a single Workflow. One or more Bindings may be used to help run a Workflow
 * and conversely the same Bindings may be used to run more than one Workflow.
 * 
 * @author alanrw
 * 
 */
@XmlType (propOrder = {"processorBindings"})
public class Bindings extends AbstractNamed implements WorkflowBean {

	private Set<ProcessorBinding> processorBindings = new HashSet<ProcessorBinding>();

	/**
	 * Return the set of bindings for individual Processors.
	 * 
	 * @return
	 */
	@XmlElementWrapper( name="processorBindings",nillable=false,required=true)
	@XmlElement( name="processorBinding",nillable=false)
	public Set<ProcessorBinding> getProcessorBindings() {
		return processorBindings;
	}

	/**
	 * @param processorBindings
	 */
	public void setProcessorBindings(Set<ProcessorBinding> processorBindings) {
		this.processorBindings = processorBindings;
	}

}
