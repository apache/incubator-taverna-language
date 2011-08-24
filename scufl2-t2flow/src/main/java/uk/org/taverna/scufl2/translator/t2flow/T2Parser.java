package uk.org.taverna.scufl2.translator.t2flow;

import java.net.URI;
import java.util.List;

import javax.xml.transform.Source;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public interface T2Parser {

	boolean canHandlePlugin(URI pluginURI);

	URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity);

	Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException;

	public void setParserState(ParserState parserState);

	public List<URI> getAdditionalSchemas();
}
