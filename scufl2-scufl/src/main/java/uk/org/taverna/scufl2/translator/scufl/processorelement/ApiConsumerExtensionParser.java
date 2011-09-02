/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser;

/**
 * @author alanrw
 *
 */
public class ApiConsumerExtensionParser extends AbstractExtensionParser {

	private static final String APICONSUMER_XSD = "../xsd/scufl-apiconsumer.xsd";

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#canHandle(java.lang.Class)
	 */
	@Override
	public boolean canHandle(Class c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.ApiconsumerType.class);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#getAdditionalSchemas()
	 */
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

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#parseScuflObject(java.lang.Object)
	 */
	@Override
	public void parseScuflObject(Object o) {
		System.err.println(this.getClass() + " is not yet implemented");
	}

}
