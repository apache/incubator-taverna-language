package uk.org.taverna.scufl2.api.change;

public class Removal extends Change {
	public Removal(String... removedResourceUris) {
		super(removedResourceUris);
	}
}
