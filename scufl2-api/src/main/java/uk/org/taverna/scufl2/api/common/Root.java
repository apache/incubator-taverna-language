package uk.org.taverna.scufl2.api.common;

import java.net.URI;

public interface Root extends WorkflowBean {
	public URI getGlobalBaseURI();

	public void setGlobalBaseURI(URI globalBaseURI);
}
