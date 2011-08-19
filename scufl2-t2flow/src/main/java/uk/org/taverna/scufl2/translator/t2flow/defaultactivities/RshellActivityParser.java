package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RShellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RShellSymanticType;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RetryConfig;

public class RshellActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI =
			T2FlowParser.ravenURI.resolve("net.sf.taverna.t2.activities/rshell-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.rshell.RshellActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/rshell");

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
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {
		RShellConfig rshellConfig = unmarshallConfig(t2FlowParser, configBean, "xstream", RShellConfig.class);
		
		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());
		
		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));
		
		// Basic properties		
		String script = rshellConfig.getScript();
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#script"), script);
		if (rshellConfig.getRVersion() != null) {
			configResource.addPropertyAsString(ACTIVITY_URI.resolve("#rVersion"), rshellConfig.getRVersion());
		}		 
		
		
		// Connection
		PropertyResource connection = configResource.addPropertyAsNewResource(ACTIVITY_URI.resolve("#connection"), ACTIVITY_URI.resolve("#Connection"));			
		connection.addPropertyAsString(ACTIVITY_URI.resolve("#hostname"), rshellConfig.getConnectionSettings().getHost());
		PropertyLiteral port = new PropertyLiteral(rshellConfig.getConnectionSettings().getPort());
		port.setLiteralType(PropertyLiteral.XSD_UNSIGNEDSHORT);
		connection.addProperty(ACTIVITY_URI.resolve("#port"), port);

		// ignored - Taverna 2.3+ uses credential manager
//		connection.addPropertyAsString(ACTIVITY_URI.resolve("#username"),
//				rshellConfig.getConnectionSettings().getUsername());
//		connection.addPropertyAsString(ACTIVITY_URI.resolve("#password"),
//				rshellConfig.getConnectionSettings().getPassword());
//		
		connection.addProperty(ACTIVITY_URI.resolve("#keepSessionAlive"), 
				new PropertyLiteral(rshellConfig.getConnectionSettings().isKeepSessionAlive()));
		
//		ignoooooored - we won't support the legacy ones anymore
//		if (rshellConfig.getConnectionSettings().isNewRVersion() == null || ! rshellConfig.getConnectionSettings().isNewRVersion()) {
//			connection.addProperty(ACTIVITY_URI.resolve("#legacy"), 
//				new PropertyLiteral(true));
//		}		
		
		
		
		// Activity ports
		Activity activity = getParserState().getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		for (ActivityPortDefinitionBean portBean : rshellConfig
				.getInputs().getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()
				) {
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		}
		for (ActivityPortDefinitionBean portBean : rshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			parseAndAddOutputPortDefinition(portBean, configuration, activity);			
		}
		
		// TODO: Semantic types
		
		RShellSymanticType inputSymanticTypes = rshellConfig.getInputSymanticTypes();
		inputSymanticTypes.getNetSfTavernaT2ActivitiesRshellRShellPortSymanticTypeBean();
		
		
		return configuration;
		
		
	}

}
