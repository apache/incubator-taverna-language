package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.annotation.Revision;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class TestAnnotationParsing {

	private static final String WF_RANDOM = "/random.t2flow";
	
	private static final String WF_ANNOTATED = "/annotated2.2.t2flow";
	private static final String SEMANTIC_ANNOTATIONS = "/semantic_annotations__eclipse.t2flow";
	
	
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

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
		URL wfResource = getClass().getResource("/annotation_with_backslash.t2flow");
		assertNotNull("Could not find workflow " + WF_ANNOTATED, wfResource);
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
