package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.DataProperty;
import uk.org.taverna.scufl2.api.configurations.Property;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityInputPorts;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean.MimeTypes;
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
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ParseException {
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);
		Configuration configuration = new Configuration();
		configuration.setConfigurationType(ACTIVITY_URI.resolve("#ConfigType"));
		String script = beanshellConfig.getScript();
		DataProperty property = new DataProperty(
				ACTIVITY_URI.resolve("#script"), script);

		// TODO: Dependencies, activities, etc
		
		Activity activity = getParserState().getCurrentActivity();		
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		for (ActivityPortDefinitionBean b : beanshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()) {
			InputActivityPort a = new InputActivityPort();
			a.setName(b.getName());
			if (b.getDepth() != null) {
				a.setDepth(b.getDepth().intValue());
			}
				
			Configuration portConfig = new Configuration();
			portConfig.setConfigurationType(ACTIVITY_URI.resolve("#PortConfigType"));
			portConfig.setConfigures(a);
			List<Property> portProps = portConfig.getProperties();
			
			if (b.getTranslatedElementType() != null) {
				// As "translated element type" is confusing, we'll instead use "dataType"
				portProps.add(new DataProperty(ACTIVITY_URI.resolve("#dataType"), b.getTranslatedElementType()));
			}
			// T2-1681: Ignoring isAllowsLiteralValues and handledReferenceScheme  
			
			if (! portProps.isEmpty()) {
				// Add the port configuration
				getParserState().getCurrentProfile().getConfigurations().add(portConfig);
			}				
			a.setParent(activity);
			// TODO: Mime types, etc
		}
		for (ActivityPortDefinitionBean b : beanshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			OutputActivityPort a = new OutputActivityPort();
			a.setName(b.getName());
			if (b.getDepth() != null) {
				a.setDepth(b.getDepth().intValue());
			}
			if (b.getGranularDepth() != null) {
				a.setGranularDepth(b.getGranularDepth().intValue());
			}
			
			Configuration portConfig = new Configuration();
			portConfig.setConfigurationType(ACTIVITY_URI.resolve("#PortConfigType"));
			portConfig.setConfigures(a);
			List<Property> portProps = portConfig.getProperties();

			MimeTypes mimeTypes = b.getMimeTypes();
			if (mimeTypes != null) {
				// FIXME: Do as annotation as this is not configuration
				URI mimeType = ACTIVITY_URI.resolve("#mimeType");
				if (mimeTypes.getElement() != null) {
					String s = mimeTypes.getElement();
					if (s.contains("'")) {
						s = s.split("'")[1];
					}					
					portProps.add(new DataProperty(mimeType, s));
				}
				if (mimeTypes.getString() != null) {
					for (String s : mimeTypes.getString()) {
						if (s.contains("'")) {
							s = s.split("'")[1];
						}
						portProps.add(new DataProperty(mimeType, s));
					}
				}					
			}
			if (! portProps.isEmpty()) {
				// Add the port configuration
				getParserState().getCurrentProfile().getConfigurations().add(portConfig);
			}				
			a.setParent(activity);		
		}

		configuration.getProperties().add(property);
		return configuration;
	}

}
