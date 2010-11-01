package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BeanshellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DataflowConfig;

public class BeanshellActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/beanshell-activity/");

	private static URI localWorkerActivityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/localworker-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.beanshell.BeanshellActivity";

	private static String localWorkerActivityClassName = "net.sf.taverna.t2.activities.localworker.LocalworkerActivity";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName)) {
			return true;
		}
		if (activityUriStr.startsWith(localWorkerActivityRavenURI
				.toASCIIString())
				&& activityUriStr.endsWith(localWorkerActivityClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ParseException {
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);
		Configuration configuration = new Configuration();

		String script = beanshellConfig.getScript();
		// ConfigurablePropertyConfiguration configurablePropertyConfiguration =
		// new ConfigurablePropertyConfiguration();
		// configurablePropertyConfiguration.setParent(configuration);
		// ConfigurableProperty configuredProperty = new ConfigurableProperty(
		// scufl2Uri.resolve("#script").toASCIIString());
		// configurablePropertyConfiguration
		// .setConfiguredProperty(configuredProperty);
		// configurablePropertyConfiguration.setValue(script);

		return configuration;
	}

}
