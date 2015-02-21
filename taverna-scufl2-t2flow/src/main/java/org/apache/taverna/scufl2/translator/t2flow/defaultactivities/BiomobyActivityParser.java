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

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;

public class BiomobyActivityParser extends AbstractActivityParser {
	private static URI activityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/biomoby-activity/");
	private static String mobyObjectClassName = "net.sf.taverna.t2.activities.biomoby.BiomobyObjectActivity";
	private static String mobyServiceClassName = "net.sf.taverna.t2.activities.biomoby.BiomobyActivity";
	public static URI mobyObjectScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/biomoby/object");
	public static URI mobyServiceScufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/biomoby/service");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		if (!activityUriStr.startsWith(activityRavenURI.toASCIIString()))
			return false;
		return activityUriStr.endsWith(mobyObjectClassName)
				|| activityUriStr.endsWith(mobyServiceClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		String activityUriStr = t2flowActivity.toASCIIString();
		if (activityUriStr.endsWith(mobyObjectClassName))
			return mobyObjectScufl2Uri;
		else
			return mobyServiceScufl2Uri;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean, ParserState parserState) {
		// TODO Auto-generated method stub
		return null;
	}
}
