package net.sf.taverna.scufl2.rdf.common;

import org.openrdf.elmo.Entity;
import org.openrdf.elmo.annotations.rdf;
import org.openrdf.elmo.sesame.roles.SesameEntity;

/**
 * Upper ontology 
 *
 */
@rdf(Ontology.WorkflowBean)
public interface WorkflowBean extends SesameEntity, Entity {

}
