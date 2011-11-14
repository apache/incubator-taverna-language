package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.math.BigInteger;
import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ApiConsumerConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public class ApiConsomerActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/apiconsumer-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.apiconsumer.ApiConsumerActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/apiconsumer");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser, ConfigBean configBean,
			ParserState parserState) throws ReaderException {
		ApiConsumerConfig config = unmarshallConfig(t2FlowParser, configBean, "xstream",
				ApiConsumerConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#apiConsumerDescription"),
				config.getApiConsumerDescription());
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#apiConsumerName"),
				config.getApiConsumerName());
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#description"),
				config.getDescription());
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#className"),
				config.getClassName());
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#methodName"),
				config.getMethodName());

		PropertyList parameterNames = new PropertyList();
		for (String parameterName : config.getParameterNames().getString()) {
			parameterNames.add(new PropertyLiteral(parameterName));
		}
		configResource.addProperty(ACTIVITY_URI.resolve("#parameterNames"), parameterNames);

		PropertyList parameterDimensions = new PropertyList();
		for (BigInteger parameterDimension : config.getParameterDimensions().getInt()) {
			parameterDimensions.add(new PropertyLiteral(parameterDimension.intValue()));
		}
		configResource.addProperty(ACTIVITY_URI.resolve("#parameterDimensions"),
				parameterDimensions);

		PropertyList parameterTypes = new PropertyList();
		for (String parameterType : config.getParameterTypes().getString()) {
			parameterTypes.add(new PropertyLiteral(parameterType));
		}
		configResource.addProperty(ACTIVITY_URI.resolve("#parameterTypes"), parameterTypes);

		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#returnType"),
				config.getReturnType());
		configResource.addProperty(ACTIVITY_URI.resolve("#returnDimension"), new PropertyLiteral(
				config.getReturnDimension().intValue()));
		configResource.addProperty(ACTIVITY_URI.resolve("#isMethodConstructor"),
				new PropertyLiteral(config.isIsMethodConstructor()));
		configResource.addProperty(ACTIVITY_URI.resolve("#isMethodStatic"), new PropertyLiteral(
				config.isIsMethodStatic()));

		return configuration;
	}

}
