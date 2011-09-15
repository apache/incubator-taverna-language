package uk.org.taverna.scufl2.translator.t2flow.defaultdispatchstack;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public class ErrorBounceParser extends AbstractActivityParser {

	private static URI ravenURI =
		T2FlowParser.ravenURI.resolve("net.sf.taverna.t2.core/workflowmodel-impl/");

	private static String className = "net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.ErrorBounce";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/ErrorBounce");

	@Override
	public boolean canHandlePlugin(URI pluginURI) {
		String uriStr = pluginURI.toASCIIString();
		return uriStr.startsWith(ravenURI.toASCIIString())
				&& uriStr.endsWith(className);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {
		return null; // no config!
	}

}
