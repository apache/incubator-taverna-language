package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.StringConstantConfig;

public class StringConstantActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI =
			T2FlowParser.ravenURI.resolve("net.sf.taverna.t2.activities/stringconstant-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.stringconstant.StringConstantActivity";

	public static URI CONSTANT = URI
			.create("http://ns.taverna.org.uk/2010/activity/constant");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		return CONSTANT;
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {
		StringConstantConfig strConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", StringConstantConfig.class);
		String value = strConfig.getValue();
		if (value == null) {
			throw new ReaderException("String constant configuration has no value set");
		}
		Configuration configuration = new Configuration();
		configuration.getPropertyResource().setTypeURI(
				CONSTANT.resolve("ConfigType"));
		configuration.getPropertyResource().addPropertyAsString(
				CONSTANT.resolve("#string"), value);
		return configuration;
	}


}
