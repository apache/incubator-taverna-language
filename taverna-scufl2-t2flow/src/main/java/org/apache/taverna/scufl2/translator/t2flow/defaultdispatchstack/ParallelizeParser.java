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

import java.math.BigInteger;
import java.net.URI;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.defaultactivities.AbstractActivityParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ParallelizeConfig;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ParallelizeParser extends AbstractActivityParser {

	private static URI modelRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.core/workflowmodel-impl/");

	private static String className = "net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Parallelize";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Parallelize");

	public static class Defaults {
		public static int maxJobs = 1;
	}

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
		ParallelizeConfig parallelConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", ParallelizeConfig.class);
		Configuration c = new Configuration();
		c.setType(scufl2Uri.resolve("#Config"));

		BigInteger maxJobs = parallelConfig.getMaxJobs();
		if (maxJobs != null && maxJobs.intValue() > 0
				&& maxJobs.intValue() != Defaults.maxJobs) {
			ObjectNode json = (ObjectNode) c.getJson();
			json.put("maxJobs", maxJobs.intValue());
		}
		return c;
	}
}
