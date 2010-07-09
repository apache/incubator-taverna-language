package uk.org.taverna.scufl2.rdf.activity;


import org.openrdf.elmo.annotations.rdf;

import uk.org.taverna.scufl2.rdf.common.Named;
import uk.org.taverna.scufl2.rdf.common.Ontology;


@rdf(Ontology.CORE + "Activity")
public interface Activity extends Named {

	@rdf(Ontology.CORE + "activityType")
	public ActivityType getType() ;

	public void setType(ActivityType type);

}
