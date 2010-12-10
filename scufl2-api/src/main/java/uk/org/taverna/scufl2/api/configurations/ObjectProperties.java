package uk.org.taverna.scufl2.api.configurations;

import java.net.URI;
import java.util.List;

public interface ObjectProperties {
	public URI getObjectClass();

	public List<Property> getObjectProperties();

	public void setObjectClass(URI objectClass);

	public void setObjectProperties(List<Property> properties);
}
