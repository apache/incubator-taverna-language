/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.api.common.WorkflowBean;

/**
 * @author alanrw
 *
 */
public interface ScuflExtensionParser {
	
	void setParserState(ParserState state);
	
	ParserState getParserState();

	List<URI> getAdditionalSchemas();

	void parseScuflObject(Object o);

	boolean canHandle(Class c);

}
