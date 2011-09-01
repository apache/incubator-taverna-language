package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.RESTConfig;

public class RESTActivityParser extends AbstractActivityParser {

	private static final String ACTIVITY_XSD = "../xsd/restactivity.xsd";

	private static URI ravenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/rest-activity/");

	private static URI ravenUIURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.ui-activities/rest-activity/");

	
	private static String className = "net.sf.taverna.t2.activities.rest.RESTActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/rest");
	
	public static URI HTTP_URI = URI.create("http://www.w3.org/2011/http#");
	public static URI HTTP_HEADERS_URI = URI.create("http://www.w3.org/2011/http-headers#");
	public static URI HTTP_METHODS_URI = URI.create("http://www.w3.org/2011/http-methods#");


	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return ( activityUriStr.startsWith(ravenURI.toASCIIString()) || activityUriStr.startsWith(ravenUIURI.toASCIIString()) ) 
				&& activityUriStr.endsWith(className);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL externalToolXsd = getClass().getResource(ACTIVITY_XSD);
		try {
			return Arrays.asList(externalToolXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find external tool schema "
					+ externalToolXsd);
		}
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	private static URITools uriTools = new URITools();

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ReaderException {

		


		RESTConfig restConfig = unmarshallConfig(t2FlowParser, configBean,
					"xstream", RESTConfig.class);
	
		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());
		getParserState().setCurrentConfiguration(configuration);
		try {
			PropertyResource configResource = configuration
					.getPropertyResource();
			configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

			

			return configuration;
		} finally {
			getParserState().setCurrentConfiguration(null);
		}
	}


}
