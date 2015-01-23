package org.apache.taverna.scufl2.translator.t2flow.defaultdispatchstack;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import static org.apache.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.Activity;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.LoopConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.Property;

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
		public org.apache.taverna.scufl2.api.activity.Activity parseActivity(
				Activity origActivity) throws ReaderException {
			org.apache.taverna.scufl2.api.activity.Activity parsed = super.parseActivity(origActivity);
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

			org.apache.taverna.scufl2.api.activity.Activity newActivity = internalParser
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
