package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean.MimeTypes;
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

	public static URI MEDIATYPES_URI = URI.create("http://purl.org/NET/mediatypes/");

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
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ParseException {
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());

		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#ConfigType"));
		String script = beanshellConfig.getScript();
		configResource.addProperty(ACTIVITY_URI.resolve("#script"), script);

		// TODO: Dependencies, activities, etc

		Activity activity = getParserState().getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()) {
			InputActivityPort inputPort = new InputActivityPort();
			inputPort.setName(portBean.getName());
			inputPort.setParent(activity);

			if (portBean.getDepth() != null) {
				inputPort.setDepth(portBean.getDepth().intValue());
			}

			PropertyResource portConfig = configResource.addPropertyResource(
					ACTIVITY_URI.resolve("#inputPortDefinition"),
					ACTIVITY_URI.resolve("#InputPortDefinition"));

			URI portUri = new URITools().relativeUriForBean(inputPort,
					configuration);
			portConfig.addPropertyURI(
					ACTIVITY_URI.resolve("#definesInputPort"), portUri);

			if (portBean.getTranslatedElementType() != null) {
				// As "translated element type" is confusing, we'll instead use "dataType"
				portConfig.addPropertyURI(
						ACTIVITY_URI.resolve("#dataType"),
						URI.create("java:" + portBean.getTranslatedElementType()));

				// TODO: Include mapping to XSD types like xsd:string
			}
			// T2-1681: Ignoring isAllowsLiteralValues and handledReferenceScheme
			// TODO: Mime types, etc
		}
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			OutputActivityPort outputPort = new OutputActivityPort();
			outputPort.setName(portBean.getName());
			outputPort.setParent(activity);
			if (portBean.getDepth() != null) {
				outputPort.setDepth(portBean.getDepth().intValue());
			}
			if (portBean.getGranularDepth() != null) {
				outputPort.setGranularDepth(portBean.getGranularDepth().intValue());
			}

			PropertyResource portConfig = configResource.addPropertyResource(
					ACTIVITY_URI.resolve("#outputPortDefinition"),
					ACTIVITY_URI.resolve("#OutputPortDefinition"));

			URI portUri = new URITools().relativeUriForBean(outputPort, configuration);

			portConfig.addPropertyURI(
					ACTIVITY_URI.resolve("#definesOutputPort"), portUri);

			MimeTypes mimeTypes = portBean.getMimeTypes();
			if (mimeTypes != null) {
				// FIXME: Do as annotation as this is not configuration
				URI mimeType = ACTIVITY_URI.resolve("#expectedMimeType");
				if (mimeTypes.getElement() != null) {
					String s = mimeTypes.getElement();
					if (s.contains("'")) {
						s = s.split("'")[1];
					}
					portConfig.addPropertyURI(mimeType,
							MEDIATYPES_URI.resolve(s));
				}
				if (mimeTypes.getString() != null) {
					for (String s : mimeTypes.getString()) {
						if (s.contains("'")) {
							s = s.split("'")[1];
						}
						portConfig.addPropertyURI(mimeType,
								MEDIATYPES_URI.resolve(s));
					}
				}
			}
			outputPort.setParent(activity);
		}
		return configuration;
	}

}
