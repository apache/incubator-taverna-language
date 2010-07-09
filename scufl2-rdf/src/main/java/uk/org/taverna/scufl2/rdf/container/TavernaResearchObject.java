package uk.org.taverna.scufl2.rdf.container;

import java.util.List;
import java.util.Set;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.activity.Activity;
import uk.org.taverna.scufl2.rdf.bindings.Bindings;
import uk.org.taverna.scufl2.rdf.common.Ontology;
import uk.org.taverna.scufl2.rdf.common.WorkflowBean;
import uk.org.taverna.scufl2.rdf.core.Workflow;

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
