/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author alanrw
 */
public class LocalExtensionParser extends AbstractExtensionParser {
	private static final String LOCAL_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-local.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.LocalType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL localXsd = getClass().getResource(LOCAL_XSD);
		try {
			return Arrays.asList(localXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find Local Service schema "
					+ localXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
