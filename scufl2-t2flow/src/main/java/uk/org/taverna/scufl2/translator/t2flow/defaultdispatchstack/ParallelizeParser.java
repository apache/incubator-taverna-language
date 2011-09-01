package uk.org.taverna.scufl2.translator.t2flow.defaultdispatchstack;

import java.math.BigInteger;
import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ParallelizeConfig;

public class ParallelizeParser extends AbstractActivityParser {

		private static URI ravenURI =
			T2FlowParser.ravenURI.resolve("net.sf.taverna.t2.core/workflowmodel-impl/");

		private static String className = "net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Parallelize";

		public static URI scufl2Uri = URI
				.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Parallelize");

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
			

			ParallelizeConfig parallelConfig = unmarshallConfig(t2FlowParser, configBean,
					"xstream", ParallelizeConfig.class);

			
			Configuration c = new Configuration();		
			c.setConfigurableType(scufl2Uri.resolve("#Config"));

			BigInteger maxJobs = parallelConfig.getMaxJobs();
			if (maxJobs != null) {
				PropertyLiteral p = new PropertyLiteral(maxJobs.longValue());
				c.getPropertyResource().addProperty(scufl2Uri.resolve("#maxJobs"), p);
			}
			return c;
		}

	}