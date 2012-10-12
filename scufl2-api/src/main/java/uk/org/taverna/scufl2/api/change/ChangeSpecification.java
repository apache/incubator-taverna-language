package uk.org.taverna.scufl2.api.change;

import java.util.List;

public class ChangeSpecification extends Resource {
	public ChangeSpecification() {		
	}
	public ChangeSpecification(VersionableResource toVersion) {
		this.toVersion = toVersion;
	}
	VersionableResource fromVersion;
	VersionableResource toVersion;
	List<Change> change;
}
