package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;

public class TestURIToolsBeans {

	private static final String BUNDLE_URI = "http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/";
	private URITools uriTools = new URITools();
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private WorkflowBundle wfBundle;

	@Before
	public void makeExampleWorkflow() {
		wfBundle = new TestWorkflowBundleIO().makeWorkflowBundle();
	}

	@Test
	public void uriForDatalink() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		List<DataLink> nameLinks = scufl2Tools.datalinksTo(hello
				.getInputPorts()
				.getByName("name"));
		URI uri = uriTools.uriForBean(nameLinks.get(0));
		assertEquals(
				BUNDLE_URI
						+ "workflow/HelloWorld/datalink?from=in/yourName&to=processor/Hello/in/name",
				uri.toASCIIString());

		// assertEquals(BUNDLE_URI + "workflow/HelloWorld/out/results/",
		// uri.toASCIIString());
	}

	@Test
	public void uriForDatalinkWithMerge() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		List<DataLink> greetingLinks = scufl2Tools.datalinksFrom(hello
				.getOutputPorts()
				.getByName("greeting"));
		URI uri = uriTools.uriForBean(greetingLinks.get(0));
		assertEquals(
				BUNDLE_URI
						+ "workflow/HelloWorld/datalink?from=processor/Hello/out/greeting&to=out/results&mergePosition=0",
				uri.toASCIIString());


	}


	@Test
	public void uriForProcessor() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getProcessors().getByName("Hello"));
		assertEquals(BUNDLE_URI + "workflow/HelloWorld/processor/Hello/",
				uri.toASCIIString());
	}

	@Test
	public void uriForProcessorInPort() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getProcessors().getByName("Hello").getInputPorts()
				.getByName("name"));
		assertEquals(BUNDLE_URI
 + "workflow/HelloWorld/processor/Hello/in/name",
				uri.toASCIIString());
	}

	@Test
	public void uriForProcessorOutPort() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getProcessors().getByName("Hello").getOutputPorts()
				.getByName("greeting"));
		assertEquals(BUNDLE_URI
				+ "workflow/HelloWorld/processor/Hello/out/greeting",
				uri.toASCIIString());
	}

	@Test
	public void uriForProfile() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainProfile());
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/" + "",
				uri.toASCIIString());
	}

	@Test
	public void uriForWfBundle() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle);
		assertEquals(BUNDLE_URI, uri.toASCIIString());
	}

	@Test
	public void uriForWorkflow() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow());
		assertEquals(BUNDLE_URI + "workflow/HelloWorld/",
 uri.toASCIIString());
	}

	@Test
	public void uriForWorkflowInPort() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getInputPorts().getByName("yourName"));
		assertEquals(BUNDLE_URI + "workflow/HelloWorld/in/yourName",
				uri.toASCIIString());
	}


	@Test
	public void uriForWorkflowOutPort() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getOutputPorts().getByName("results"));
		assertEquals(BUNDLE_URI + "workflow/HelloWorld/out/results",
				uri.toASCIIString());
	}

}
