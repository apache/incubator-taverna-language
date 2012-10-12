package uk.org.taverna.scufl2.api.change;

import java.util.Calendar;


public class VersionableResource extends Resource {	
	public VersionableResource(String uriStr) {
		super(uriStr);
	}
	ChangeSpecification wasChangedBy = new ChangeSpecification(this);
	Calendar generatedAtTime;
	VersionableResource wasRevisionOf;
}
