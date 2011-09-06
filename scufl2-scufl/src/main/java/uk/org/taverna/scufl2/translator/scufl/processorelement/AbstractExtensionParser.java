/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.translator.scufl.ParserState;
import uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser;

/**
 * @author alanrw
 *
 */
public abstract class AbstractExtensionParser implements ScuflExtensionParser {
	
	private ParserState parserState;

	/**
	 * @return the parserState
	 */
	public ParserState getParserState() {
		return parserState;
	}

	/**
	 * @param parserState the parserState to set
	 */
	public void setParserState(ParserState parserState) {
		this.parserState = parserState;
	}

}
