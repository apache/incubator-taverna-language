package uk.org.taverna.scufl2.api.change;

import java.net.URI;

public class Resource {
	public Resource() {		
	}
	public Resource(String rdfAboutUri) {
		rdfAbout = URI.create(rdfAboutUri);
	}
	URI rdfAbout;
	URI rdfType;
}
