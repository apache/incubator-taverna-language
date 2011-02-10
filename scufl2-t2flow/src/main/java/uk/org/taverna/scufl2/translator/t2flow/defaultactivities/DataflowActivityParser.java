package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DataflowConfig;

public class DataflowActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/dataflow-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/nested-workflow");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {
		DataflowConfig dataflowConfig = unmarshallConfig(t2FlowParser,
				configBean, "dataflow", DataflowConfig.class);
		String dataflowReference = dataflowConfig.getRef();

		Configuration configuration = new Configuration();

		// ConfigurablePropertyConfiguration configurablePropertyConfiguration =
		// new ConfigurablePropertyConfiguration();
		// configurablePropertyConfiguration.setParent(configuration);
		// ConfigurableProperty configuredProperty = new ConfigurableProperty(
		// scufl2Uri.resolve("#workflow").toASCIIString());
		// configurablePropertyConfiguration
		// .setConfiguredProperty(configuredProperty);
		// configurablePropertyConfiguration.setValue(dataflowReference);
		return configuration;
	}

}
