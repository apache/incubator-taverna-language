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
public class WsdlExtensionParser extends AbstractExtensionParser {
	private static final String WSDL_XSD = "/uk/org/taverna/scufl2/translator/scufl/xsd/scufl-wsdl.xsd";

	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.WsdlType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL wsdlXsd = getClass().getResource(WSDL_XSD);
		try {
			return Arrays.asList(wsdlXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find WSDL schema " + wsdlXsd);
		}
	}

	@Override
	public void parseScuflObject(Object o) {
		// TODO write to log?
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
