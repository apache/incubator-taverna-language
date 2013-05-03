package uk.org.taverna.scufl2.translator.t2flow.t23activities;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.XPathConfig;

public class XPathActivityParser extends AbstractActivityParser {

	private static final String ACTIVITY_XSD = "/uk/org/taverna/scufl2/translator/t2flow/xsd/xpathactivity.xsd";

	private static URI ravenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/xpath-activity/");

	private static URI ravenUIURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.ui-activities/xpath-activity/");

	private static String className = "net.sf.taverna.t2.activities.xpath.XPathActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/xpath");

	public static URI NAMESPACE_MAPPING_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/xpath/NamespaceMapping");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return (activityUriStr.startsWith(ravenURI.toASCIIString()) || activityUriStr
				.startsWith(ravenUIURI.toASCIIString()))
				&& activityUriStr.endsWith(className);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		URL xpathXsd = XPathActivityParser.class.getResource(ACTIVITY_XSD);
		try {
			return Arrays.asList(xpathXsd.toURI());
		} catch (Exception e) {
			throw new IllegalStateException("Can't find XPath schema "
					+ xpathXsd);
		}
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {

		XPathConfig xpathConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", XPathConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());
		parserState.setCurrentConfiguration(configuration);

		try {

			PropertyResource configResource = configuration
					.getPropertyResource();
			configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

			String xmlDocument = xpathConfig.getXmlDocument();
			if (xmlDocument != null) {
				configResource.addPropertyAsString(
					ACTIVITY_URI.resolve("#exampleXmlDocument"), xmlDocument);
			}

			String xpathExpression = xpathConfig.getXpathExpression();
			configResource.addPropertyAsString(
					ACTIVITY_URI.resolve("#xpathExpression"), xpathExpression);

			for (uk.org.taverna.scufl2.xml.t2flow.jaxb.XPathNamespaceMap.List list : xpathConfig
					.getXpathNamespaceMap().getList()) {

				String namespacePrefix = list.getContent().get(0).getValue();
				String namespaceURI = list.getContent().get(1).getValue();

				PropertyResource namespaceMapping = configResource.addPropertyAsNewResource(
						ACTIVITY_URI.resolve("#xpathNamespaceMap"), NAMESPACE_MAPPING_URI);
				namespaceMapping.addPropertyAsString(NAMESPACE_MAPPING_URI.resolve("#prefix"), namespacePrefix);
				namespaceMapping.addPropertyReference(NAMESPACE_MAPPING_URI.resolve("#uri"), URI.create(namespaceURI));
			}

		} finally {
			parserState.setCurrentConfiguration(null);
		}
		return configuration;
	}
}
