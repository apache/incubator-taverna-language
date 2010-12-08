package uk.org.taverna.scufl2.api.common;

import java.net.URI;

public interface Root extends WorkflowBean {
	public URI getSameBaseAs();

	public void setSameBaseAs(URI sameBaseAs);
}
