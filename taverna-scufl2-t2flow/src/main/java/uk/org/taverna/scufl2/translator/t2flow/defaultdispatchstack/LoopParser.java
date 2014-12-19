package uk.org.taverna.scufl2.translator.t2flow.defaultdispatchstack;

import static uk.org.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;

import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Activity;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.LoopConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.Property;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoopParser extends AbstractActivityParser {
	/**
	 * Expose some of the useful activity/config parser methods to LoopParser
	 * with a different parser state.
	 * <p>
	 * TODO: Refactor T2FlowParser to avoid the need for this 
	 */
	protected class ConditionalActivityParser extends T2FlowParser {
		public ConditionalActivityParser(ParserState origState) throws JAXBException {
			super();
			parserState.get().setCurrentProfile(origState.getCurrentProfile());
			parserState.get().setCurrentProcessor(origState.getCurrentProcessor());
		}
		@Override
		public uk.org.taverna.scufl2.api.activity.Activity parseActivity(
				Activity origActivity) throws ReaderException {
			uk.org.taverna.scufl2.api.activity.Activity parsed = super.parseActivity(origActivity);
			parserState.get().setCurrentActivity(parsed);
			return parsed;
		}
		@Override
		protected Configuration parseConfiguration(ConfigBean configBean)
				throws JAXBException, ReaderException {
			return super.parseConfiguration(configBean);
		}
	}

	private static final URI modelRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.core/workflowmodel-impl/");
	private static final String className = "net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Loop";
	public static final URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Loop");

	@Override
	public boolean canHandlePlugin(URI pluginURI) {
		String uriStr = pluginURI.toASCIIString();
		return uriStr.startsWith(modelRavenURI.toASCIIString())
				&& uriStr.endsWith(className);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		LoopConfig loopConfig = unmarshallConfig(t2FlowParser, configBean,
				"xstream", LoopConfig.class);

		final Configuration c = new Configuration();
		c.setType(scufl2Uri.resolve("Config"));

		ObjectNode json = (ObjectNode) c.getJson();

		json.put("runFirst", loopConfig.isRunFirst());

		for (Property prop : loopConfig.getProperties().getProperty())
			json.put(prop.getName(), prop.getValue());
		
		String conditionXml = loopConfig.getConditionXML();	
		if (conditionXml == null)
		    // activity is unconfigured (bug in T2). 
		    // Return c only if there are properties beyond "runFirst"
			return json.size() > 1 ? c : null;
		Activity conditionActivity = unmarshallXml(
				parserState.getT2FlowParser(), conditionXml, Activity.class);
		try {
			ConditionalActivityParser internalParser = new ConditionalActivityParser(
					parserState);

			uk.org.taverna.scufl2.api.activity.Activity newActivity = internalParser
					.parseActivity(conditionActivity);
			String name = parserState.getCurrentProcessor().getName() + "-loop";
			newActivity.setName(name);
			parserState.getCurrentProfile().getActivities()
					.addWithUniqueName(newActivity);
			newActivity.setParent(parserState.getCurrentProfile());

			Configuration newConfig = internalParser
					.parseConfiguration(conditionActivity.getConfigBean());
			newConfig.setName(name);
			newConfig.setConfigures(newActivity);
			parserState.getCurrentProfile().getConfigurations()
					.addWithUniqueName(newConfig);
			// URI uriActivity = uriTools.relativeUriForBean(newActivity, parserState.getCurrentProfile());
			json.put("conditionActivity", newActivity.getName());
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse conditional loop activity",
					e);
		}
		return c;
	}
}
