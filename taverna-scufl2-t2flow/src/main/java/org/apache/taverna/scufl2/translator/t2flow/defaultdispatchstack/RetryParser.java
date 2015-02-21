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
import static org.apache.taverna.scufl2.translator.t2flow.defaultdispatchstack.RetryParser.Defaults.BACKOFF_FACTOR;
import static org.apache.taverna.scufl2.translator.t2flow.defaultdispatchstack.RetryParser.Defaults.INITIAL_DELAY;
import static org.apache.taverna.scufl2.translator.t2flow.defaultdispatchstack.RetryParser.Defaults.MAX_DELAY;
import static org.apache.taverna.scufl2.translator.t2flow.defaultdispatchstack.RetryParser.Defaults.MAX_RETRIES;

import java.math.BigInteger;
import java.net.URI;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.RetryConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RetryParser extends AbstractActivityParser {
	private static final URI modelRavenURI =
		ravenURI.resolve("net.sf.taverna.t2.core/workflowmodel-impl/");
	private static final String className = "net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Retry";
	public static final URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Retry");

	@Override
	public boolean canHandlePlugin(URI pluginURI) {
		String uriStr = pluginURI.toASCIIString();
		return uriStr.startsWith(modelRavenURI.toASCIIString())
				&& uriStr.endsWith(className);
	}

	/**
	 * Default values - ignored for now
	 */
	public static final class Defaults {
		public static final int MAX_RETRIES = 0;
		public static final int INITIAL_DELAY = 1000;
		public static final int MAX_DELAY = 5000;
		public static final double BACKOFF_FACTOR = 1.0;
	}
	
	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		RetryConfig config = unmarshallConfig(t2FlowParser, configBean,
				"xstream", RetryConfig.class);

		Configuration c = new Configuration();
		c.setType(scufl2Uri.resolve("#Config"));

		ObjectNode json = (ObjectNode) c.getJson();

		BigInteger maxRetries = config.getMaxRetries();
		if (maxRetries != null
				&& (maxRetries.longValue() != MAX_RETRIES || maxRetries
						.longValue() < 0))
			json.put("maxRetries", maxRetries.longValue());

		if (maxRetries != null && maxRetries.longValue() != 0) {
			// Neither of these makes sense if retries are disabled

			if (config.getInitialDelay() != INITIAL_DELAY
					&& config.getInitialDelay() > -1)
				json.put("initialDelay", config.getInitialDelay());

			if (config.getMaxDelay() != MAX_DELAY && config.getMaxDelay() > -1)
				json.put("maxDelay", config.getMaxDelay());

			double delta = Math.abs(config.getBackoffFactor() - BACKOFF_FACTOR);
			if (config.getBackoffFactor() > 0 && delta > 1e-14)
				json.put("backoffFactor", config.getBackoffFactor());
		}
		return c;
	}
}
