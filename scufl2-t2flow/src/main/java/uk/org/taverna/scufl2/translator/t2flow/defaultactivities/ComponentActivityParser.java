package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import static java.net.URI.create;
import static uk.org.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ComponentConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ComponentActivityParser extends AbstractActivityParser {
	private static URI activityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.component/component-activity/");
	private static String activityClassName = "net.sf.taverna.t2.component.ComponentActivity";
	public static URI ACTIVITY_URI = create("http://ns.taverna.org.uk/2010/activity/component");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return (activityUriStr.startsWith(activityRavenURI.toASCIIString()) && activityUriStr
				.endsWith(activityClassName));
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		ComponentConfig config = unmarshallConfig(t2FlowParser, configBean,
				"xstream", ComponentConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(ACTIVITY_URI.resolve("#Config"));

		json.put("registryBase", config.getRegistryBase());
		json.put("familyName", config.getFamilyName());
		json.put("componentName", config.getComponentName());
		if (config.getVersion() != null)
			json.put("componentVersion", (int) config.getVersion());

		return configuration;
	}
}
