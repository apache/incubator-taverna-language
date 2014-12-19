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
public class BiomobyExtensionParser extends AbstractExtensionParser {

	private static final String BIOMOBY_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-biomoby.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomobyobjectType.class)
				|| c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomobyparserType.class)
				|| c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.BiomobywsdlType.class);
	}

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

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
