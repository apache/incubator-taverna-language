package org.apache.taverna.scufl2.translator.t2flow.defaultactivities;
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

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ActivityPortDefinitionBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.BasicArtifact;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.BeanshellConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ClassLoaderSharing;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BeanshellActivityParser extends AbstractActivityParser {
	private static final URI activityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/beanshell-activity/");
	private static final URI localWorkerActivityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/localworker-activity/");
	private static final String activityClassName = "net.sf.taverna.t2.activities.beanshell.BeanshellActivity";
	private static final String localWorkerActivityClassName = "net.sf.taverna.t2.activities.localworker.LocalworkerActivity";
	public static final URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	public static final URI LOCAL_WORKER_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/localworker/");
	public static final URI DEPENDENCY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/dependency");
	private static URITools uriTools = new URITools();

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName))
			return true;
		if (activityUriStr.startsWith(localWorkerActivityRavenURI
				.toASCIIString())
				&& activityUriStr.endsWith(localWorkerActivityClassName))
			return true;
		return false;
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState)
			throws ReaderException {
		BeanshellConfig beanshellConfig = unmarshallConfig(t2FlowParser,
				configBean, "xstream", BeanshellConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(ACTIVITY_URI.resolve("#Config"));
		
		if (beanshellConfig.getLocalworkerName() != null) {
			URI localWorkerURI = LOCAL_WORKER_URI.resolve(uriTools
					.validFilename(beanshellConfig.getLocalworkerName()));
			/*
			 * FIXME: As we can't read the annotation chain yet, we can't tell
			 * whether this local worker has been edited or not, and so can't
			 * use #definedBy
			 */
			json.put("derivedFrom", localWorkerURI.toString());
		}

		String script = beanshellConfig.getScript();
		json.put("script", script);

		ClassLoaderSharing classLoaderSharing = beanshellConfig
				.getClassLoaderSharing();
		if (classLoaderSharing == ClassLoaderSharing.SYSTEM)
			json.put("classLoaderSharing", "system");
		else {
		    // default is "workflow" but don't need to be expressed
//		    json.put("classLoaderSharing", "workflow");
		}
 
		if (beanshellConfig.getLocalDependencies() != null) {
			ArrayNode dependencies = json.arrayNode();
			for (String localDep : beanshellConfig.getLocalDependencies()
					.getString())
				dependencies.add(localDep);
			if (dependencies.size() > 0)
				json.put("localDependency", dependencies);
		}

		/*
		 * Note: Maven Dependencies are not supported by Taverna 3 - only here
		 * for informational purposes and potential t2flow->t2flow scenarios
		 */
		if (beanshellConfig.getArtifactDependencies() != null) {
			ArrayNode dependencies = json.arrayNode();
			for (BasicArtifact mavenDep : beanshellConfig
					.getArtifactDependencies()
					.getNetSfTavernaRavenRepositoryBasicArtifact()) {
				ObjectNode mavenDependency = json.objectNode();
				dependencies.add(mavenDependency);
				mavenDependency.put("groupId", mavenDep.getGroupId());
				mavenDependency.put("artifactId", mavenDep.getArtifactId());
				mavenDependency.put("version", mavenDep.getVersion());
			}
			if (dependencies.size() > 0)
				json.put("mavenDependency", dependencies);
		}
		
		Activity activity = parserState.getCurrentActivity();
		activity.getInputPorts().clear();
		activity.getOutputPorts().clear();
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getInputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityInputPortDefinitionBean())
			parseAndAddInputPortDefinition(portBean, configuration, activity);
		for (ActivityPortDefinitionBean portBean : beanshellConfig
				.getOutputs()
				.getNetSfTavernaT2WorkflowmodelProcessorActivityConfigActivityOutputPortDefinitionBean())
			parseAndAddOutputPortDefinition(portBean, configuration, activity);
		return configuration;
	}
}
