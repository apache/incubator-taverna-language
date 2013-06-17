package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import static uk.org.taverna.scufl2.translator.t2flow.defaultactivities.WSDLActivityParser.WSDL;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.XMLSplitterConfig;

public class WSDLXMLSplitterParser extends AbstractActivityParser {

	private static URI wsdlActivityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/wsdl-activity/");

	private static String inputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLInputSplitterActivity";
	private static String outputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLOutputSplitterActivity";

	public static URI SPLITTER = WSDL.resolve("xml-splitter");
	public static URI XML_INPUT_SPLITTER = SPLITTER.resolve("xml-splitter/in");
	public static URI XML_OUTPUT_SPLITTER = SPLITTER.resolve("xml-splitter/out");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(wsdlActivityRavenURI.toASCIIString())) {
			return false;
		}
		if (activityUriStr.endsWith(inputSplitterClassName)
				|| activityUriStr.endsWith(outputSplitterClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		String activityUriStr = t2flowActivity.toASCIIString();
		if (activityUriStr.endsWith(inputSplitterClassName)) {
			return XML_INPUT_SPLITTER;
		} else if (activityUriStr.endsWith(outputSplitterClassName)) {
			return XML_OUTPUT_SPLITTER;		
		} else {
			throw new IllegalArgumentException("Unexpected URI " + t2flowActivity);
		}
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) throws ReaderException {

		XMLSplitterConfig splitterConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", XMLSplitterConfig.class);		

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());
		
		PropertyResource resource = configuration.getJson();		
		resource.setTypeURI(
				SPLITTER.resolve("#Config"));

		String wrappedTypeXML = splitterConfig.getWrappedTypeXML();
		PropertyLiteral literalXml = new PropertyLiteral(wrappedTypeXML, PropertyLiteral.XML_LITERAL);
		resource.addProperty(SPLITTER.resolve("#wrappedType"), literalXml);
		

		Activity activity = parserState.getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		
		for (ActivityPortDefinitionBean portBean : splitterConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean()) {
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		}		
		for (ActivityPortDefinitionBean portBean : splitterConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean()) {
			parseAndAddOutputPortDefinition(portBean, configuration, activity);			
		}
	
		return configuration;
	}

}
