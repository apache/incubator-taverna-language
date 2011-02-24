package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestRDFXMLReader {

	private URL exampleBundle;
	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	protected WorkflowBundle workflowBundle;
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO testWorkflowBundleIO = new uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO();

	@Test
	public void sameBaseAs() throws Exception {
		assertEquals("http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/", workflowBundle.getSameBaseAs().toASCIIString());
	}

	@Test
	public void bundleName() throws Exception {
		assertEquals("HelloWorld", workflowBundle.getName());
	}

	
	@Before
	public void exampleBundle() throws ReaderException, IOException {
		String name = "example.scufl2";
		exampleBundle = getClass().getResource(name);
		assertNotNull("Can't find example workflow bundle " + name,
				exampleBundle);
		workflowBundle = bundleIO.readBundle(exampleBundle,
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}

	@Test
	public void dispatchStack() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(
				"http://ns.taverna.org.uk/2010/taverna/2.2/DefaultDispatchStack",
				hello.getDispatchStack().getType().toASCIIString());
		assertEquals(7, hello.getDispatchStack().size());
		assertEquals("http://ns.taverna.org.uk/2010/scufl2/taverna#Failover",
				hello.getDispatchStack().get(3).getConfigurableType()
						.toASCIIString());
	}

	@Test
	public void dispatchStackWait4Me() throws Exception {
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		assertEquals(
				"http://ns.taverna.org.uk/2010/scufl2/taverna#defaultDispatchStack",
				wait4me.getDispatchStack().getType().toASCIIString());
		assertEquals(0, wait4me.getDispatchStack().size());
	}

	@Test
	public void processors() throws Exception {
		Set<String> expected = new HashSet<String>();
		expected.add("Hello");
		expected.add("wait4me");
		assertEquals(expected, workflowBundle.getMainWorkflow().getProcessors()
				.getNames());
	}

	@Test
	public void workflows() throws Exception {
		assertEquals(1, workflowBundle.getWorkflows().size());
		Workflow helloWorld = workflowBundle.getWorkflows().getByName(
				"HelloWorld");
		assertEquals("HelloWorld", helloWorld.getName());
		assertEquals(workflowBundle.getMainWorkflow(), helloWorld);
	}

	@Test
	public void profiles() throws Exception {
		assertEquals(2, workflowBundle.getProfiles().size());
		Profile tavernaWorkbench = workflowBundle.getProfiles().getByName(
				"tavernaWorkbench");
		assertEquals("tavernaWorkbench", tavernaWorkbench.getName());
		assertEquals(workflowBundle.getMainProfile(), tavernaWorkbench);		
		assertEquals("tavernaServer", workflowBundle.getProfiles().getByName("tavernaServer").getName());
	}
	
	@Test
	public void workflowIdentifier() throws Exception {
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/",
				workflowBundle.getMainWorkflow().getWorkflowIdentifier()
						.toASCIIString());
	}

	@Test
	public void processorInputPorts() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(1, hello.getInputPorts().size());
		assertEquals("name", hello.getInputPorts().getByName("name").getName());
		assertEquals(0, hello.getInputPorts().getByName("name").getDepth()
				.intValue());
	}

	@Test
	public void workflowInputPorts() throws Exception {
		NamedSet<InputWorkflowPort> inputPorts = workflowBundle
				.getMainWorkflow().getInputPorts();
		assertEquals(1, inputPorts.size());
		assertEquals("yourName", inputPorts.getByName("yourName").getName());
		assertEquals(0, inputPorts.getByName("yourName").getDepth().intValue());
	}

	@Test
	public void workflowOutputPorts() throws Exception {
		NamedSet<OutputWorkflowPort> outputPorts = workflowBundle
				.getMainWorkflow().getOutputPorts();
		assertEquals(1, outputPorts.size());
		assertEquals("results", outputPorts.getByName("results").getName());
	}

	@Test
	public void processorPortsWait4Me() throws Exception {
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		assertEquals(0, wait4me.getInputPorts().size());
		assertEquals(0, wait4me.getOutputPorts().size());
	}

	@Test
	public void processorOutputPorts() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(1, hello.getOutputPorts().size());
		assertEquals("greeting", hello.getOutputPorts().getByName("greeting")
				.getName());
		assertEquals(0, hello.getOutputPorts().getByName("greeting").getDepth()
				.intValue());
		assertEquals(0, hello.getOutputPorts().getByName("greeting")
				.getGranularDepth().intValue());
	}

	@Test
	public void iterationStrategy() throws Exception {
		Processor hello = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertEquals(1, hello.getIterationStrategyStack().size());
		IterationStrategyTopNode iterationStrategyTopNode = hello
				.getIterationStrategyStack().get(0);
		assertTrue(iterationStrategyTopNode instanceof CrossProduct);
		CrossProduct cross = (CrossProduct) iterationStrategyTopNode;
		assertEquals(1, cross.size());
		PortNode portNode = (PortNode) cross.get(0);
		assertEquals(hello.getInputPorts().getByName("name"),
				portNode.getInputProcessorPort());
		assertEquals(0, portNode.getDesiredDepth().intValue());
	}

	@Test
	public void iterationStrategyWait4Me() throws Exception {
		Processor wait4me = workflowBundle.getMainWorkflow().getProcessors()
				.getByName("wait4me");
		assertEquals(0, wait4me.getIterationStrategyStack().size());
	}

	@Test
	public void testParsedWorkflow() throws Exception {
		assertEquals("HelloWorld", workflowBundle.getName());

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(workflowBundle, output,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);

		String bundleTxt = new String(output.toByteArray(), "UTF-8");

		assertEquals(testWorkflowBundleIO.getStructureFormatWorkflowBundle(),
				bundleTxt);

	}

	@Ignore
	@Test
	public void xmlOutput() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		new RDFXMLSerializer(workflowBundle).workflowDoc(output,
				workflowBundle.getMainWorkflow(),
				URI.create("workflows/HelloWorld.rdf"));
		String bundleTxt = new String(output.toByteArray(), "UTF-8");
		System.out.println(bundleTxt);

	}

}
