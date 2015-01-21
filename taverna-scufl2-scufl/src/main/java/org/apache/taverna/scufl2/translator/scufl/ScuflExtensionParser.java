/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl;

import java.net.URI;
import java.util.List;

/**
 * @author alanrw
 */
public interface ScuflExtensionParser {
	void setParserState(ParserState state);
	
	ParserState getParserState();

	List<URI> getAdditionalSchemas();

	void parseScuflObject(Object o);

	boolean canHandle(Class<?> c);
}
