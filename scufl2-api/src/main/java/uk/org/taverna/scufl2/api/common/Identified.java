package uk.org.taverna.scufl2.api.common;

import java.net.URI;

public interface Identified extends WorkflowBean {
	public URI getIdentifier();

	public void setIdentifier(URI identifier);
}
