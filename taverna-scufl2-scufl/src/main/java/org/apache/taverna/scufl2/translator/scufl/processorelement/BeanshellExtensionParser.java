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
public class BeanshellExtensionParser extends AbstractExtensionParser {
	private static final String BEANSHELL_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-beanshell.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(org.apache.taverna.scufl2.xml.scufl.jaxb.BeanshellType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL beanshellXsd = getClass().getResource(BEANSHELL_XSD);
		try {
			return Arrays.asList(beanshellXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find Beanshell schema "
					+ beanshellXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
