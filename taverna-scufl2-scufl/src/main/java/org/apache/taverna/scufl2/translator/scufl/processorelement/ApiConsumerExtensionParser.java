/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author alanrw
 */
public class ApiConsumerExtensionParser extends AbstractExtensionParser {
	private static final String APICONSUMER_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-apiconsumer.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.ApiconsumerType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL apiConsumerXsd = getClass().getResource(APICONSUMER_XSD);
		try {
			return Arrays.asList(apiConsumerXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find APIConsumer schema "
					+ apiConsumerXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
