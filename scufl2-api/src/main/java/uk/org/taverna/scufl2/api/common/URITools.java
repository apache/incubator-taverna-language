package uk.org.taverna.scufl2.api.common;

import java.net.URI;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class URITools {

	public URI relativeUriForBean(WorkflowBean bean, WorkflowBean relativeToBean) {
		URI rootUri = uriForBean(relativeToBean);
		URI beanUri = uriForBean(bean);
		return rootUri.relativize(beanUri);
	}

	private URI uriForBean(WorkflowBean bean) {
		if (bean instanceof Root) {
			Root root = (Root) bean;
			if (root.getSameBaseAs() == null) {
				if (root instanceof WorkflowBundle) {
					root.setSameBaseAs(WorkflowBundle.generateIdentifier());
				} else {
					throw new IllegalArgumentException(
							"sameBaseAs is null for bean " + bean);
				}
			}
			return root.getSameBaseAs();
		}
		if (bean instanceof Child) {
			Child child = (Child) bean;
			WorkflowBean parent = child.getParent();
			URI parentUri = uriForBean(parent);

			if (!parentUri.getPath().endsWith("/")) {
				parentUri = parentUri.resolve(parentUri.getPath() + "/");
			}
			String relation = child.getClass().getSimpleName() + "/";
			return parentUri.resolve(relation);
		}
		throw new IllegalArgumentException("Unsupported type "
				+ bean.getClass() + " for bean " + bean);
	}
}
