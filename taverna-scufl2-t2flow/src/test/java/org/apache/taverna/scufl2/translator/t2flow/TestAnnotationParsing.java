package org.apache.taverna.scufl2.translator.t2flow;
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


import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.annotation.Revision;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


public class TestAnnotationParsing {

	private static final String WF_T3_1226 = "/T3-1226-annotations-with-quotes.t2flow";

	private static final String WF_ANNOTATION_WITH_BACKSLASH_T2FLOW = "/annotation_with_backslash.t2flow";

	private static final String WF_RANDOM = "/random.t2flow";
	
	private static final String WF_ANNOTATED = "/annotated2.2.t2flow";
	private static final String SEMANTIC_ANNOTATIONS = "/semantic_annotations__eclipse.t2flow";
	
	
	private static URITools uriTools = new URITools();

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(WF_ANNOTATED);
		assertNotNull("Could not find workflow " + WF_ANNOTATED, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		List<String> expectedRevisions = Arrays.asList(
				"9e1f7ffd-3bf9-4ba8-9c63-03b79b1858ad",
				"bb902d82-b0e4-46fc-bed5-950a3b38bb98");

		List<String> foundRevisions = new ArrayList<String>();

		Revision revision = wfBundle.getMainWorkflow().getCurrentRevision();
		while (revision != null) {
			URI revisionUri = revision.getIdentifier();
			String revisionUUID = uriTools
					.relativePath(Workflow.WORKFLOW_ROOT, revisionUri)
					.toASCIIString().replace("/", "");
			foundRevisions.add(revisionUUID);
			revision = revision.getPreviousRevision();
		}
		assertEquals(expectedRevisions, foundRevisions);

	}

	@Test
	public void readWorkflowWithEscapes() throws Exception {
		URL wfResource = getClass().getResource(WF_ANNOTATION_WITH_BACKSLASH_T2FLOW);
		assertNotNull("Could not find workflow " + WF_ANNOTATION_WITH_BACKSLASH_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Annotation ann = wfBundle.getAnnotations().iterator().next();		
		String annStr  = wfBundle.getResources().getResourceAsString(ann.getBody().toString());
		System.out.println(annStr);
		// """c:\\Program Files\\"""
		assertTrue(annStr.contains("\"\"\"c:\\\\Program Files\\\\\"\"\""));
	}
	
	@Test
	public void readWorkflowWithQuotesInAnnotations() throws Exception {
		URL wfResource = getClass().getResource(WF_T3_1226);
		assertNotNull("Could not find workflow " + WF_T3_1226, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		Workflow wf = wfBundle.getMainWorkflow();
		Collection<Annotation> wfAnnotations = wf.getAnnotations();
		assertEquals(3, wfAnnotations.size());
		for (Annotation ann : wfAnnotations) {
			String content = ann.getRDFContent();
			System.out.println(content);
			if (content.contains("dc/terms/title")) {
				assertTrue("Single 'quote' should not be escaped", content.contains("with 'single quote'"));
			} else if (content.contains("dc/terms/description")) {
				assertTrue("Triple quotes inside should be escaped", content.contains("contains \\\"\\\"\\\"triple quotes\\\"\\\"\\\" inside"));
			} else if (content.contains("elements/1.1/creator")) {
				assertTrue("Unexpected escaping", content.contains("\"\"\"Stian Soiland-Reyes\"\"\""));
			} else {
				fail("Unexpected annotation content: " + content);
			}
		}
		
		
		Collection<Annotation> portAnnotations = wf.getInputPorts().getByName("a").getAnnotations();
		assertEquals(2, portAnnotations.size());
		for (Annotation ann : portAnnotations) {
			String content = ann.getRDFContent();
			System.out.println(content);
			if (content.contains("dc/terms/description")) {
				assertTrue("Quote at start was not escaped", content.contains("description> \"\"\"\\\"quote at the start"));
			} else if (content.contains("attribute/exampleData")) {
				assertTrue("Quote at end was not escaped", content.contains("quote at the end\\\"\"\"\" ."));
			} else {
				fail("Unexpected annotation content: " + content);
			}
		}
		
	}
	

	@Test
	public void readSemanticAnnotations() throws Exception {
		URL wfResource = getClass().getResource(SEMANTIC_ANNOTATIONS);
		assertNotNull("Could not find workflow " + SEMANTIC_ANNOTATIONS, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(false);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		assertEquals(4, wfBundle.getAnnotations().size());
		for (Annotation x : wfBundle.getAnnotations()) {
			System.out.println(x.getTarget());
//			System.out.println(x.getBodyStatements().get(0));
		}
		File f = File.createTempFile("annotation", ".wfbundle");
		System.err.println(f);
		new WorkflowBundleIO().writeBundle(wfBundle, f, "application/vnd.taverna.scufl2.workflow-bundle");
	}


	@Test
	public void workflowWithoutRevisions() throws Exception {
		URL wfResource = getClass().getResource(WF_RANDOM);
		assertNotNull("Could not find workflow " + WF_RANDOM, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());
		List<String> expectedRevisions = Arrays.asList(
				"e87de19a-02c7-4106-ae81-0b8e28efb22c");

		List<String> foundRevisions = new ArrayList<String>();

		Revision revision = wfBundle.getMainWorkflow().getCurrentRevision();
		while (revision != null) {
			URI revisionUri = revision.getIdentifier();
			String revisionUUID = uriTools
					.relativePath(Workflow.WORKFLOW_ROOT, revisionUri)
					.toASCIIString().replace("/", "");
			foundRevisions.add(revisionUUID);
			revision = revision.getPreviousRevision();
		}
		assertEquals(expectedRevisions, foundRevisions);

	}
	
}
