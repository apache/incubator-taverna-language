package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class TestProcessorNames {

	@Test
	public void processorNames() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/t2flow/biomartandembossanalysis_904962.t2flow");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow,
				"application/vnd.taverna.t2flow+xml");

		List<String> expected = Arrays.asList("CreateFasta",
				"FlattenImageList", "GetUniqueHomolog", "emma",
				"getHSapSequence", "getMMusSequence", "getRNorSequence",
				"hsapiensGeneEnsembl", "plot", "seqret");
		ProcessorNames processorNames = new ProcessorNames();
		assertEquals(expected, processorNames.showProcessorNames(ro));
		System.out.println(processorNames.showProcessorTree(ro));
	}
	

	@Test
	public void nestedWorkflow() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/t2flow/as.t2flow");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow,
				"application/vnd.taverna.t2flow+xml");
		ProcessorNames processorNames = new ProcessorNames();
		System.out.println(processorNames.showProcessorTree(ro));
	}
	

	@Test
	public void nestedWorkflowBundle() throws JAXBException, IOException, ReaderException {
		InputStream workflow = getClass().getResourceAsStream(
				"/workflows/wfbundle/as.wfbundle");
		assertNotNull(workflow);

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle ro = io.readBundle(workflow, null);
		ProcessorNames processorNames = new ProcessorNames();
		System.out.println(processorNames.showProcessorTree(ro));
	}
	
}
