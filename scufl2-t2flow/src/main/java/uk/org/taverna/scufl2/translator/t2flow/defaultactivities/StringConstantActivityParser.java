package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import org.junit.matchers.StringContains;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.DataProperty;
import uk.org.taverna.scufl2.api.configurations.ObjectProperty;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.StringConstantConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.WSDLConfig;

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
			ConfigBean configBean) throws ParseException {
		StringConstantConfig strConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", StringConstantConfig.class);
		String value = strConfig.getValue();
		if (value == null) {
			throw new ParseException("String constant configuration has no value set");
		}
		Configuration configuration = new Configuration();

		configuration = new Configuration();
		DataProperty prop = new DataProperty(CONSTANT.resolve("#string"), value);
		configuration.getProperties().add(prop);
		return configuration;
	}


}
