package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BeanshellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public class BeanshellActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/beanshell-activity/");

	private static URI localWorkerActivityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/localworker-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.beanshell.BeanshellActivity";

	private static String localWorkerActivityClassName = "net.sf.taverna.t2.activities.localworker.LocalworkerActivity";

	public static URI ACTIVITY_URI = URI
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
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {
		
		// FIXME: Test with local workers
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());

		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));
		
		String script = beanshellConfig.getScript();
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#script"), script);

		// TODO: Dependencies, activities, etc

		Activity activity = getParserState().getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()) {
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		}
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			parseAndAddOutputPortDefinition(portBean, configuration, activity);
			
		}
		return configuration;
	}

}
