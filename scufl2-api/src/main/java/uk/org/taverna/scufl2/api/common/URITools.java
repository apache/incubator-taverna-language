package uk.org.taverna.scufl2.api.common;

import java.net.URI;

public class URITools {

	public URI relativeUriForBean(WorkflowBean bean, WorkflowBean relativeToBean) {
		URI rootUri = uriForBean(relativeToBean);
		URI beanUri = uriForBean(bean);
		return rootUri.relativize(beanUri);
	}

	private URI uriForBean(WorkflowBean bean) {

	}

}
