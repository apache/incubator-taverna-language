package uk.org.taverna.scufl2.api.common;

import java.net.URI;

public interface Root extends WorkflowBean {
	URI getGlobalBaseURI();

	void setGlobalBaseURI(URI globalBaseURI);
}
