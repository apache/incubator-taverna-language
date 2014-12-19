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
public class BiomartExtensionParser extends AbstractExtensionParser {
	private static final String BIOMART_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-biomart.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomartType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL biomartXsd = getClass().getResource(BIOMART_XSD);
		try {
			return Arrays.asList(biomartXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find Biomart schema "
					+ biomartXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
