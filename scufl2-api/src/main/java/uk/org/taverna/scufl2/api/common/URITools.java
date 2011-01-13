package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.net.URISyntaxException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class URITools {

	public URI relativePath(URI base, URI uri) {
		if (! base.resolve("/").equals(uri.resolve("/"))) {
			// Different protocol/host/auth
			return uri;
		}
		URI relation = URI.create(".");
		URI candidate = base.relativize(uri);
		while (candidate.isAbsolute() &&
				! (base.getPath().isEmpty() || base.getPath().equals("/"))) {
			base = base.resolve("../");
			relation = relation.resolve("../");
			candidate = base.relativize(uri);
		}
		// Add the ../.. again
		URI resolved = relation.resolve(candidate);
		return resolved;

	}

	public URI relativeUriForBean(WorkflowBean bean, WorkflowBean relativeToBean) {
		URI rootUri = uriForBean(relativeToBean);
		URI beanUri = uriForBean(bean);
		return relativePath(rootUri, beanUri);
	}

	public URI uriForBean(WorkflowBean bean) {
		if (bean == null) {
			throw new NullPointerException("Bean can't be null");
		}
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
			if (parent == null) {
				throw new IllegalStateException("Bean does not have a parent: "
						+ child);
			}
			URI parentUri = uriForBean(parent);

			if (!parentUri.getPath().endsWith("/")) {
				parentUri = parentUri.resolve(parentUri.getPath() + "/");
			}
			// TODO: Get relation by container
			String relation = child.getClass().getSimpleName() + "/";
			URI relationUri = parentUri.resolve(relation.toLowerCase());
			if (bean instanceof Named) {
				Named named = (Named) bean;
				String name = validFilename(named.getName());
				return relationUri.resolve(name + "/");
			} else {
				throw new IllegalStateException(
						"Can't create URIs for non-named child: " + bean);
			}

		}
		throw new IllegalArgumentException("Unsupported type "
				+ bean.getClass() + " for bean " + bean);
	}

	public String validFilename(String name) {

		// Make a relative URI
		URI uri;
		try {
			uri = new URI(null, null, name, null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid name " + name);
		}
		String ascii = uri.toASCIIString();
		// And escape / and \
		String escaped = ascii.replace("/", "%2f");
		escaped = escaped.replace("\\", "%5c");
		escaped = escaped.replace(":", "%3a");
		return escaped;
	}

}
