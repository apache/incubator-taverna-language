/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser;

/**
 * @author alanrw
 *
 */
public class BeanshellExtensionParser extends AbstractExtensionParser {

	private static final String BEANSHELL_XSD = "../xsd/scufl-beanshell.xsd";

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#canHandle(java.lang.Class)
	 */
	@Override
	public boolean canHandle(Class c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BeanshellType.class);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#getAdditionalSchemas()
	 */
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

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#parseScuflObject(java.lang.Object)
	 */
	@Override
	public void parseScuflObject(Object o) {
		System.err.println(this.getClass() + " is not yet implemented");
	}

}
