package uk.org.taverna.scufl2.api.change;

import java.util.ArrayList;
import java.util.List;

public class Change extends Resource {
	public Change(String... resourceUris) {
		for (String resourceUri : resourceUris) {
			relatedResource.add(new Resource(resourceUri));
		}
	}
	Change hasPreviousChange; 
	List<Resource> relatedResource = new ArrayList<Resource>();
}
