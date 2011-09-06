/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser;

/**
 * @author alanrw
 *
 */
public class AbstractProcessorExtensionParser extends AbstractExtensionParser {

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#canHandle(java.lang.Class)
	 */
	@Override
	public boolean canHandle(Class c) {
		return c.equals(uk.org.taverna.scufl2.xml.scufl.jaxb.AbstractprocessorType.class);
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#getAdditionalSchemas()
	 */
	@Override
	public List<URI> getAdditionalSchemas() {
		return Collections.EMPTY_LIST;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser#parseScuflObject(java.lang.Object)
	 */
	@Override
	public void parseScuflObject(Object o) {
		System.err.println(this.getClass() + " is not yet implemented");
	}

}
