package uk.org.taverna.scufl2.translator.t2flow.defaultdispatchstack;

import java.math.BigInteger;
import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RetryConfig;

public class RetryParser extends AbstractActivityParser {

	private static URI ravenURI =
		T2FlowParser.ravenURI.resolve("net.sf.taverna.t2.core/workflowmodel-impl/");

	private static String className = "net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Retry";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Retry");

	@Override
	public boolean canHandlePlugin(URI pluginURI) {
		String uriStr = pluginURI.toASCIIString();
		return uriStr.startsWith(ravenURI.toASCIIString())
				&& uriStr.endsWith(className);
	}

	/**
	 * Default values - ignored for now
	 */
	public static final class Defaults {
		public static final int MAX_RETRIES = 1;
		public static final int INITIAL_DELAY = 1000;
		public static final int MAX_DELAY = 5000;
		public static final int BACKOFF_FACTOR = 5000;
	}
	
	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {
		

		RetryConfig config = unmarshallConfig(t2FlowParser, configBean,
				"xstream", RetryConfig.class);

		
		Configuration c = new Configuration();		
		c.setConfigurableType(scufl2Uri.resolve("#Config"));

		PropertyResource resource = c.getPropertyResource();

		BigInteger maxRetries = config.getMaxRetries();	
		PropertyLiteral maxRetriesProp = new PropertyLiteral(maxRetries.longValue());
		resource.addProperty(scufl2Uri.resolve("#maxRetries"), maxRetriesProp);
	
		PropertyLiteral initialDelayProp = new PropertyLiteral(config.getInitialDelay());
		resource.addProperty(scufl2Uri.resolve("#initialDelay"), initialDelayProp);
		
		PropertyLiteral maxDelay = new PropertyLiteral(config.getMaxDelay());
		resource.addProperty(scufl2Uri.resolve("#maxDelay"), maxDelay);
		
		PropertyLiteral backoffFactor = new PropertyLiteral(config.getBackoffFactor());
		resource.addProperty(scufl2Uri.resolve("#backoffFactor"), backoffFactor);
		
		return c;
	}
}
