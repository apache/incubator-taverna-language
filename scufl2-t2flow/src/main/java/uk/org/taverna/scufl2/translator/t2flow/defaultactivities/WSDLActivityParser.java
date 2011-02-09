package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.WSDLConfig;

public class WSDLActivityParser extends AbstractActivityParser {

	private static URI wsdlActivityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/wsdl-activity/");

	private static String wsdlActivityClassName = "net.sf.taverna.t2.activities.wsdl.WSDLActivity";

	private static String inputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLInputSplitterActivity";
	private static String outputSplitterClassName = "net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLOutputSplitterActivity";

	public static URI WSDL = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static URI XML_INPUT_SPLITTER = WSDL.resolve("wsdl/xmlinputsplitter");
	public static URI SECURITY = WSDL.resolve("wsdl/security");
	public static URI XML_OUTPUT_SPLITTER = WSDL.resolve("wsdl/xmloutputsplitter");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(wsdlActivityRavenURI.toASCIIString())) {
			return false;
		}
		if (activityUriStr.endsWith(wsdlActivityClassName)
				|| activityUriStr.endsWith(inputSplitterClassName)
				|| activityUriStr.endsWith(outputSplitterClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		String activityUriStr = t2flowActivity.toASCIIString();
		if (activityUriStr.endsWith(inputSplitterClassName)) {
			return XML_INPUT_SPLITTER;
		} else if (activityUriStr.endsWith(outputSplitterClassName)) {
			return XML_OUTPUT_SPLITTER;
		} else {
			return WSDL;
		}
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {

		// TODO: XML splitters

		WSDLConfig wsdlConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", WSDLConfig.class);

		Configuration configuration = new Configuration();
		configuration.getPropertyResource().setTypeURI(
				WSDL.resolve("#ConfigType"));

		URI wsdl;
		try {
			wsdl = URI.create(wsdlConfig.getWsdl());
			if (!wsdl.isAbsolute()) {
				throw new ReaderException("WSDL URI is not absolute: "
						+ wsdlConfig.getWsdl());
			}
		} catch (IllegalArgumentException ex) {
			throw new ReaderException("WSDL not a valid URI: "
					+ wsdlConfig.getWsdl());
		} catch (NullPointerException ex) {
			throw new ReaderException("WSDL config has no wsdl set");
		}
		String operation = wsdlConfig.getOperation();
		if (operation == null || operation.equals("")) {
			throw new ReaderException("WSDL config has no operation set");
		}

		PropertyResource wsdlOperation = configuration.getPropertyResource()
				.addPropertyAsNewResource(WSDL.resolve("#operation"),
						WSDL.resolve("#Operation"));
		wsdlOperation.addPropertyReference(WSDL.resolve("#wsdl"), wsdl);
		wsdlOperation.addPropertyAsString(WSDL.resolve("#operationName"), operation);

		if (wsdlConfig.getSecurityProfile() != null
				&& !wsdlConfig.getSecurityProfile().isEmpty()) {
			URI securityProfileURI = SECURITY.resolve("#" + wsdlConfig
					.getSecurityProfile());
			configuration.getPropertyResource().addPropertyReference(
					WSDL.resolve("#securityProfile"), securityProfileURI);
		}

		// TODO: Security stuff
		return configuration;
	}

}
