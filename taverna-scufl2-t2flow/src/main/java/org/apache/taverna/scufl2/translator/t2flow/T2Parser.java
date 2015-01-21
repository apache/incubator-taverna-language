package org.apache.taverna.scufl2.translator.t2flow;

import java.net.URI;
import java.util.List;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public interface T2Parser {
	boolean canHandlePlugin(URI pluginURI);

	URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity);

	Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException;

	List<URI> getAdditionalSchemas();
}
