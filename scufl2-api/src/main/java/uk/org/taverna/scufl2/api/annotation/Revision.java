package uk.org.taverna.scufl2.api.annotation;

import java.net.URI;
import java.util.Calendar;
import java.util.Set;

public class Revision {
	
	URI identifier;
	Set<URI> creators;
	Calendar created;
	Revision previousRevision;
	
	Set<URI> additionOf;
	Set<URI> removalOf;
	Set<URI> modificationsOf;
	
	

}
