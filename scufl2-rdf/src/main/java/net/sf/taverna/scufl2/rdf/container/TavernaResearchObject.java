package net.sf.taverna.scufl2.rdf.container;

import java.util.List;
import java.util.Set;

import net.sf.taverna.scufl2.rdf.activity.Activity;
import net.sf.taverna.scufl2.rdf.bindings.Bindings;
import net.sf.taverna.scufl2.rdf.common.Ontology;
import net.sf.taverna.scufl2.rdf.common.WorkflowBean;
import net.sf.taverna.scufl2.rdf.core.Workflow;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.TavernaResearchObject)
public interface TavernaResearchObject extends ResearchObject, WorkflowBean {

	@rdf(Ontology.hasBinding)
	public Set<Bindings> getBindings();


	@rdf(Ontology.mainWorkflow)
	public Workflow getMainWorkflow();
	
	public void setBindings(List<Bindings> bindings);

	public void setMainWorkflow(Workflow mainWorkflow);
	
	@rdf(Ontology.hasActivity)
	public Set<Activity> getActivities();
	
	public void setActivities(Set<Activity> activities);
	

}
