package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BasicArtifact;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BeanshellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ClassLoaderSharing;
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

	public static URI LOCAL_WORKER_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/localworker/");

	
	public static URI DEPENDENCY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/dependency");
	
	private static URITools uriTools = new URITools();

	

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
			ConfigBean configBean, ParserState parserState) throws ReaderException {
		
	
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		PropertyResource configResource = configuration.getJson();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));
		
		if (beanshellConfig.getLocalworkerName() != null) {
			URI localWorkerURI = LOCAL_WORKER_URI.resolve(uriTools.validFilename(beanshellConfig.getLocalworkerName()));
			URI relation = ACTIVITY_URI.resolve("#derivedFrom");
			// FIXME: As we can't read the annotation chain yet, we can't tell
			// whether this local worker has been edited or not, and so 
			// can't use #definedBy
			configResource.addPropertyReference(relation, 
					localWorkerURI);
		}
		
		
		String script = beanshellConfig.getScript();
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#script"), script);


		ClassLoaderSharing classLoaderSharing = beanshellConfig.getClassLoaderSharing();
		if (classLoaderSharing == ClassLoaderSharing.SYSTEM) {
			configResource.addPropertyReference(DEPENDENCY_URI.resolve("#classLoader"), 
					DEPENDENCY_URI.resolve("#SystemClassLoader"));				
		} // default is WorkflowClassLoader
 		
		if (beanshellConfig.getLocalDependencies() != null) {			
			for (String localDep : beanshellConfig.getLocalDependencies().getString()) {
				PropertyResource dependency = configResource.addPropertyAsNewResource(DEPENDENCY_URI.resolve("#dependency"), 
						DEPENDENCY_URI.resolve("#LocalJarDependency"));
				dependency.addPropertyAsString(DEPENDENCY_URI.resolve("#jarFile"), localDep);
			}
		}


		/**
		 * Note: Maven Dependencies are not supported by Taverna 3 - 
		 * only here for t2flow->t2flow scenarios
		 */
		if (beanshellConfig.getArtifactDependencies() != null) {			
			for (BasicArtifact mavenDep : beanshellConfig.getArtifactDependencies().getNetSfTavernaRavenRepositoryBasicArtifact()) {
				PropertyResource dependency = configResource.addPropertyAsNewResource(DEPENDENCY_URI.resolve("#dependency"), 
						DEPENDENCY_URI.resolve("#MavenDependency"));				
				dependency.addPropertyAsString(DEPENDENCY_URI.resolve("#mavenGroupId"), mavenDep.getGroupId());
				dependency.addPropertyAsString(DEPENDENCY_URI.resolve("#mavenArtifactId"), mavenDep.getArtifactId());
				dependency.addPropertyAsString(DEPENDENCY_URI.resolve("#mavenVersion"), mavenDep.getVersion());
			}
		}
		
		Activity activity = parserState.getCurrentActivity();
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
