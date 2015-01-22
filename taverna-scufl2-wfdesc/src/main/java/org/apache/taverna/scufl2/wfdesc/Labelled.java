package org.apache.taverna.scufl2.wfdesc;

import java.util.Set;

import org.openrdf.elmo.annotations.rdf;

@rdf("http://purl.org/wf4ever/wfdesc#Description")
public interface Labelled {
	
	@rdf("http://www.w3.org/2000/01/rdf-schema#label")
	Set<String> getLabel();
	void setLabel(Set<String> label);
}

