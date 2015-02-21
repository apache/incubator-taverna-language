package org.apache.taverna.scufl2.rdfxml;
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


import static org.apache.taverna.scufl2.rdfxml.TestRDFXMLWriter.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("resource")
public class TestResourcesInZip {

	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	protected ExampleWorkflow exampleWorkflow = new ExampleWorkflow();
	protected WorkflowBundle originalBundle;


	@Before
	public void makeBundle() {
		originalBundle = exampleWorkflow.makeWorkflowBundle();
	}

	@Test
	public void singleFile() throws Exception {
		UCFPackage resources = originalBundle.getResources();
		assertEquals(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE, resources.getPackageMediaType());
		resources.addResource("Hello there", "hello.txt", "text/plain");
		File bundleFile = tempFile();
		bundleIO.writeBundle(originalBundle, bundleFile, APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);		
		assertEquals(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE, resources.getPackageMediaType());
		assertEquals(1, resources.getRootFiles().size());
		assertEquals("workflowBundle.rdf", resources.getRootFiles().get(0).getPath());
		
		ZipFile zipFile = new ZipFile(bundleFile);
		ZipEntry hello = zipFile.getEntry("hello.txt");
		assertEquals("hello.txt", hello.getName());
		assertEquals("Hello there",
				IOUtils.toString(zipFile.getInputStream(hello), "ASCII"));		
	}
	
	@Test
	public void differentMediaType() throws Exception {
		UCFPackage resources = originalBundle.getResources();
		resources.setPackageMediaType("application/x-something-else");
		assertEquals("application/x-something-else", resources.getPackageMediaType());
		
		resources.addResource("Hello there", "hello.txt", "text/plain");
		File bundleFile = tempFile();
		bundleIO.writeBundle(originalBundle, bundleFile, APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
		assertEquals("application/x-something-else", resources.getPackageMediaType());
		assertEquals(0, resources.getRootFiles().size());
		// RDFXMLWriter does not touch the rootFile or media type if it's non-null
		
		ZipFile zipFile = new ZipFile(bundleFile);
		ZipEntry hello = zipFile.getEntry("hello.txt");
		assertEquals("hello.txt", hello.getName());
		assertEquals("Hello there",
				IOUtils.toString(zipFile.getInputStream(hello), "ASCII"));		
	}
	
	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("test", ".scufl2");
		bundleFile.deleteOnExit();
		//System.out.println(bundleFile);
		return bundleFile;
	}
}
