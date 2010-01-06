package net.sf.taverna.scufl2.rdf.activity;

import net.sf.taverna.scufl2.rdf.common.Named;
import net.sf.taverna.scufl2.rdf.common.Ontology;

import org.openrdf.elmo.annotations.rdf;


@rdf(Ontology.CORE + "Activity")
public interface Activity extends Named {

	@rdf(Ontology.CORE + "activityType")
	public ActivityType getType() ;

	public void setType(ActivityType type);

}
