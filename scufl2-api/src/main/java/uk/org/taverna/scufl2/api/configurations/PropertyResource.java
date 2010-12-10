package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class PropertyResource implements PropertyObject {
	private URI resourceURI;
	private URI typeURI;
	private final Map<URI, Set<PropertyObject>> properties = new LazyMap<URI, Set<PropertyObject>>() {
		private static final long serialVersionUID = 1L;

		@Override
		public Set<PropertyObject> getDefault(URI key) {
			return new LinkedHashSet<PropertyObject>();
		}
	};


	public final Map<URI, Set<PropertyObject>> getProperties() {
		return properties;
	}

	public final URI getResourceURI() {
		return resourceURI;
	}

	public final URI getTypeURI() {
		return typeURI;
	}

	public final void setProperties(Map<URI, Set<PropertyObject>> properties) {
		this.properties.clear();
		this.properties.putAll(properties);
	}

	public final void setResourceURI(URI resourceURI) {
		this.resourceURI = resourceURI;
	}

	public final void setTypeURI(URI typeURI) {
		this.typeURI = typeURI;
	}

}
