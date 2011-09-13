/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser;
import uk.org.taverna.scufl2.xml.scufl.jaxb.LocalType;

/**
 * @author alanrw
 *
 */
public class LocalExtensionParser extends AbstractExtensionParser {

	private static final String LOCAL_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-local.xsd";

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#canHandle(java.lang.Class)
	 */
	@Override
	public boolean canHandle(Class c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.LocalType.class);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#getAdditionalSchemas()
	 */
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

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#parseScuflObject(java.lang.Object)
	 */
	@Override
	public void parseScuflObject(Object o) {
		System.err.println(this.getClass() + " is not yet implemented");
	}

}
