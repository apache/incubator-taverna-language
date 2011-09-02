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
public class BiomobyExtensionParser extends AbstractExtensionParser {

	private static final String BIOMOBY_XSD = "../xsd/scufl-biomoby.xsd";

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#canHandle(java.lang.Class)
	 */
	@Override
	public boolean canHandle(Class c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomobyobjectType.class) || 
		c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomobyparserType.class) || 
		c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomobywsdlType.class);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#getAdditionalSchemas()
	 */
	@Override
	public List<URI> getAdditionalSchemas() {
		URL apiConsumerXsd = getClass().getResource(BIOMOBY_XSD);
		try {
			return Arrays.asList(apiConsumerXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find APIConsumer schema "
					+ apiConsumerXsd);
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
