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

	public static URI WSDL = URI
			.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static URI SECURITY = WSDL.resolve("wsdl/security");
	public static URI OPERATION = WSDL.resolve("wsdl/operation");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(wsdlActivityRavenURI.toASCIIString())) {
			return false;
		}
		if (activityUriStr.endsWith(wsdlActivityClassName)) {
			return true;
		}
		return false;
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {		
		return WSDL;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {

		WSDLConfig wsdlConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", WSDLConfig.class);

		Configuration configuration = new Configuration();
		configuration.getPropertyResource().setTypeURI(
				WSDL.resolve("#Config"));

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
						OPERATION);
		wsdlOperation.addPropertyReference(OPERATION.resolve("#wsdl"), wsdl);
		wsdlOperation.addPropertyAsString(OPERATION.resolve("#name"), operation);

		if (wsdlConfig.getSecurityProfile() != null
				&& !wsdlConfig.getSecurityProfile().isEmpty()) {
			URI securityProfileURI = SECURITY.resolve("#" + wsdlConfig
					.getSecurityProfile());
			configuration.getPropertyResource().addPropertyReference(
					SECURITY.resolve("#profile"), securityProfileURI);
		}
		return configuration;
	}

}
