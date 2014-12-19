package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import static uk.org.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.WSDLConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class WSDLActivityParser extends AbstractActivityParser {
	private static final URI wsdlActivityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/wsdl-activity/");
	private static final String wsdlActivityClassName = "net.sf.taverna.t2.activities.wsdl.WSDLActivity";
	public static final URI WSDL = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static final URI SECURITY = WSDL.resolve("wsdl/security");
	public static final URI OPERATION = WSDL.resolve("wsdl/operation");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(wsdlActivityRavenURI.toASCIIString()))
			return false;
		if (activityUriStr.endsWith(wsdlActivityClassName))
			return true;
		return false;
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return WSDL;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		WSDLConfig wsdlConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", WSDLConfig.class);

		Configuration configuration = new Configuration();
		configuration.setType(WSDL.resolve("#Config"));

		URI wsdl;
		try {
			wsdl = URI.create(wsdlConfig.getWsdl());
			if (!wsdl.isAbsolute())
				throw new ReaderException("WSDL URI is not absolute: "
						+ wsdlConfig.getWsdl());
		} catch (IllegalArgumentException ex) {
			throw new ReaderException("WSDL not a valid URI: "
					+ wsdlConfig.getWsdl());
		} catch (NullPointerException ex) {
			throw new ReaderException("WSDL config has no wsdl set");
		}
		String operation = wsdlConfig.getOperation();
		if (operation == null || operation.equals(""))
			throw new ReaderException("WSDL config has no operation set");

		ObjectNode json = (ObjectNode) configuration.getJson();

		ObjectNode wsdlOperation = json.objectNode();
		json.put("operation", wsdlOperation);

		wsdlOperation.put("wsdl", wsdl.toString());
		wsdlOperation.put("name", operation);

		if (wsdlConfig.getSecurityProfile() != null
				&& !wsdlConfig.getSecurityProfile().isEmpty()) {
			URI securityProfileURI = SECURITY.resolve("#"
					+ wsdlConfig.getSecurityProfile());
			json.put("securityProfile", securityProfileURI.toString());
		}
		return configuration;
	}
}
