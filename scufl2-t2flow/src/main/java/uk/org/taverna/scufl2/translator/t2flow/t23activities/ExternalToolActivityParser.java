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
import uk.org.taverna.scufl2.xml.t2flow.jaxb.BeanshellConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.UsecaseConfig;

public class ExternalToolActivityParser extends AbstractActivityParser {

	private static final String EXTERNALTOOLACTIVITY_XSD = "../xsd/externaltoolactivity.xsd";

	private static URI usecaseActivityRavenUri = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/usecase-activity/");

	private static String usecaseActivityClass = "net.sf.taverna.t2.activities.usecase.UseCaseActivity";

	private static URI externalToolRavenUri = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/external-tool-activity/");

	private static String externalToolClass = "net.sf.taverna.t2.activities.externaltool.ExternalToolActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/tool");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (activityUriStr.startsWith(usecaseActivityRavenUri.toASCIIString())
				&& activityUriStr.endsWith(usecaseActivityClass)) {
			return true;
		}
		return activityUriStr.startsWith(externalToolRavenUri.toASCIIString())
				&& activityUriStr.endsWith(externalToolClass);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL externalToolXsd = getClass().getResource(EXTERNALTOOLACTIVITY_XSD);
		try {
			return Arrays.asList(externalToolXsd.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't find external tool schema " + externalToolXsd);
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
		
		UsecaseConfig usecaseConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", UsecaseConfig.class);
		
		Configuration configuration = new Configuration();
		configuration.setParent(getParserState().getCurrentProfile());
		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));
		
		if (usecaseConfig.getRepositoryUrl() != null) {
			URI repositoryUri = URI.create(usecaseConfig.getRepositoryUrl());
			URI usecase = repositoryUri.resolve("#" + uriTools.validFilename(usecaseConfig.getUsecaseid()));
			configResource.addPropertyReference(ACTIVITY_URI.resolve("#usecase"), usecase);
		}		
		return configuration;
	}
}