package org.apache.taverna.scufl2.api.common;

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
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.UUID;

import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.TestWorkflowBundleIO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class TestURITools {

	private URITools uriTools = new URITools();
	private WorkflowBundle wfBundle;

	@Before
	public void makeExampleWorkflow() {
		wfBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}

	@Test
	public void relativizeNotSameFolder() {
		URI base = URI.create("workflow/Workflow.rdf");
		URI uri = URI.create("profile/Profile/");
		assertEquals("../profile/Profile/", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeNotSameFolderAbsolute() {
		URI base = URI.create("/workflow/Workflow.rdf");
		URI uri = URI.create("/profile/Profile/");
		assertEquals("../profile/Profile/", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeNotSameFolderComplete() {
		URI base = URI.create("http://example.com/workflow/Workflow.rdf");
		URI uri = URI.create("http://example.com/profile/Profile/");
		assertEquals("../profile/Profile/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSameFolder() {
		URI base = URI.create("workflow/Workflow.rdf");
		URI uri = URI.create("workflow/Folder/");
		assertEquals("Folder/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSameFolderAbsoulute() {
		URI base = URI.create("/workflow/Workflow.rdf");
		URI uri = URI.create("/workflow/Folder/");
		assertEquals("Folder/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSameFolderComplete() {
		URI base = URI.create("http://example.com/workflow/Workflow.rdf");
		URI uri = URI.create("http://example.com/workflow/Folder/");
		assertEquals("Folder/", uriTools.relativePath(base, uri)
				.toASCIIString());
	}

	@Test
	public void relativizeSamePath() {
		URI base = URI.create("workflow/../workflow//Workflow.rdf#");
		URI uri = URI.create("workflow/Workflow.rdf#fish");
		assertEquals("#fish", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeSamePathAbsolute() {
		URI base = URI.create("/workflow/../workflow//Workflow.rdf#");
		URI uri = URI.create("/workflow/Workflow.rdf#fish");
		assertEquals("#fish", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relativizeSamePathComplete() {
		URI base = URI
				.create("http://example.com/workflow/../workflow//Workflow.rdf#");
		URI uri = URI
.create("http://example.com/workflow/Workflow.rdf#fish");
		assertEquals("#fish", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relatizeSame() throws Exception {
		URI base = URI.create("workflow/HelloWorld.rdf");
		URI uri = URI.create("workflow/HelloWorld/");
		assertEquals("HelloWorld/", uriTools.relativePath(base, uri).toASCIIString());
	}

	@Test
	public void relatizeToFragment() throws Exception {
		URI base = URI.create("filename.txt");
		URI fragment = URI.create("./#sd");
		assertEquals(".#sd", uriTools.relativePath(base, fragment)
				.toASCIIString());
	}

	@Ignore("Not so critical to fix for now..")
	@Test
	public void relatizeToSlash() throws Exception {
		URI base = URI.create("filename.txt");
		URI fragment = URI.create("./");
		assertEquals("./", uriTools.relativePath(base, fragment)
				.toASCIIString());
	}

	@Test
	public void validFileName() {
		assertEquals(
				"f%2fsd%5Csdf'asd%20fa%3as&%3F%3dd%20%C6%92%C3%AB%E2%88%9A%C3%A6%E2%80%9A%C3%84%C3%B9%C2%AC%C2%AA%E2%88%9A%C2%B6",
				uriTools.validFilename("f/sd\\sdf'asd fa:s&?=d \u0192\u00eb\u221a\u00e6\u201a\u00c4\u00f9\u00ac\u00aa\u221a\u00b6")); 
		// f/sd\sdf'asd fa:s&?=d  ƒë√æ‚Äù¬ª√¶  -- double check your Maven/Eclipse is using utf-8
	}


	@Test(expected=NullPointerException.class)
	public void validFileNameNull() {		
		uriTools.validFilename(null);
	}
	
	@Test
	public void wfBundle() {
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/",
				wfBundle.getGlobalBaseURI().toASCIIString());
		assertEquals(wfBundle.getGlobalBaseURI(), uriTools.uriForBean(wfBundle));
		String uuidPath = uriTools.relativePath(
				WorkflowBundle.WORKFLOW_BUNDLE_ROOT, wfBundle.getGlobalBaseURI())
				.getPath();
		assertTrue(uuidPath.endsWith("/"));
		// Should be a valid uuid
		UUID.fromString(uuidPath.substring(0, uuidPath.length() - 1));
	}

	@Test
	public void wfBundleSelfRelative() {
		assertEquals(URI.create(""),
				uriTools.relativeUriForBean(wfBundle, wfBundle));
	}

	@Test
	public void workflow() {
		Workflow wf = wfBundle.getMainWorkflow();
		URI wfUri = uriTools.uriForBean(wf);
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/",
				wfUri.toASCIIString());
		URI namePath = uriTools.relativeUriForBean(wf, wfBundle);
		assertEquals("workflow/HelloWorld/", namePath.toASCIIString()); // wf.getName();
	}

	@Test
	public void workflowIdentifier() {
		Workflow wf = wfBundle.getMainWorkflow();
		String uuidPath = uriTools.relativePath(Workflow.WORKFLOW_ROOT,
				wf.getIdentifier()).getPath();
		assertTrue(uuidPath.endsWith("/"));
		// Should be a valid uuid
		UUID.fromString(uuidPath.substring(0, uuidPath.length() - 1));
	}

}
