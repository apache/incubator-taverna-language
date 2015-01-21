/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * @author alanrw
 */
public class AbstractProcessorExtensionParser extends AbstractExtensionParser {
	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(org.apache.taverna.scufl2.xml.scufl.jaxb.AbstractprocessorType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		return Collections.emptyList();
	}

	@Override
	public void parseScuflObject(Object o) {
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
