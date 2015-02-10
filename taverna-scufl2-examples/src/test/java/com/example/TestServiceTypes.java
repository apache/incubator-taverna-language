package com.example;

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


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestServiceTypes {
	@Test
	public void wsdlServices() throws Exception {
		File tmp = File.createTempFile("scufl2-ebi-interproscan", ".t2flow");
		tmp.deleteOnExit();
		InputStream ebi = getClass()
				.getResourceAsStream(
						"/workflows/t2flow/ebi_interproscan_for_taverna_2_317472.t2flow");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();

		Set<String> expectedTypes = new HashSet<String>();
		expectedTypes.addAll(Arrays.asList(
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				"http://ns.taverna.org.uk/2010/activity/beanshell",
				"http://ns.taverna.org.uk/2010/activity/wsdl",
				"http://ns.taverna.org.uk/2010/activity/constant"));

		Set<String> types = new ServiceTypes().serviceTypes(new String[] { tmp
				.getAbsolutePath() });
		assertEquals(expectedTypes, types);
	}
	
	@Test
	public void wsdlServicesWfBundle() throws Exception {
		File tmp = File.createTempFile("scufl2-ebi-interproscan", ".wfbundle");
		tmp.deleteOnExit();
		InputStream ebi = getClass()
				.getResourceAsStream(
						"/workflows/wfbundle/ebi_interproscan_for_taverna_2_317472.wfbundle");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();

		Set<String> expectedTypes = new HashSet<String>();
		expectedTypes.addAll(Arrays.asList(
				"http://ns.taverna.org.uk/2010/activity/xml-splitter/in",
				"http://ns.taverna.org.uk/2010/activity/beanshell",
				"http://ns.taverna.org.uk/2010/activity/wsdl",
				"http://ns.taverna.org.uk/2010/activity/constant"));

		Set<String> types = new ServiceTypes().serviceTypes(new String[] { tmp
				.getAbsolutePath() });
		assertEquals(expectedTypes, types);
	}
	
}
