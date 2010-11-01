package uk.org.taverna.scufl2.api.common;

import java.net.URI;

public interface Typed extends WorkflowBean {
	public URI getType();

	public void setType(URI type);
}
