/**
 * 
 */
package uk.org.taverna.scufl2.translator.scufl.processorelement;

import uk.org.taverna.scufl2.translator.scufl.ParserState;
import uk.org.taverna.scufl2.translator.scufl.ScuflExtensionParser;

/**
 * @author alanrw
 */
public abstract class AbstractExtensionParser implements ScuflExtensionParser {
	private ParserState parserState;

	/**
	 * @return the parserState
	 */
	@Override
	public ParserState getParserState() {
		return parserState;
	}

	/**
	 * @param parserState the parserState to set
	 */
	@Override
	public void setParserState(ParserState parserState) {
		this.parserState = parserState;
	}
}
