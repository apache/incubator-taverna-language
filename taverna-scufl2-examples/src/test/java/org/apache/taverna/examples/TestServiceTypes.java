package org.apache.taverna.examples;

/*
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
 */


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestServiceTypes {
	@Test
	public void defaultActivitiesT2flow() throws Exception {
		File tmp = File.createTempFile("defaultActivities2.2", ".t2flow");
		tmp.deleteOnExit();
		InputStream ebi = getClass()
				.getResourceAsStream(
						"/workflows/t2flow/defaultActivitiesTaverna2.2.t2flow");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();

		Set<String> expectedTypes = new HashSet<String>();
		expectedTypes.addAll(Arrays.asList(
				"http://ns.taverna.org.uk/2010/activity/beanshell",
				"http://ns.taverna.org.uk/2010/activity/nested-workflow",
				"http://ns.taverna.org.uk/2010/activity/rshell",
				"http://ns.taverna.org.uk/2010/activity/spreadsheet-import",
				"http://ns.taverna.org.uk/2010/activity/constant",
				"http://ns.taverna.org.uk/2010/activity/apiconsumer",
				"http://ns.taverna.org.uk/2010/activity/biomart",
				"http://ns.taverna.org.uk/2010/activity/biomoby/object",
				"http://ns.taverna.org.uk/2010/activity/biomoby/service",
				"http://ns.taverna.org.uk/2010/activity/wsdl",
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/out",
				"http://ns.taverna.org.uk/2010/activity/soaplab"));

		Set<String> types = new ServiceTypes().serviceTypes(new String[] { tmp
				.getAbsolutePath() });
//		List<String> expected = new ArrayList<>(expectedTypes);
//		List<String> actual = new ArrayList<>(types);
//		Collections.sort(expected);
//		Collections.sort(actual);
//		assertEquals(expected.toString(), actual.toString());
		assertEquals(expectedTypes, types);
	}
	
	@Test
	public void defaultActivitiesWfBundle() throws Exception {
		File tmp = File.createTempFile("defaultActivities2.2", ".wfbundle");
		tmp.deleteOnExit();
		InputStream ebi = getClass()
				.getResourceAsStream(
						"/workflows/wfbundle/defaultActivitiesTaverna2.wfbundle");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();

		Set<String> expectedTypes = new HashSet<String>();
		expectedTypes.addAll(Arrays.asList(
				"http://ns.taverna.org.uk/2010/activity/beanshell",
				"http://ns.taverna.org.uk/2010/activity/nested-workflow",
				"http://ns.taverna.org.uk/2010/activity/rshell",
				"http://ns.taverna.org.uk/2010/activity/spreadsheet-import",
				"http://ns.taverna.org.uk/2010/activity/constant",
				"http://ns.taverna.org.uk/2010/activity/apiconsumer",
				"http://ns.taverna.org.uk/2010/activity/biomart",
				"http://ns.taverna.org.uk/2010/activity/biomoby/object",
				"http://ns.taverna.org.uk/2010/activity/biomoby/service",
				"http://ns.taverna.org.uk/2010/activity/wsdl",
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/out",
				"http://ns.taverna.org.uk/2010/activity/soaplab"));

		Set<String> types = new ServiceTypes().serviceTypes(new String[] { tmp
				.getAbsolutePath() });
		assertEquals(expectedTypes, types);
	}
	
}
