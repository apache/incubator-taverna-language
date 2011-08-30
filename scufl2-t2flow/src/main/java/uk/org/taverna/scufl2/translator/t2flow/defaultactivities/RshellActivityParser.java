package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RShellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RShellSymanticType;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RShellSymanticType.RShellPortSymanticTypeBean;

public class RshellActivityParser extends AbstractActivityParser {

	/*
	 * A lovely artifact of xstream 'efficiency' - Xpath backpointers to
	 * previous elements. luckily we are here restricted within this specific
	 * config bean
	 * 
	 * Example:
	 * ../../net.sf.taverna.t2.activities.rshell.RShellPortSymanticTypeBean
	 * [3]/symanticType
	 * ../../../inputSymanticTypes/net.sf.taverna.t2.activities.
	 * rshell.RShellPortSymanticTypeBean[2]/symanticType
	 */
	Pattern strangeXpath = Pattern
			.compile("[./]+/(inputSymanticTypes)?.*TypeBean(\\[(\\d+)\\])?/symanticType$");

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/rshell-activity/");

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
		RShellConfig rshellConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", RShellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());

		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

		// Basic properties
		String script = rshellConfig.getScript();
		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#script"),
				script);
		if (rshellConfig.getRVersion() != null) {
			configResource.addPropertyAsString(
					ACTIVITY_URI.resolve("#rVersion"),
					rshellConfig.getRVersion());
		}

		// Connection
		PropertyResource connection = configResource.addPropertyAsNewResource(
				ACTIVITY_URI.resolve("#connection"),
				ACTIVITY_URI.resolve("#Connection"));
		connection.addPropertyAsString(ACTIVITY_URI.resolve("#hostname"),
				rshellConfig.getConnectionSettings().getHost());
		PropertyLiteral port = new PropertyLiteral(rshellConfig
				.getConnectionSettings().getPort());
		port.setLiteralType(PropertyLiteral.XSD_UNSIGNEDSHORT);
		connection.addProperty(ACTIVITY_URI.resolve("#port"), port);

		// ignored - Taverna 2.3+ uses credential manager
		// connection.addPropertyAsString(ACTIVITY_URI.resolve("#username"),
		// rshellConfig.getConnectionSettings().getUsername());
		// connection.addPropertyAsString(ACTIVITY_URI.resolve("#password"),
		// rshellConfig.getConnectionSettings().getPassword());
		//
		connection.addProperty(ACTIVITY_URI.resolve("#keepSessionAlive"),
				new PropertyLiteral(rshellConfig.getConnectionSettings()
						.isKeepSessionAlive()));

		// ignoooooored - we won't support the legacy ones anymore
		// if (rshellConfig.getConnectionSettings().isNewRVersion() == null || !
		// rshellConfig.getConnectionSettings().isNewRVersion()) {
		// connection.addProperty(ACTIVITY_URI.resolve("#legacy"),
		// new PropertyLiteral(true));
		// }

		// Activity ports
		Activity activity = getParserState().getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		Map<URI, PropertyResource> portDefs = new HashMap<URI, PropertyResource>();
		for (ActivityPortDefinitionBean portBean : rshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()) {
			PropertyResource portDef = parseAndAddInputPortDefinition(portBean,
					configuration, activity);
			try {
				URI portURI = portDef
						.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION
								.resolve("#definesInputPort"));
				portDefs.put(portURI, portDef);
			} catch (PropertyException ex) {
				throw new ReaderException("Did not define port in " + portDef);
			}
		}
		for (ActivityPortDefinitionBean portBean : rshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			PropertyResource portDef = parseAndAddOutputPortDefinition(
					portBean, configuration, activity);
			try {

				URI portURI = portDef
						.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION
								.resolve("#definesOutputPort"));
				portDefs.put(portURI, portDef);
			} catch (PropertyException ex) {
				throw new ReaderException("Did not define port in " + portDef);
			}

		}

		RShellSymanticType inputSymanticTypes = rshellConfig
				.getInputSymanticTypes();
		List<String> foundInputTypes = new ArrayList<String>();
		for (RShellPortSymanticTypeBean symanticType : inputSymanticTypes
				.getNetSfTavernaT2ActivitiesRshellRShellPortSymanticTypeBean()) {
			String portName = symanticType.getName();
			InputActivityPort symanticPort = getParserState()
					.getCurrentActivity().getInputPorts().getByName(portName);
			URI portUri = new URITools().relativeUriForBean(symanticPort,
					configuration);

			PropertyResource portDef = portDefs.get(portUri);
			if (portDef == null) {
				throw new ReaderException("Can't find the port definition for "
						+ portUri);
			}
			String symanticValue = symanticType.getSymanticType().getValue();

			String reference = symanticType.getSymanticType().getReference();
			if (reference != null) {

				Matcher matcher = strangeXpath.matcher(reference);
				if (matcher == null || !matcher.matches()) {
					throw new ReaderException(
							"Unhandled xstream xpath expression: " + reference);
				}
				String position = matcher.group(3);
				if (position == null) {
					position = "1";
				}
				symanticValue = foundInputTypes
						.get(Integer.parseInt(position) - 1);
			}

			foundInputTypes.add(symanticValue); // Even if it's null - so the
												// index is correct
			if (symanticValue != null) {
				portDef.addPropertyReference(
						Scufl2Tools.PORT_DEFINITION.resolve("#dataType"),
						ACTIVITY_URI.resolve("#" + symanticValue));
			}
		}
		// FIXME: Avoid this repetition. Would require a fair bit of parser
		// state..
		RShellSymanticType outputSymanticTypes = rshellConfig
				.getOutputSymanticTypes();
		List<String> foundOutputTypes = new ArrayList<String>();
		for (RShellPortSymanticTypeBean symanticType : outputSymanticTypes
				.getNetSfTavernaT2ActivitiesRshellRShellPortSymanticTypeBean()) {
			String portName = symanticType.getName();
			OutputActivityPort symanticPort = getParserState()
					.getCurrentActivity().getOutputPorts().getByName(portName);
			URI portUri = new URITools().relativeUriForBean(symanticPort,
					configuration);

			PropertyResource portDef = portDefs.get(portUri);
			if (portDef == null) {
				throw new ReaderException("Can't find the port definition for "
						+ portUri);
			}
			String symanticValue = symanticType.getSymanticType().getValue();

			String reference = symanticType.getSymanticType().getReference();
			if (reference != null) {
				// A lovely artifact of xstream 'efficiency' - Xpath
				// backpointers to previous elements.
				// luckily we are here restricted within this specific config
				// bean
				//
				// Example:
				// ../../net.sf.taverna.t2.activities.rshell.RShellPortSymanticTypeBean[3]/symanticType
				// ../../../inputSymanticTypes/net.sf.taverna.t2.activities.rshell.RShellPortSymanticTypeBean[2]/symanticType

				Matcher matcher = strangeXpath.matcher(reference);
				if (matcher == null || !matcher.matches()) {
					throw new ReaderException(
							"Unhandled xstream xpath expression: " + reference);
				}

				boolean isInputSymantic = matcher.group(1) != null;
				String position = matcher.group(3);
				if (position == null) {
					position = "1";
				}
				if (isInputSymantic) {
					symanticValue = foundInputTypes.get(Integer
							.parseInt(position) - 1);
				} else {
					symanticValue = foundOutputTypes.get(Integer
							.parseInt(position) - 1);
				}
			}

			foundOutputTypes.add(symanticValue); // Even if it's null - so the
													// index is correct
			if (symanticValue != null) {
				portDef.addPropertyReference(
						Scufl2Tools.PORT_DEFINITION.resolve("#dataType"),
						ACTIVITY_URI.resolve("#" + symanticValue));
			}
		}

		return configuration;

	}

}
