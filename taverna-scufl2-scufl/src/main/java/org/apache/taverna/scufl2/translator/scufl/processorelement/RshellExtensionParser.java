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
public class RshellExtensionParser extends AbstractExtensionParser {
	private static final String RSHELL_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-rshell.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.RshellType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL rshellXsd = getClass().getResource(RSHELL_XSD);
		try {
			return Arrays.asList(rshellXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find RShell schema "
					+ rshellXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
